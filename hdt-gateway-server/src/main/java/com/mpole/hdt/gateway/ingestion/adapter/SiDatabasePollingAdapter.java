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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @Value("${hdt.poll.batch-size:3000}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${hdt.poll.delay-ms:10}")
    public void poll() {
        if (!running.compareAndSet(false, true)) return;

        try {
            String sql = """
            SELECT id, device_id, point_id, event_time, received_time, value_num, value_str, payload
            FROM core.test_telemetry_src
            WHERE id > ?
            ORDER BY id
            LIMIT ?
        """;

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, lastId, batchSize);

            Mono<Void> work;
            if (rows.isEmpty()) {
                // 빈 배치도 체인으로 보내야 doFinally가 running을 풀어줌
                log.debug("poll empty: lastId={}", lastId);
                work = Mono.empty();
            } else {
                long maxId = rows.stream()
                        .mapToLong(r -> ((Number) r.get("id")).longValue())
                        .max().orElse(lastId);

                List<StandardBodyItem> items = rows.stream().map(r -> {
                    String deviceId = String.valueOf(r.get("device_id"));
                    String pointId  = String.valueOf(r.get("point_id"));

                    Object vNum = r.get("value_num");
                    Object vStr = r.get("value_str");

                    String value = (vNum != null) ? String.valueOf(vNum)
                            : (vStr != null) ? String.valueOf(vStr)
                            : (r.get("payload") != null) ? String.valueOf(r.get("payload"))
                            : null;

                    String ts = (r.get("event_time") != null) ? String.valueOf(r.get("event_time"))
                            : (r.get("received_time") != null) ? String.valueOf(r.get("received_time"))
                            : null;

                    return StandardBodyItem.builder()
                            .deviceCode(deviceId)
                            .pointCode(pointId)
                            .value(value)
                            .soureTimestamp(ts)
                            .build();
                }).toList();

                StandardEnvelope<Object> envelope = buildEnvelope(items); // 아래 2)에서 통일 추천
                String trxId = envelope.getHeader().getTrxId();

                work = ingestService.ingest(envelope)
                        .doOnSuccess(v -> {
                            lastId = maxId; // 성공한 경우에만 커서 전진
                            log.info("poll success: trxId={} sent={} lastId={}", trxId, items.size(), lastId);
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

    // 1초마다 실행 (통합SI DB 변경분 조회)
//    @Scheduled(fixedDelay = 1000)
//    public void pollChanges() {
//        try {
//            LocalDateTime queryTime = LocalDateTime.now();
//
//            // [SQL] 변경분 조회 (View 이름과 컬럼명은 SI 협의 내용에 따라 수정 필요)
//            // 예: device_id, point_id, value, update_time 컬럼이 있다고 가정
//            String sql = "SELECT device_id, point_id, value, update_time " +
//                    "FROM SI_DATA_VIEW " +
//                    "WHERE update_time > ? AND update_time <= ?";
//
//            // RowMapper를 이용해 DTO로 변환
//            List<StandardBodyItem> items = jdbcTemplate.query(
//                    sql,
//                    (rs, rowNum) -> StandardBodyItem.builder()
//                            .deviceCode(rs.getString("device_id"))
//                            .pointCode(rs.getString("point_id"))
//                            .value(rs.getString("value"))
//                            .soureTimestamp(rs.getString("update_time"))
//                            .build(),
//                    lastReadTime, queryTime
//            ).stream().collect(Collectors.toList()); // List<StandardBodyItem> -> List<Object> 변환
//
//            if (!items.isEmpty()) {
//                log.info("Found {} changed items from SI DB", items.size());
//
//                // Envelope 포장
//                StandardEnvelope<Object> envelope = StandardEnvelope.<Object>builder()
//                        .header(StandardHeader.builder()
//                                .trxId(UUID.randomUUID().toString())
//                                .senderId("SI_POLLING")
//                                .timestamp(LocalDateTime.now().toString())
//                                .messageType(MessageType.UPDATE)
//                                .build())
//                        .body(items)
//                        .build();
//
//                // 서비스 호출 (전송 성공 시에만 커서 업데이트)
//                ingestService.ingest(envelope)
//                        .doOnSuccess(v -> {
//                            lastReadTime = queryTime;
//                            log.debug("Polling success. Cursor updated to {}", lastReadTime);
//                        })
//                        .doOnError(e -> log.error("Ingest failed during polling", e))
//                        .subscribe();
//            }
//        } catch (Exception e) {
//            log.error("Polling failed. Check DB connection or SQL.", e);
//        }
//    }

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
