package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.CategorySystemMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorySystemMappingRepository extends JpaRepository<CategorySystemMapping, Long> {

    /**
     * 특정 카테고리의 모든 매핑 조회
     */
    List<CategorySystemMapping> findByCategory_Id(Long categoryId);

    /**
     * 특정 시스템 타입의 모든 매핑 조회
     */
    List<CategorySystemMapping> findBySystemType_Id(Long systemTypeId);

    /**
     * 특정 카테고리의 모든 매핑 삭제
     */
    @Modifying
    @Query("DELETE FROM CategorySystemMapping m WHERE m.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 특정 시스템 타입의 모든 매핑 삭제
     */
    @Modifying
    @Query("DELETE FROM CategorySystemMapping m WHERE m.systemType.id = :systemTypeId")
    void deleteBySystemTypeId(@Param("systemTypeId") Long systemTypeId);

    /**
     * 특정 카테고리-시스템타입 매핑 존재 여부
     */
    boolean existsByCategory_IdAndSystemType_Id(Long categoryId, Long systemTypeId);
}

