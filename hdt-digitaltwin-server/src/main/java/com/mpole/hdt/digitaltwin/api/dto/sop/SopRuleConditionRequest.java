package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleConditionRequest {
    private Long itemId;
    private Long optionId; // nullable
}
