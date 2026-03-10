package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDTO {
    
    // Device 기본 정보
    private Long deviceId;
    private String deviceName;

    private String description;
    private Boolean active;

    // Category 정보 (DeviceModel을 통해)
    private Long categoryId;
    private String categoryName;
    private String categoryPath;
    
    // SystemType 정보 (DeviceModel을 통해)

    // Asset3DModel 정보 (DeviceModel을 통해)
    private Long assetId;
    private String assetName;

    // DevicePlacement 정보 (있는 경우)
    private DeviceTransformDTO transform;

    private DeviceLocationDTO location;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private boolean set;
}

