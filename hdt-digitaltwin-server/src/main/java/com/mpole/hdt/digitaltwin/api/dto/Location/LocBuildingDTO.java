package com.mpole.hdt.digitaltwin.api.dto.Location;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocBuildingDTO {
    private Long buildingId;
    private String buildingName;
}
