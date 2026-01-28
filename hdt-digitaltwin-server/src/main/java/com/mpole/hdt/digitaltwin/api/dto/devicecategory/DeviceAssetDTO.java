package com.mpole.hdt.digitaltwin.api.dto.devicecategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAssetDTO {
    private Long assetId;
    private String assetCode;
    private String assetNameKo;
    private String assetNameEn;

    // 카테고리 정보
    private Long categoryId;
    private String categoryName;
    private String categoryFullPath;

    // 시스템 정보
    private Long systemId;
    private String systemCode;
    private String systemName;

    // 위치
    private String building;
    private String floor;
    private String zone;

    // Unity
    private Long object3dId;
    private Float unityPositionX;
    private Float unityPositionY;
    private Float unityPositionZ;
    private Float unityRotationX;
    private Float unityRotationY;
    private Float unityRotationZ;
    private Float unityScale;

    // 기본 속성
    private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private OffsetDateTime installationDate;

    // 확장 속성
    private Map<String, Object> customAttributes;

    private String status;
    private Boolean enabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
