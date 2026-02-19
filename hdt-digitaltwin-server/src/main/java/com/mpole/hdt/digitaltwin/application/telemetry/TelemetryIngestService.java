package com.mpole.hdt.digitaltwin.application.telemetry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelemetryIngestService {
    public void ingest(TelemetryRaw raw) {
        // TODO: 여기서 DT 상태 업데이트(메모리 캐시/LastValue Map 등)로 연결
        var h = raw.header();
        var list = raw.body();

        // 로그 폭발 방지: 일단 건수 + 첫 1건만 찍기 추천
        if (list == null || list.isEmpty()) {
            log.info("[DT] telemetry received: trxId={}, count=0", h.trxId());
            return;
        }

        var first = list.get(0);
        log.info("[DT] telemetry received: trxId={}, count={}, firstDevice={}, firstPoint={}, firstValue={}, firstTime={}",
                h.trxId(), list.size(),
                first.deviceCode(), first.pointCode(), first.value(), first.sourceTimestamp());

    }
}
