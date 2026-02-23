package com.mpole.hdt.digitaltwin.application.repository.patrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolWaypointRepo extends JpaRepository<PatrolWaypoint,Long> {


    @Query("SELECT pw FROM PatrolWaypoint pw JOIN FETCH pw.patrolRoute pr WHERE pr.routeId = :routeId")
    List<PatrolWaypoint> findWaypointsByRoute(@Param("routeId")int routeId);
}
