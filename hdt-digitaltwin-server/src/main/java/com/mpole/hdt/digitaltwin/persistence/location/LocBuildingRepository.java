package com.mpole.hdt.digitaltwin.persistence.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocBuildingRepository extends JpaRepository<LocBuilding,Long> {

    @Query("""
            SELECT lb FROM LocBuilding lb 
            LEFT JOIN FETCH lb.locFloorList lf 
            LEFT JOIN FETCH lf.locZoneList lz
            LEFT JOIN FETCH lz.locZoneDetailList lzd 
            """)
    List<LocBuilding> fetchLocationInfoAll();
}
