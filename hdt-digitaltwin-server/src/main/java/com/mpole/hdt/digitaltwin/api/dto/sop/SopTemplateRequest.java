package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopTemplateRequest {

    private String eventType;
    private String name;
    private Integer version;
    private Boolean isActive;
}
