package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCategoryDTO {
    
    // Device 기본 정보
    private Long categoryId;
    private Long parentId;
    private Integer depth;
    private String categoryName;
    private String description;
    private Integer displayOrder;
    private Boolean active;
    private String fullPath;
    private Boolean isRoot;
    private Boolean isLeaf;

    // 트리구조 용
    @Builder.Default
    private List<DeviceCategoryDTO> children = new ArrayList<>();
}

