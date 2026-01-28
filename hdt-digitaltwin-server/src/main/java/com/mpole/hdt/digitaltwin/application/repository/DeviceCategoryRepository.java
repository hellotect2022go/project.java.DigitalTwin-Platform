package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceCategoryRepository extends JpaRepository<DeviceCategory, Long> {

    // 대분류 조회 (parent_id 가 null)
    List<DeviceCategory> findByParentIsNullOrderByDisplayOrderAsc();

    // 특정 부모의 자식 조회
    List<DeviceCategory> findByParent_IdOrderByDisplayOrderAsc(Long parentId);

    // 특정 깊이의 카테고리 조회
    List<DeviceCategory> findByDepthOrderByDisplayOrderAsc(Integer depth);

    // 코드로 조회
    Optional<DeviceCategory> findByCategoryCode(String categoryCode);

    // 검색 (이름으로 )
    @Query("SELECT a FROM DeviceCategory a WHERE a.categoryNameKo LIKE %:keyword% OR a.categoryCode LIKE %:keyword% ORDER BY a.depth")
    List<DeviceCategory> searchByKeyword(@Param("keyword")String keyword);


    // 재귀 쿼리로 전체 트리 조회
    @Query(value= """
            WITH RECURSIVE category_tree (
                id, parent_id, depth, category_code, category_name_ko, category_name_en,
                description, display_order, enabled, created_at, updated_at, path
            ) 
            AS (
                SELECT id, parent_id, depth, category_code, category_name_ko, category_name_en,
                       description, display_order, enabled, created_at, updated_at,
                       CAST(id AS VARCHAR(500))
                FROM DEVICE_CATEGORIES
                WHERE parent_id IS NULL
            
                UNION ALL
           
                SELECT c.id, c.parent_id, c.depth, c.category_code, c.category_name_ko, c.category_name_en,
                       c.description, c.display_order, c.enabled, c.created_at, c.updated_at,
                       CONCAT(ct.path, '/', c.id) AS path
                FROM DEVICE_CATEGORIES c
                JOIN category_tree ct ON c.parent_id = ct.id
            )
            SELECT * FROM category_tree ORDER BY path
            """, nativeQuery = true)
    List<DeviceCategory> findAllRecursive();
}
