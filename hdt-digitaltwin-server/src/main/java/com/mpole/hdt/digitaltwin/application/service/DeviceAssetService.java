package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.devicecategory.DeviceAssetDTO;
import com.mpole.hdt.digitaltwin.api.dto.request.DeviceAssetRequest;
import com.mpole.hdt.digitaltwin.application.repository.DeviceAssetRepository;
import com.mpole.hdt.digitaltwin.application.repository.DeviceCategoryRepository;
import com.mpole.hdt.digitaltwin.application.repository.DeviceSystemTypeRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceAsset;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceCategory;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceSystemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAssetService {

    private final DeviceAssetRepository assetRepository;
    private final DeviceCategoryRepository categoryRepository;
    private final DeviceSystemTypeRepository systemTypeRepository;
    private final DeviceCategoryService categoryService;

    /**
     * 전체 에셋 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> getAllAssets() {
        return assetRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * ID로 에셋 조회
     */
    @Transactional(readOnly = true)
    public Optional<DeviceAssetDTO> getAssetById(Long assetId) {
        return assetRepository.findById(assetId)
                .map(this::toDto);
    }

    /**
     * 에셋 코드로 조회
     */
    @Transactional(readOnly = true)
    public Optional<DeviceAssetDTO> getAssetByCode(String assetCode) {
        return assetRepository.findByAssetCode(assetCode)
                .map(this::toDto);
    }

    /**
     * 시스템별 에셋 조회 (계층 구조 포함)
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> getAssetsBySystemCode(String sysCode) {
        return assetRepository.findBySystemCodeWithHierarchy(sysCode)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 카테고리별 에셋 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> getAssetsByCategory(Long catId) {
        DeviceCategory category = categoryRepository.findById(catId).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));;

        // 부모가 있으면 부모 id 추출 & 자식이 있으면 자식 id 추출
        // 2. 자신 + 모든 하위 카테고리 ID 수집
        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIds(category, categoryIds);

        return assetRepository
                //.findByCategory_Id(catId)
                .findByCategoryIdIn(categoryIds)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 재귀적으로 자신 + 모든 하위 카테고리 ID 수집
    private void collectCategoryIds(DeviceCategory category, List<Long> ids) {
        ids.add(category.getId()); // 자신 추가
        for (DeviceCategory child : category.getChildren()) {
            collectCategoryIds(child, ids); // 재귀 호출
        }
    }

    /**
     * 건물별 에셋 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> getAssetsByBuilding(String building) {
        return assetRepository.findByBuilding(building)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 건물+층별 에셋 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> getAssetsByBuildingAndFloor(String building, String floor) {
        return assetRepository.findByBuildingAndFloor(building, floor)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 검색
     */
    @Transactional(readOnly = true)
    public List<DeviceAssetDTO> searchAssets(String keyword) {
        return assetRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 에셋 생성
     */
    @Transactional
    public DeviceAssetDTO createAsset(DeviceAssetRequest request) {
        DeviceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        DeviceSystemType systemType = systemTypeRepository.findById(request.getSystemId())
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다."));

        DeviceAsset asset = DeviceAsset.builder()
                .assetCode(request.getAssetCode())
                .assetNameKo(request.getAssetNameKo())
                .assetNameEn(request.getAssetNameEn())
                .category(category)
                .systemType(systemType)
                .building(request.getBuilding())
                .floor(request.getFloor())
                .zone(request.getZone())
                .object3dId(request.getObject3dId())
                .unityPositionX(request.getUnityPositionX())
                .unityPositionY(request.getUnityPositionY())
                .unityPositionZ(request.getUnityPositionZ())
                .unityRotationX(request.getUnityRotationX())
                .unityRotationY(request.getUnityRotationY())
                .unityRotationZ(request.getUnityRotationZ())
                .unityScale(request.getUnityScale() != null ? request.getUnityScale() : 1.0f)
                .manufacturer(request.getManufacturer())
                .modelNumber(request.getModelNumber())
                .serialNumber(request.getSerialNumber())
                .installationDate(request.getInstallationDate())
                .customAttributes(request.getCustomAttributes())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();

        asset = assetRepository.save(asset);
        log.info("새 에셋 생성: {} (코드: {})", asset.getAssetNameKo(), asset.getAssetCode());

        return toDto(asset);
    }

    /**
     * 에셋 수정
     */
    @Transactional
    public DeviceAssetDTO updateAsset(Long assetId, DeviceAssetRequest request) {
        DeviceAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("에셋을 찾을 수 없습니다."));

        DeviceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        DeviceSystemType systemType = systemTypeRepository.findById(request.getSystemId())
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다."));

        asset.setAssetCode(request.getAssetCode());
        asset.setAssetNameKo(request.getAssetNameKo());
        asset.setAssetNameEn(request.getAssetNameEn());
        asset.setCategory(category);
        asset.setSystemType(systemType);
        asset.setBuilding(request.getBuilding());
        asset.setFloor(request.getFloor());
        asset.setZone(request.getZone());
        asset.setObject3dId(request.getObject3dId());
        asset.setUnityPositionX(request.getUnityPositionX());
        asset.setUnityPositionY(request.getUnityPositionY());
        asset.setUnityPositionZ(request.getUnityPositionZ());
        asset.setUnityRotationX(request.getUnityRotationX());
        asset.setUnityRotationY(request.getUnityRotationY());
        asset.setUnityRotationZ(request.getUnityRotationZ());
        asset.setUnityScale(request.getUnityScale());
        asset.setManufacturer(request.getManufacturer());
        asset.setModelNumber(request.getModelNumber());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setInstallationDate(request.getInstallationDate());
        asset.setCustomAttributes(request.getCustomAttributes());
        asset.setStatus(request.getStatus());
        asset.setEnabled(request.getEnabled());

        asset = assetRepository.save(asset);
        log.info("에셋 수정: {} (코드: {})", asset.getAssetNameKo(), asset.getAssetCode());

        return toDto(asset);
    }

    /**
     * 에셋 삭제
     */
    @Transactional
    public void deleteAsset(Long assetId) {
        DeviceAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("에셋을 찾을 수 없습니다."));

        assetRepository.delete(asset);
        log.info("에셋 삭제: {} (코드: {})", asset.getAssetNameKo(), asset.getAssetCode());
    }

    // ===== Private Methods =====

    private DeviceAssetDTO toDto(DeviceAsset entity) {
        if (entity == null) return null;

        return DeviceAssetDTO.builder()
                .assetId(entity.getId())
                .assetCode(entity.getAssetCode())
                .assetNameKo(entity.getAssetNameKo())
                .assetNameEn(entity.getAssetNameEn())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getCategoryNameKo())
                .categoryFullPath(categoryService.buildFullPath(entity.getCategory()))
                .systemId(entity.getSystemType().getId())
                .systemCode(entity.getSystemType().getSysCode())
                .systemName(entity.getSystemType().getSysNameKo())
                .building(entity.getBuilding())
                .floor(entity.getFloor())
                .zone(entity.getZone())
                .object3dId(entity.getObject3dId())
                .unityPositionX(entity.getUnityPositionX())
                .unityPositionY(entity.getUnityPositionY())
                .unityPositionZ(entity.getUnityPositionZ())
                .unityRotationX(entity.getUnityRotationX())
                .unityRotationY(entity.getUnityRotationY())
                .unityRotationZ(entity.getUnityRotationZ())
                .unityScale(entity.getUnityScale())
                .manufacturer(entity.getManufacturer())
                .modelNumber(entity.getModelNumber())
                .serialNumber(entity.getSerialNumber())
                .installationDate(entity.getInstallationDate())
                .customAttributes(entity.getCustomAttributes())
                .status(entity.getStatus())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
