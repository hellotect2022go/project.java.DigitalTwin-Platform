package com.mpole.hdt.digitaltwin.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class DeviceAssetRequest {
    @NotBlank(message = "에셋 코드는 필수입니다")
    private String assetCode;

    @NotBlank(message = "장비명은 필수입니다")
    private String assetNameKo;

    private String assetNameEn;

    @NotNull(message = "카테고리 ID는 필수입니다")
    private Long categoryId;

    @NotNull(message = "시스템 ID는 필수입니다")
    private Long systemId;

    private String building;
    private String floor;
    private String zone;

    private Long object3dId;
    private Float unityPositionX;
    private Float unityPositionY;
    private Float unityPositionZ;
    private Float unityRotationX;
    private Float unityRotationY;
    private Float unityRotationZ;
    private Float unityScale;

    private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private OffsetDateTime installationDate;

    private Map<String, Object> customAttributes;

    private String status;
    private Boolean enabled;
}
