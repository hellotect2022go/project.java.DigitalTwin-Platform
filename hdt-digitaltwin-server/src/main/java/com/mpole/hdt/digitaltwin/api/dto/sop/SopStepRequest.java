package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStepRequest {

    private Long templateId;
    private Integer stepOrder;
    private String title;
    private String description;
    private Long activationRuleId;
    private Long completionRuleId;
}
