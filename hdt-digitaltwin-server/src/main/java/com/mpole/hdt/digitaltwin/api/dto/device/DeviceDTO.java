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
    private Long id;
    private String deviceId;
    private String deviceName;
    private String status;
    private Boolean online;
//    private String location;
//    private String floor;
//    private String zone;
    private Double currentValue;
    private String unit;
    private OffsetDateTime lastCommunication;
    private OffsetDateTime installationDate;
    private OffsetDateTime manufactureDate;
    private String serialNumber;
    private String description;
    private Boolean enabled;
    
    // DeviceModel 정보
    private Long deviceModelId;
    private String modelAssetCode;
    private String modelAssetNameKo;
    private String modelAssetNameEn;
    
    // Category 정보 (DeviceModel을 통해)
    private Long categoryId;
    private String categoryCode;
    private String categoryNameKo;
    private String categoryPath;
    
    // SystemType 정보 (DeviceModel을 통해)
    private Long systemTypeId;
    private String sysCode;
    private String sysNameKo;
    
    // Asset3DModel 정보 (DeviceModel을 통해)
    private Long asset3DModelId;
    private String asset3DModelName;
    private String thumbnailUrl;
    
    // DevicePlacement 정보 (있는 경우)
    private DevicePlacementDTO placement;
    
    // 타임스탬프
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

