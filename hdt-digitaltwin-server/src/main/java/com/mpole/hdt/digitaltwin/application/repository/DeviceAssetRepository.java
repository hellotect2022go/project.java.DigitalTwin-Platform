package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceAssetRepository extends JpaRepository<DeviceAsset, Long> {

    Optional<DeviceAsset> findByAssetCode(String assetCode);

    // 특정 카테고리의 asset
    List<DeviceAsset> findByCategory_Id(Long categoryId);

    // 특정 카테고리 id 들의 asset
    List<DeviceAsset> findByCategoryIdIn(List<Long> categoryIds);

    // 특정 시스템의 asset
    List<DeviceAsset> findBySystemType_Id(Long systemId);

    // 시스템 코드로 조회
    @Query("SELECT a FROM DeviceAsset a WHERE a.systemType.sysCode = :sysCode")
    List<DeviceAsset> findBySystemCode(@Param("sysCode")String sysCode);

    // 건물별 조회
    List<DeviceAsset> findByBuilding(String building);

    // 건물 + 층별 조회
    List<DeviceAsset> findByBuildingAndFloor(String building,String floor);

    // 검색
    @Query("SELECT a FROM DeviceAsset a WHERE a.assetNameKo LIKE %:keyword OR a.assetCode LIKE %:keyword%")
    List<DeviceAsset> searchByKeyword(@Param("keyword") String keyword);

    // 계층구조 포함 조회 (대분류 > 중분류 > 소분류)
    @Query("""
        SELECT a FROM DeviceAsset a
        JOIN FETCH a.category c3
        JOIN FETCH a.systemType s
        LEFT JOIN FETCH c3.parent c2
        LEFT JOIN FETCH c2.parent c1
        WHERE s.sysCode = :sysCode
        ORDER BY c1.displayOrder, c2.displayOrder, c3.displayOrder
        """)
    List<DeviceAsset> findBySystemCodeWithHierarchy(@Param("sysCode") String sysCode);

    // 제조사별 통계
    @Query("SELECT a.manufacturer, COUNT(a) FROM DeviceAsset a GROUP BY a.manufacturer")
    List<Object[]> countByManufacturer();
}
