package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleResponse {
    private Long id;
    private Long templateId;
    private String ruleType;
    private String description;
    private List<SopRuleConditionResponse> conditions;
    private List<SopRuleActionResponse> actions;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
