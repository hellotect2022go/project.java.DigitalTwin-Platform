package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.Asset3DModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Asset3DModelRepository extends JpaRepository<Asset3DModel, Long> {
    
    /**
     * 모델명으로 조회
     */
    Optional<Asset3DModel> findByModelName(String modelName);
    
    /**
     * 모델명 존재 여부 확인
     */
    boolean existsByModelName(String modelName);
    
    /**
     * 활성화된 모델 전체 조회
     */
    List<Asset3DModel> findByEnabledTrue();
    
    /**
     * 파일 확장자로 조회
     */
    List<Asset3DModel> findByFileExtension(String fileExtension);
    
    /**
     * 모델명 또는 설명으로 검색
     */
    @Query("SELECT a FROM Asset3DModel a WHERE " +
           "a.modelName LIKE %:keyword% OR " +
           "a.description LIKE %:keyword%")
    List<Asset3DModel> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 확장자별로 조회 (활성화된 것만)
     */
    List<Asset3DModel> findByFileExtensionAndEnabledTrue(String fileExtension);
}

