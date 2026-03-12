package com.mpole.hdt.digitaltwin.config.initializer;

import com.mpole.hdt.digitaltwin.persistence.device.Device;
import com.mpole.hdt.digitaltwin.persistence.device.DevicePoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceCache {
    public record EnrichedTelemetry(
            Long deviceId, String deviceName, String deviceCode, String pointCode, String unit, String value, String timestamp
    ){}


    private final Map<String, EnrichedTelemetry> pointLookupMap = new ConcurrentHashMap<>();

    public void initCache(List<Device> deviceList) {
        pointLookupMap.clear();

        for (Device device : deviceList) {
            for (DevicePoint devicePoint : device.getDevicePoints()) {
                String pointId = devicePoint.getDeviceCode()+":"+devicePoint.getPointCode();

                pointLookupMap.put(pointId, new EnrichedTelemetry(
                        device.getDeviceId(),
                        device.getDeviceName(),
                        devicePoint.getDeviceCode(),
                        devicePoint.getPointCode(),
                        devicePoint.getUnit(),
                        null,
                        null
                ));
            }
        }
    }

    public EnrichedTelemetry getDeviceInfo(String pointId) {
        return pointLookupMap.get(pointId);
    }
}
