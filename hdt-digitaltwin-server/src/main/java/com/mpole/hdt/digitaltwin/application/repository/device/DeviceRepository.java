package com.mpole.hdt.digitaltwin.application.repository.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {


    @Query("""
            SELECT d FROM Device d
            LEFT JOIN FETCH d.deviceTransform
            LEFT JOIN FETCH d.locBuilding
            LEFT JOIN FETCH d.locFloor
            LEFT JOIN FETCH d.locZone
            LEFT JOIN FETCH d.locZoneDetail 
            ORDER BY d.deviceId ASC
            """)
    List<Device> fetchAllDevices();

    @Query("""
            SELECT d FROM Device d
            LEFT JOIN FETCH d.deviceTransform
            LEFT JOIN FETCH d.locBuilding
            LEFT JOIN FETCH d.locFloor
            LEFT JOIN FETCH d.locZone
            LEFT JOIN FETCH d.locZoneDetail 
            WHERE (:floorId IS NULL OR d.locFloor.floorId = :floorId) 
            AND (:zoneId IS NULL OR d.locZone.zoneId = : zoneId)
            ORDER BY d.deviceId ASC
            """)
    List<Device> fetchSearchDevices(@Param("floorId") String floorId, @Param("zoneId") String zoneId  );


    @Query("""
            SELECT d FROM Device d
            LEFT JOIN FETCH d.deviceTransform
            LEFT JOIN FETCH d.locBuilding
            LEFT JOIN FETCH d.locFloor
            LEFT JOIN FETCH d.locZone
            LEFT JOIN FETCH d.locZoneDetail 
            WHERE d.deviceCategory.categoryId = :categoryId
            ORDER BY d.deviceId ASC
            """)
    List<Device> fetchTargetCategoryDevices(@Param("categoryId") Integer categoryId);
}
