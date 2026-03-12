package com.mpole.hdt.digitaltwin.persistence.patrol;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatrolWaypointResponse {
    private Long waypointId;
    private Integer seqNum;
    private float posX;
    private float posY;
    private float posZ;
    private int durSec;
}
