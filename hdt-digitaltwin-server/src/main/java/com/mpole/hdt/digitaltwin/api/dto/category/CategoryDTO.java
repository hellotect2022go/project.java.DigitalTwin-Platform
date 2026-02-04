package com.mpole.hdt.digitaltwin.api.dto.category;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;
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
    @Builder.Default
    private List<CategoryDTO> children = new ArrayList<>();
    
    // 전체 경로 (예: "대분류 > 중분류 > 소분류")
    private String fullPath;
}

