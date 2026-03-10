package com.mpole.hdt.digitaltwin.application.repository.patrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolRouteRepo extends JpaRepository<PatrolRoute,Long> {
}
