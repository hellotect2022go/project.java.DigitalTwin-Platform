package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 최상위 카테고리 조회 (parent가 null인 것들)
     */
    List<Category> findByParentIsNullOrderByDisplayOrderAsc();

    /**
     * 특정 부모의 자식 카테고리 조회
     */
    List<Category> findByParent_IdOrderByDisplayOrderAsc(Long parentId);

    /**
     * depth별 카테고리 조회
     */
    List<Category> findByDepthOrderByDisplayOrderAsc(Integer depth);

    /**
     * 카테고리 코드로 조회
     */
    Optional<Category> findByCategoryCode(String categoryCode);

    /**
     * 카테고리 코드 중복 체크
     */
    boolean existsByCategoryCode(String categoryCode);

    /**
     * 검색 (이름 또는 코드)
     */
    @Query("SELECT c FROM Category c WHERE " +
           "c.categoryNameKo LIKE %:keyword% OR " +
           "c.categoryNameEn LIKE %:keyword% OR " +
           "c.categoryCode LIKE %:keyword%")
    List<Category> searchCategories(@Param("keyword") String keyword);

    /**
     * 전체 카테고리 조회 (트리 구조 구성용)
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent ORDER BY c.depth, c.displayOrder")
    List<Category> findAllWithParent();
}

