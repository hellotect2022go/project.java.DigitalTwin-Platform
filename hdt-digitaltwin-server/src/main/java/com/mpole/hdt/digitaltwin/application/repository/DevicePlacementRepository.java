package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DevicePlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevicePlacementRepository extends JpaRepository<DevicePlacement, Long> {
    
    /**
     * Device ID로 Placement 조회
     */
    Optional<DevicePlacement> findByDevice_Id(Long deviceId);
    
    /**
     * Device ID로 존재 여부 확인
     */
    boolean existsByDevice_Id(Long deviceId);
    
    /**
     * Floor Level별 조회
     */
    List<DevicePlacement> findByFloorLevel(String floorLevel);
    
    /**
     * Layer별 조회
     */
    List<DevicePlacement> findByLayerName(String layerName);
    
    /**
     * 부모 오브젝트별 조회
     */
    List<DevicePlacement> findByParentObject(String parentObject);
    
    /**
     * 활성화된 Placement만 조회
     */
    List<DevicePlacement> findByEnabledTrue();
    
    /**
     * Floor Level + 활성화된 것만 조회
     */
    List<DevicePlacement> findByFloorLevelAndEnabledTrue(String floorLevel);
    
    /**
     * 특정 범위 내 장비 조회 (AABB - Axis-Aligned Bounding Box)
     */
    @Query("SELECT dp FROM DevicePlacement dp WHERE " +
           "dp.positionX BETWEEN :minX AND :maxX AND " +
           "dp.positionY BETWEEN :minY AND :maxY AND " +
           "dp.positionZ BETWEEN :minZ AND :maxZ AND " +
           "dp.enabled = true")
    List<DevicePlacement> findByPositionRange(
            @Param("minX") Float minX, @Param("maxX") Float maxX,
            @Param("minY") Float minY, @Param("maxY") Float maxY,
            @Param("minZ") Float minZ, @Param("maxZ") Float maxZ
    );
    
    /**
     * Device 삭제 시 Placement도 함께 삭제
     */
    void deleteByDevice_Id(Long deviceId);
}

