package com.mpole.hdt.digitaltwin.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpole.hdt.digitaltwin.application.telemetry.TelemetryIngestService;
import com.mpole.hdt.digitaltwin.application.telemetry.TelemetryRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryRawConsumer {
    private final ObjectMapper objectMapper;
    private final TelemetryIngestService ingestService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "${hdt.kafka.topics.telemetry-raw}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) throws Exception {
        try {
            log.info("[DT] raw telemetry: {}", message);

            TelemetryRaw raw = objectMapper.readValue(message, TelemetryRaw.class);
            ingestService.ingest(raw);

            messagingTemplate.convertAndSend("/sub/digitaltwin/all", message);
        } catch (Exception e) {
            log.error("Kafka message parse/ingest failed. payload={}", message, e);
        }
    }
}
