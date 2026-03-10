package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleRequest {
    private Long templateId;
    private String ruleType;
    private String description;
    private List<SopRuleConditionRequest> conditions;
    private List<SopRuleActionRequest> actions;
}
