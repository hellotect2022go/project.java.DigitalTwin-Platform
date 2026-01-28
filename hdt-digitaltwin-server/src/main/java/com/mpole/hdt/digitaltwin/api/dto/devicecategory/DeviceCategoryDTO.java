package com.mpole.hdt.digitaltwin.api.dto.devicecategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryDTO {
    private Long categoryId;
    private Long parentId;
    private Integer depth;
    private String categoryCode;
    private String categoryNameKo;
    private String categoryNameEn;
    private String description;
    private Integer displayOrder;
    private Boolean enabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // 트리 구조용
    private List<DeviceCategoryDTO> children;

    // 전체 경로 (예: "기계설비 > 자동제어 > 제어반")
    private String fullPath;
}
