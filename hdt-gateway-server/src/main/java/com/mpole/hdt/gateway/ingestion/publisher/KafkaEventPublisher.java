package com.mpole.hdt.gateway.ingestion.publisher;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher<Object> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    @Value("${hdt.kafka.topic.raw:hdt-telemetry-raw}")
    private String topic;

    private Counter publishSuccess;
    private Counter publishFail;
    private Timer publishLatency;

    @PostConstruct
    void initMetrics() {
        this.publishSuccess = Counter.builder("hdt.gateway.publish.success").register(meterRegistry);
        this.publishFail = Counter.builder("hdt.gateway.publish.fail").register(meterRegistry);
        this.publishLatency = Timer.builder("hdt.gateway.publish.latency").register(meterRegistry);
    }

    @Override
    public Mono<Void> publish(StandardEnvelope<Object> envelope) {
        return Mono.defer(() -> {

            if (envelope == null || envelope.getHeader() == null) {
                return Mono.error(new IllegalArgumentException("envelope/header is required"));
            }

            String trxId = (envelope.getHeader() != null && envelope.getHeader().getTrxId() != null)
                    ? envelope.getHeader().getTrxId()
                    : "";

            if (trxId.isEmpty()) {
                return Mono.error(new IllegalArgumentException("header.trxId is required (must be normalized before publish)"));
            }

            // key 정책(예: trxId or device_code). 지금은 안전하게 trxId로.
            String key = trxId;

            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, envelope);
            record.headers().add("X-Trx-Id", trxId.getBytes(StandardCharsets.UTF_8));

            long startNs = System.nanoTime();

            return Mono.fromFuture(kafkaTemplate.send(record).toCompletableFuture())
                    .doOnSuccess(r -> {
                        publishSuccess.increment();
                        publishLatency.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
                        log.debug("[Kafka Publish OK] trxId={} topic={} partition={}",
                                trxId, topic, r.getRecordMetadata().partition());
                    })
                    .doOnError(e -> {
                        publishFail.increment();
                        log.error("[Kafka Publish Fail] trxId={} topic={}", trxId, topic, e);
                    })
                    .doFinally(sig ->
                            publishLatency.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS)
                    )
                    .then();

        });
    }
}
