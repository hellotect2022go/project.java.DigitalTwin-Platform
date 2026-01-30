package com.mpole.hdt.digitaltwin.api.dto.devicemodel;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceModelDTO {
    
    private Long id;
    private String assetCode;
    private String assetNameKo;
    private String assetNameEn;
    
    // Category 정보
    private Long categoryId;
    private String categoryCode;
    private String categoryNameKo;
    private String categoryPath; // 전체 경로 (예: "기계설비 > 자동제어")
    
    // SystemType 정보
    private Long systemTypeId;
    private String sysCode;
    private String sysNameKo;
    
    // Asset3DModel 정보
    private Long asset3DModelId;
    private String modelName;
    private String thumbnailUrl;
    
    private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private OffsetDateTime installationDate;
    private Map<String, Object> customAttributes;
    private String status;
    private Boolean enabled;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

