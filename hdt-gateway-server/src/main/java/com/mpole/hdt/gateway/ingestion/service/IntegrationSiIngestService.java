package com.mpole.hdt.gateway.ingestion.service;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import com.mpole.hdt.gateway.ingestion.publisher.EventPublisher;
import com.mpole.hdt.gateway.ingestion.validation.IngestValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 검증/보정/공통 ingest처리 담당
 * 데이터가 DB에서 왔든(Polling), API로 들어왔든(Push) 상관없이 "검증하고 전송하는 로직"을 한 곳에서 관리하기 위함입니다.
 * Service는 "헤더 확인하고, UUID 없으면 붙이고" 나서 EventPublisher를 통해 이벤트 서버로 전달
 * 공통 ingest 정책
 */

@Slf4j
@Service
@RequiredArgsConstructor
    public class IntegrationSiIngestService {
    private final EventPublisher<Object> eventPublisher;

    private final MeterRegistry meterRegistry;
    private Counter ingestAccept;
    private Counter ingestReject;
    private DistributionSummary ingestRecords;

    @PostConstruct
    void initMetrics() {
        this.ingestAccept = Counter.builder("hdt.gateway.ingest.accept").register(meterRegistry);
        this.ingestReject = Counter.builder("hdt.gateway.ingest.reject").register(meterRegistry);
        this.ingestRecords = DistributionSummary.builder("hdt.gateway.ingest.records")
                .baseUnit("records")
                .register(meterRegistry);
    }

    public Mono<Void> ingest(StandardEnvelope<Object> envelope) {

        normalizeEnvelope(envelope);

        // 들어온 데이터가 추적 가능하도록 보장
        String trxId = envelope.getHeader() != null ? envelope.getHeader().getTrxId() : "UNKNOWN";

        // 2) 검증
        IngestValidator.ValidationResult vr = IngestValidator.validate(envelope);
        if (!vr.isOk()) {
            log.warn("[Ingest Reject] trxId={} reason={}", trxId, vr.getMessage());
            return Mono.error(new IllegalArgumentException("Invalid envelope: " + vr.getMessage()));
        }

        int size = envelope.getBody().size();

        ingestAccept.increment();     // accept 메트릭
        ingestRecords.record(size);   // records 메트릭

        log.debug("[Ingest] accepted trxId={} records={}", trxId, size);

        // 3. Publisher로 전송 (비동기)
        return eventPublisher.publish(envelope);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void normalizeEnvelope(StandardEnvelope envelope) {
        if (envelope == null) return;
        if (envelope.getHeader() == null) return;
        if (envelope.getBody() == null) return;

        // 추적 ID(TrxId) 보정, trxId 없으면 생성
        if (envelope.getHeader().getTrxId() == null || envelope.getHeader().getTrxId().trim().isEmpty()) {
            envelope.getHeader().setTrxId(UUID.randomUUID().toString());
        }

        // timestamp 없으면 현재시각으로 보정(정책)
        if (envelope.getHeader().getTimestamp() == null || envelope.getHeader().getTimestamp().trim().isEmpty()) {
            envelope.getHeader().setTimestamp(OffsetDateTime.now().toString());
        }

        // senderId/schemaVersion은 “보정” 대신 “필수로 강제”가 더 안전함
        // -> validate에서 필수 체크로 걸러짐
    }
}