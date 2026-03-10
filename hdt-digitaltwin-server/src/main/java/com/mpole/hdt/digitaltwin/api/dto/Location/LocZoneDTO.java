package com.mpole.hdt.digitaltwin.api.dto.Location;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocZoneDTO {
    private Long zoneId;
    private String zoneName;
    private String meshName;
}
