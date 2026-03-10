package com.mpole.hdt.digitaltwin.api.dto.Location;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponse {
    private Long buildingId;
    private String buildingName;
    private List<LocFloorDTO> floorList;
}
