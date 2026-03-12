package com.mpole.hdt.digitaltwin.persistence.patrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolRouteRepo extends JpaRepository<PatrolRoute,Long> {
}
