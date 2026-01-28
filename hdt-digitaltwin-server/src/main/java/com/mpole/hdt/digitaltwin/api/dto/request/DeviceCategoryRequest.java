package com.mpole.hdt.digitaltwin.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryRequest {
    private Long parentId;

    @NotBlank(message = "깊이는 필수입니다.")
    private Integer depth;

    @NotBlank(message = "코드는 필수입니다.")
    private String categoryCode;

    @NotBlank(message = "한글명은 필수입니다.")
    private String categoryNameKo;

    private String categoryNameEn;
    private String description;
    private Integer displayOrder;
    private Boolean enabled;
}
