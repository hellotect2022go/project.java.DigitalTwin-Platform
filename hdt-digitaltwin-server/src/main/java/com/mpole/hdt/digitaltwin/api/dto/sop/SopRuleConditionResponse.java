package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleConditionResponse {
    private Long id;
    private Long ruleId;
    private Long itemId;
    private Long optionId;
    private String itemLabel; // 편의를 위한 정보
    private String optionLabel; // 편의를 위한 정보
}
