package com.mpole.hdt.digitaltwin.api.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    private Long parentId;

    @NotNull(message = "depth는 필수입니다")
    private Integer depth;

    @NotBlank(message = "카테고리 코드는 필수입니다")
    private String categoryCode;

    @NotBlank(message = "카테고리명(한글)은 필수입니다")
    private String categoryNameKo;

    private String categoryNameEn;
    private String description;
    
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Builder.Default
    private Boolean enabled = true;
}

