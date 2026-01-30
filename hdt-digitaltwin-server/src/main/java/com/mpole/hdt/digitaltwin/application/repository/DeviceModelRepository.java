package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceModelRepository extends JpaRepository<DeviceModel, Long> {
    
    /**
     * Asset 코드로 조회
     */
    Optional<DeviceModel> findByAssetCode(String assetCode);
    
    /**
     * Asset 코드 존재 여부 확인
     */
    boolean existsByAssetCode(String assetCode);
    
    /**
     * 카테고리별 조회
     */
    List<DeviceModel> findByCategory_Id(Long categoryId);
    
    /**
     * 시스템 타입별 조회
     */
    List<DeviceModel> findBySystemType_Id(Long systemTypeId);
    
    /**
     * 카테고리 + 시스템 타입별 조회
     */
    List<DeviceModel> findByCategory_IdAndSystemType_Id(Long categoryId, Long systemTypeId);
    
    /**
     * 활성화된 모델만 조회
     */
    List<DeviceModel> findByEnabledTrue();
    
    /**
     * 3D 모델별 조회
     */
    List<DeviceModel> findByAsset3DModel_Id(Long asset3DModelId);
    
    /**
     * 검색 (코드, 한글명, 영문명, 제조사)
     */
    @Query("SELECT dm FROM DeviceModel dm WHERE " +
           "dm.assetCode LIKE %:keyword% OR " +
           "dm.assetNameKo LIKE %:keyword% OR " +
           "dm.assetNameEn LIKE %:keyword% OR " +
           "dm.manufacturer LIKE %:keyword%")
    List<DeviceModel> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 상태별 조회
     */
    List<DeviceModel> findByStatus(String status);
    
    /**
     * 카테고리별 활성화된 모델 조회
     */
    List<DeviceModel> findByCategory_IdAndEnabledTrue(Long categoryId);
}

