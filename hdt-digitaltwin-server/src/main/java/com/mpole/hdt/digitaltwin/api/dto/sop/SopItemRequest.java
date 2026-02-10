package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemRequest {

    private Long stepId;
    private String title;
    private String itemType;  // SINGLE_BUTTON, MULTI_BUTTON
    private Boolean isRequired;
    private String groupKey;
    private Integer displayOrder;
    private String uiStyle;
    private String actionLabel;
    private Long activationRuleId;
    private List<SopItemOptionRequest> options;
}
