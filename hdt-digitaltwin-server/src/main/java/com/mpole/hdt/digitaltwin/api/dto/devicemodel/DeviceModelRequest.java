package com.mpole.hdt.digitaltwin.api.dto.devicemodel;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceModelRequest {
    
    private String assetCode;
    private String assetNameKo;
    private String assetNameEn;
    private Long categoryId;
    private Long systemTypeId;
    private Long asset3DModelId; // 3D 모델 선택 (선택사항)
    private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private OffsetDateTime installationDate;
    private Map<String, Object> customAttributes;
    private String status;
    private Boolean enabled;
}

