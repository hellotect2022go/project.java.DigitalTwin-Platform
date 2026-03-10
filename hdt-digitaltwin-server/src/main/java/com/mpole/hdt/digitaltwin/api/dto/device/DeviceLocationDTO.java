package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceLocationDTO {
    private Long buildingId;
    private String buildingName;

    private Long floorId;
    private String floorName;

    private Long zoneId;
    private String zoneName;

    private Long zoneDetailId;
    private String zoneDetailName;
}
