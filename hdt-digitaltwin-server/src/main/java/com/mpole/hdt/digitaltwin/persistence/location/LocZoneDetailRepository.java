package com.mpole.hdt.digitaltwin.persistence.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocZoneDetailRepository extends JpaRepository<LocZoneDetail,Long> {

}
