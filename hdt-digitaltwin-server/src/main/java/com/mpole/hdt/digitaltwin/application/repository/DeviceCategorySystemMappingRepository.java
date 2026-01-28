package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceCategorySystemMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceCategorySystemMappingRepository extends JpaRepository<DeviceCategorySystemMapping, Long> {

    // 특정 카테고리의 가능한 시스템 목록
    List<DeviceCategorySystemMapping> findByCategory_Id(Long categoryId);

    // 특정 시스템을 사용하는 카테고리 목록
    List<DeviceCategorySystemMapping> findBySystemType_Id(Long sysId);

    // 특정 카테고리 - 시스템 매핑 조회
    Optional<DeviceCategorySystemMapping> findByCategory_IdAndSystemType_Id(Long categoryId, Long sysId);

    // 특정 카테고리의 기본 시스템
    Optional<DeviceCategorySystemMapping> findByCategory_IdAndIsDefaultTrue(Long categoryId);

    // 매핑 존재 여부
    boolean existsByCategory_IdAndSystemType_Id(Long categoryId, Long sysId);
}
