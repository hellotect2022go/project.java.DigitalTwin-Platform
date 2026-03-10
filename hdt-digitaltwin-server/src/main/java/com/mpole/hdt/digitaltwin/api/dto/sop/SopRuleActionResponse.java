package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleActionResponse {
    private Long id;
    private Long ruleId;
    private Long stepId;
    private String stepTitle; // 편의를 위한 정보
}
