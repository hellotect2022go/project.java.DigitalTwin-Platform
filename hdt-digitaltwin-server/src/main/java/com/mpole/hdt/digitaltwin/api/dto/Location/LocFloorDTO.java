package com.mpole.hdt.digitaltwin.api.dto.Location;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocFloorDTO {
    private Long floorId;
    private String floorName;
    private List<LocZoneDTO> zoneList;
}
