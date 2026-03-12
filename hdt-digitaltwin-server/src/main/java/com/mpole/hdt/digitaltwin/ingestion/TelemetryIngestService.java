package com.mpole.hdt.digitaltwin.ingestion;

import com.mpole.hdt.digitaltwin.config.initializer.DeviceCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryIngestService {

    private final DeviceCache deviceCache;

    public List<DeviceCache.EnrichedTelemetry> ingest(TelemetryRaw raw) {
        // TODO: 여기서 DT 상태 업데이트(메모리 캐시/LastValue Map 등)로 연결
        var h = raw.header();
        var list = raw.body();

        // 로그 폭발 방지: 일단 건수 + 첫 1건만 찍기 추천
        if (list == null || list.isEmpty()) {
            log.info("[DT] telemetry received: trxId={}, count=0", h.trxId());
            return Collections.emptyList();
        }

        List<DeviceCache.EnrichedTelemetry> enrichedList = list.stream().map(b->{
            DeviceCache.EnrichedTelemetry base = deviceCache.getDeviceInfo(b.deviceCode()+":"+b.pointCode());
            if (base == null ) {
                return null;
            }
            DeviceCache.EnrichedTelemetry finalData = new DeviceCache.EnrichedTelemetry(
                    base.deviceId(),
                    base.deviceName(),
                    base.deviceCode(),
                    base.pointCode(),
                    base.unit(),
                    b.value(),
                    b.sourceTimestamp()
            );

            return finalData;
        }).toList();

        return enrichedList;
    }

    private void sendToNextStep(DeviceCache.EnrichedTelemetry data) {
        // 비즈니스 로직 처리 (예: InfluxDB 저장 등)
        log.info("데이터 조립 완료: {} - {} - {}", data.deviceId(), data.deviceName(), data.value());
    }
}
