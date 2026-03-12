package com.mpole.hdt.digitaltwin.persistence.patrol;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatrolRouteResponse {
    private Long routeId;
    private String routeName;
    private String description;
    private boolean active;
    private Integer waypointCount;
}
