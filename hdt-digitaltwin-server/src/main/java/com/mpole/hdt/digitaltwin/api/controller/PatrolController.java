package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.application.repository.menu.Menu;
import com.mpole.hdt.digitaltwin.application.repository.menu.MenuRepo;
import com.mpole.hdt.digitaltwin.application.repository.menu.MenuResponse;
import com.mpole.hdt.digitaltwin.application.repository.menu.RoleMenuRepo;
import com.mpole.hdt.digitaltwin.application.repository.patrol.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/patrol")
@RequiredArgsConstructor
public class PatrolController {

    private final PatrolRouteRepo patrolRouteRepo;
    private final PatrolWaypointRepo patrolWaypointRepo;

    @GetMapping("/route")
    public ResponseEntity fetchRoutes() {
        List<PatrolRouteResponse> list = patrolRouteRepo.findAll().stream().map(patrolRoute -> {
            return PatrolRouteResponse.builder()
                    .routeId(patrolRoute.getRouteId())
                    .routeName(patrolRoute.getRouteName())
                    .active(patrolRoute.isActive())
                    .description(patrolRoute.getDescription())
                    .waypointCount(patrolRoute.getPatrolWaypoints().size())
                    .build();
        }).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{routeId}/waypoints")
    public ResponseEntity fetchWaypointsByRoute(@PathVariable int routeId) {
        List<PatrolWaypointResponse> list = patrolWaypointRepo.findWaypointsByRoute(routeId).stream().map(pw->{
            return PatrolWaypointResponse.builder()
                    .waypointId(pw.getWaypointId())
                    .seqNum(pw.getSeqNum())
                    .posX(pw.getPosX())
                    .posY(pw.getPosY())
                    .posZ(pw.getPosZ())
                    .durSec(pw.getDurSec())
                    .build();
        }).toList();
        return ResponseEntity.ok(list);
    }

}
