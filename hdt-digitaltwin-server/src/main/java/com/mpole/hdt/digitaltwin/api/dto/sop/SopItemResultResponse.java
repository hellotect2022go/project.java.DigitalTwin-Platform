package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemResultResponse {

    private Long id;
    private Long instanceId;
    private Long itemId;
    private Long optionId;
    private Boolean selected;
    private String selectedBy;
    private OffsetDateTime selectedAt;
}
