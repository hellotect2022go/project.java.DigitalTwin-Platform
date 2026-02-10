package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemOptionRequest {

    private String label;
    private String uiStyle;
    private Integer displayOrder;
    private Long activationRuleId;
}
