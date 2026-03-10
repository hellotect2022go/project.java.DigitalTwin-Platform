package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.util.List;

/**
 * Step과 Items를 포함한 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStepWithItemsResponse {

    private Long id;
    private Integer stepOrder;
    private String title;
    private String description;
    private Long activationRuleId;
    private Long completionRuleId;

    // Step에 속한 Items
    private List<SopItemResponse> items;
}
