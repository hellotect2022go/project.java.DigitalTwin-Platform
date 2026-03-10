package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.util.List;

/**
 * 여러 Item 결과를 한 번에 저장하는 요청
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemResultBatchRequest {

    private List<ItemResultInput> results;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemResultInput {
        private Long itemId;
        private Long optionId;  // nullable
        private Boolean selected;
        private String selectedBy;  // nullable
    }
}
