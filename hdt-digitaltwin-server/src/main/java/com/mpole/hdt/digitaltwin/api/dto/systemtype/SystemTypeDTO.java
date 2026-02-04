package com.mpole.hdt.digitaltwin.api.dto.systemtype;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemTypeDTO {

    private Long id;
    private String sysCode;
    private String sysNameKo;
    private String sysNameEn;
    private String description;
    private String iconUrl;
    private Boolean enabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

