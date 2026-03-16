package com.mpole.hdt.gateway.ingestion.adapter;

import com.mpole.hdt.gateway.infrastructure.external.dto.MessageType;
import com.mpole.hdt.gateway.infrastructure.external.dto.StandardBodyItem;
import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import com.mpole.hdt.gateway.infrastructure.external.dto.StandardHeader;
import com.mpole.hdt.gateway.ingestion.service.IntegrationSiIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 내부 스케줄러용(DB)
 * 통합SI DB를 엠폴에서 직접 조회하는 방식 (표준 API 형태 지원 어려울 경우 대안용)
 * 순서 1
 * 이 클래스는 JDBC를 사용해서 통합SI DB(MS-SQL 등)에 직접 접속합니다.
 * "1초마다(또는 설정된 주기마다)" 쿼리를 날려서 "변경된 데이터만" 긁어오는(Pull) 역할을 전담합니다.
 * API 호출이 아니라, DB 조회를 수행하는 공급책입니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class SiDatabasePollingAdapter {
    private final JdbcTemplate jdbcTemplate;
    private final IntegrationSiIngestService ingestService;

    // 서버 시작 시점 기준 1분 전 데이터부터 조회 시작
    private LocalDateTime lastReadTime = LocalDateTime.now().minusMinutes(1);

    private volatile long lastId = 0L; // 시작 커서(원하면 DB/파일에서 로드)
    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile long statWindowStartMs = System.currentTimeMillis();
    private volatile long statRowsInWindow = 0L;      // row 기준
    private volatile long statMsgsInWindow = 0L;      // kafka 메시지(envelope) 기준
    private volatile long statTotalRows = 0L;         // 누적 row

    private volatile String currentMode = "POSTGRE"; // 기본값 POSTGRE

    @Value("${hdt.poll.batch-size:6000}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${hdt.poll.delay-ms:5}")
    public void pollRouter() {

            pollPostgres(); // 기존 100만 건 부하테스트 코드 호출

    }

    public void pollPostgres() {
        if (!running.compareAndSet(false, true)) return;
        try {
            // 핵심 변경 1: si_point_raw 테이블 구조에 맞게 쿼리 수정 (ID 컬럼명은 캡처본 기준 raw_id 로 가정)
            String sql = """
                SELECT raw_id, sub_if_type, device_code, point_code, object_type, value_raw, update_datetime
                FROM core.si_point_raw
                WHERE raw_id > ?
                ORDER BY raw_id ASC
                LIMIT ?
            """;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, lastId, batchSize);

            Mono<Void> work;
            if (rows.isEmpty()) {
                // 핵심 변경 2: 데이터가 끝나면 다시 처음(0)부터 읽도록 무한 루프 세팅
//                log.info("모든 데이터를 전송했습니다. 시뮬레이션을 위해 lastId를 0으로 초기화합니다.");
                lastId = 0L;
                work = Mono.empty();
            } else {
                long maxId = rows.stream()
                        .mapToLong(r -> ((Number) r.get("raw_id")).longValue())
                        .max().orElse(lastId);

                // 핵심 변경 3: 데이터를 카프카로 쏠 때마다 시간을 '현재 시간'으로 위조합니다.
                String simulatedNow = OffsetDateTime.now().toString();

                List<StandardBodyItem> items = rows.stream().map(r -> {
                    return StandardBodyItem.builder()
                            .systemType(String.valueOf(r.get("sub_if_type")))
                            .deviceCode(String.valueOf(r.get("device_code")))
                            .pointCode(String.valueOf(r.get("point_code")))
                            .pointType(String.valueOf(r.get("object_type")))
                            .value(String.valueOf(r.get("value_raw")))
                            .soureTimestamp(simulatedNow) // 위조된 현재 시간 세팅
                            .build();
                }).toList();

                StandardEnvelope<Object> envelope = buildEnvelope(items);

                work = ingestService.ingest(envelope)
                        .doOnSuccess(v -> {
                            lastId = maxId; // 전송 성공 시 커서 전진

                            int rowsSent = items.size();
                            statRowsInWindow += rowsSent;
                            statMsgsInWindow += 1;
                            statTotalRows += rowsSent;

                            long now = System.currentTimeMillis();
                            long elapsedMs = now - statWindowStartMs;

                            if (elapsedMs >= 1000) {
                                double sec = elapsedMs / 1000.0;
                                long rowPerSec = (long) (statRowsInWindow / sec);
                                long msgPerSec = (long) (statMsgsInWindow / sec);

                                log.info("[시뮬레이터 처리량] 초당 {}건 전송 / 누적 {}건 / lastId={}",
                                        String.format("%,d", rowPerSec),
                                        String.format("%,d", statTotalRows),
                                        lastId);

                                statWindowStartMs = now;
                                statRowsInWindow = 0L;
                                statMsgsInWindow = 0L;
                            }
                        });
            }

            work.doOnError(e -> log.error("poll ingest failed", e))
                    .doFinally(sig -> running.set(false))
                    .subscribe();
        } catch (Exception e) {
            running.set(false);
            log.error("poll failed", e);
        }
    }


    private StandardEnvelope buildEnvelope(List<StandardBodyItem> rows) {

        StandardHeader header = StandardHeader.builder()
                .trxId(UUID.randomUUID().toString())
                .timestamp(OffsetDateTime.now().toString())
                .senderId("INTEGRATION_SI_POLLING")
                .schemaVersion("1.0")
                .messageType(MessageType.UPDATE)
                .build();

        return StandardEnvelope.builder()
                .header(header)
                .body(rows)
                .build();
    }
}
