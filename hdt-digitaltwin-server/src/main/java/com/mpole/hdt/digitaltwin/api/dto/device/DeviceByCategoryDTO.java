package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceByCategoryDTO {
    
    // Device 기본 정보
    private String deviceId;
    private String deviceName;

    // DeviceModel 정보
    private Long deviceModelId;
    private String modelAssetCode;
    private String modelAssetNameKo;

    
    // Category 정보 (DeviceModel을 통해)
    private Long categoryId;
    private String categoryCode;
    private String categoryNameKo;
    private String categoryPath;
    
    // SystemType 정보 (DeviceModel을 통해)
    private String sysCode;
    private String sysNameKo;
    
    // Asset3DModel 정보 (DeviceModel을 통해)
    private String asset3DModelName;
    
    // DevicePlacement 정보 (있는 경우)
    private boolean set;
}

