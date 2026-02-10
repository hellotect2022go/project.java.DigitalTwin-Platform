package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStepDetailResponse {

    private Long id;
    private Long templateId;
    private Integer stepOrder;
    private String title;
    private String description;
    private Long activationRuleId;
    private Long completionRuleId;
    private List<SopItemResponse> items;
}
