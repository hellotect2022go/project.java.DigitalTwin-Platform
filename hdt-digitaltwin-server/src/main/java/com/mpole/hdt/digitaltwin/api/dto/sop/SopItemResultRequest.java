package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemResultRequest {

    private Boolean selected;
    private String selectedBy;
    private Long optionId;
}
