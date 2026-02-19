package com.mpole.hdt.gateway.ingestion.controller;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import com.mpole.hdt.gateway.ingestion.service.IntegrationSiIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ingest")
public class IngestController {
    private final IntegrationSiIngestService integrationSiIngestService;

    /**
     * 통합 SI / 테스트용 이벤트 수신 진입점
     * - Micro-batch(List) 기반
     * - 표준 Envelope(header/body/result) 강제
     * - HTTP 입구
     */
    @PostMapping("/events")
    public Mono<ResponseEntity<Void>> ingest(@RequestBody StandardEnvelope envelope) {
        // Service가 검증/보정/전달을 모두 담당
        return integrationSiIngestService.ingest(envelope)
                .thenReturn(ResponseEntity.accepted().build());
    }
}


//[통합 SI]
//        ↓ HTTP
//        IngestController
//   ↓
//IntegrationSiIngestService
//   - normalize (trxId/timestamp)
//   - IngestValidator.validate()
//   - 메트릭: ingest.accept / reject / records
//   ↓
//KafkaEventPublisher
//   - Kafka publish
//   - Header: X-Trx-Id
//   - 메트릭: publish.success / fail / latency
//   ↓
//Kafka Topic (hdt.telemetry.raw)