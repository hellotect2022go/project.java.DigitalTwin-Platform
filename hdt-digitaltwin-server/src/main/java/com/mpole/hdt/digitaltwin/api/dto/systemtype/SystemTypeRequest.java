package com.mpole.hdt.digitaltwin.api.dto.systemtype;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemTypeRequest {

    @NotBlank(message = "시스템 코드는 필수입니다")
    private String sysCode;

    @NotBlank(message = "시스템명(한글)은 필수입니다")
    private String sysNameKo;

    private String sysNameEn;
    private String description;
    private String iconUrl;
    
    @Builder.Default
    private Boolean enabled = true;
}

