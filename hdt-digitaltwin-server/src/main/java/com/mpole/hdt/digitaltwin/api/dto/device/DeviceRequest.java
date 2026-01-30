package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRequest {
    
    private String deviceId;
    private String deviceName;
    private Long deviceModelId;  // 필수: DeviceModel 선택
    private String status;
    private Boolean online;
    private String location;
    private String floor;
    private String zone;
    private Double currentValue;
    private String unit;
    private OffsetDateTime installationDate;
    private OffsetDateTime manufactureDate;
    private String serialNumber;
    private String description;
    private Boolean enabled;
}

