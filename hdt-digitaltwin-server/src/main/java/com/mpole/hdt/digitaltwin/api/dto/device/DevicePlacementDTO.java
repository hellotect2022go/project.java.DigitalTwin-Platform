package com.mpole.hdt.digitaltwin.api.dto.device;


import com.mpole.hdt.digitaltwin.application.repository.device.Device;

public record DevicePlacementDTO(
        Long deviceId,
        String deviceName,
        String description,
        DeviceTransformDTO transform,
        DeviceLocationDTO location,
        Boolean active,
        boolean isSet
) {
    // 서비스에서 호출할 정적 팩토리 메서드
    public static DevicePlacementDTO from(Device device) {
        return new DevicePlacementDTO(
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDescription(),
                DeviceTransformDTO.from(device.getDeviceTransform()),
                DeviceLocationDTO.from(device),
                device.getActive(),
                device.getDeviceTransform() != null
        );
    }
}
