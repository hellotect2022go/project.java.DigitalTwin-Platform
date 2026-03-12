package com.mpole.hdt.digitaltwin.ingestion.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpole.hdt.digitaltwin.websocket.config.RawWebsocketHandler;
import com.mpole.hdt.digitaltwin.config.initializer.DeviceCache;
import com.mpole.hdt.digitaltwin.ingestion.TelemetryIngestService;
import com.mpole.hdt.digitaltwin.ingestion.TelemetryRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryRawConsumer {
    private final ObjectMapper objectMapper;
    private final TelemetryIngestService ingestService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RawWebsocketHandler rawWebsocketHandler;

//    @KafkaListener(
//            topics = "${hdt.kafka.topics.telemetry-raw}",
//            groupId = "debug-group-${random.uuid}"
//            //groupId = "${spring.kafka.consumer.group-id}"
//            )
    public void onMessage(byte[] message) throws Exception {
        try {
            //log.info("[DT] raw telemetry: {}", message);

            TelemetryRaw raw = objectMapper.readValue(message, TelemetryRaw.class);
            List<DeviceCache.EnrichedTelemetry> list = ingestService.ingest(raw);


            //messagingTemplate.convertAndSend("/sub/digitaltwin/all", list);
            rawWebsocketHandler.broadcast(list);
        } catch (Exception e) {
            log.error("Kafka message parse/ingest failed. payload={}", message, e);
        }
    }
}
