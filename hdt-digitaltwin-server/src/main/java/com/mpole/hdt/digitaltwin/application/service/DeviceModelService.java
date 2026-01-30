package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.devicemodel.DeviceModelDTO;
import com.mpole.hdt.digitaltwin.api.dto.devicemodel.DeviceModelRequest;
import com.mpole.hdt.digitaltwin.application.repository.Asset3DModelRepository;
import com.mpole.hdt.digitaltwin.application.repository.CategoryRepository;
import com.mpole.hdt.digitaltwin.application.repository.DeviceModelRepository;
import com.mpole.hdt.digitaltwin.application.repository.SystemTypeRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.Asset3DModel;
import com.mpole.hdt.digitaltwin.application.repository.entity.Category;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceModel;
import com.mpole.hdt.digitaltwin.application.repository.entity.SystemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceModelService {
    
    private final DeviceModelRepository repository;
    private final CategoryRepository categoryRepository;
    private final SystemTypeRepository systemTypeRepository;
    private final Asset3DModelRepository asset3DModelRepository;
    
    /**
     * 전체 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceModelDTO> getAllModels() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 활성화된 모델만 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceModelDTO> getEnabledModels() {
        return repository.findByEnabledTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ID로 조회
     */
    @Transactional(readOnly = true)
    public DeviceModelDTO getModelById(Long id) {
        DeviceModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + id));
        return toDto(model);
    }
    
    /**
     * Asset 코드로 조회
     */
    @Transactional(readOnly = true)
    public DeviceModelDTO getModelByAssetCode(String assetCode) {
        DeviceModel model = repository.findByAssetCode(assetCode)
                .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + assetCode));
        return toDto(model);
    }
    
    /**
     * 카테고리별 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceModelDTO> getModelsByCategory(Long categoryId) {
        return repository.findByCategory_IdAndEnabledTrue(categoryId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 시스템 타입별 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceModelDTO> getModelsBySystemType(Long systemTypeId) {
        return repository.findBySystemType_Id(systemTypeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 검색
     */
    @Transactional(readOnly = true)
    public List<DeviceModelDTO> searchModels(String keyword) {
        return repository.searchByKeyword(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 모델 생성
     */
    @Transactional
    public DeviceModelDTO createModel(DeviceModelRequest request) {
        // Asset 코드 중복 체크
        if (repository.existsByAssetCode(request.getAssetCode())) {
            throw new IllegalArgumentException("이미 존재하는 Asset 코드입니다: " + request.getAssetCode());
        }
        
        // Category 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));
        
        // SystemType 조회
        SystemType systemType = systemTypeRepository.findById(request.getSystemTypeId())
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + request.getSystemTypeId()));
        
        // Asset3DModel 조회 (선택사항)
        Asset3DModel asset3DModel = null;
        if (request.getAsset3DModelId() != null) {
            asset3DModel = asset3DModelRepository.findById(request.getAsset3DModelId())
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + request.getAsset3DModelId()));
        }
        
        // 엔티티 생성
        DeviceModel model = DeviceModel.builder()
                .assetCode(request.getAssetCode())
                .assetNameKo(request.getAssetNameKo())
                .assetNameEn(request.getAssetNameEn())
                .category(category)
                .systemType(systemType)
                .asset3DModel(asset3DModel)
                .manufacturer(request.getManufacturer())
                .modelNumber(request.getModelNumber())
                .serialNumber(request.getSerialNumber())
                .installationDate(request.getInstallationDate())
                .customAttributes(request.getCustomAttributes())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        
        model = repository.save(model);
        log.info("장비 모델 생성: {} ({})", model.getAssetNameKo(), model.getAssetCode());
        
        return toDto(model);
    }
    
    /**
     * 모델 수정
     */
    @Transactional
    public DeviceModelDTO updateModel(Long id, DeviceModelRequest request) {
        DeviceModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + id));
        
        // Asset 코드 변경 시 중복 체크
        if (!model.getAssetCode().equals(request.getAssetCode())) {
            if (repository.existsByAssetCode(request.getAssetCode())) {
                throw new IllegalArgumentException("이미 존재하는 Asset 코드입니다: " + request.getAssetCode());
            }
            model.setAssetCode(request.getAssetCode());
        }
        
        // Category 조회
        if (!model.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + request.getCategoryId()));
            model.setCategory(category);
        }
        
        // SystemType 조회
        if (!model.getSystemType().getId().equals(request.getSystemTypeId())) {
            SystemType systemType = systemTypeRepository.findById(request.getSystemTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + request.getSystemTypeId()));
            model.setSystemType(systemType);
        }
        
        // Asset3DModel 조회 (선택사항)
        if (request.getAsset3DModelId() != null) {
            Asset3DModel asset3DModel = asset3DModelRepository.findById(request.getAsset3DModelId())
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + request.getAsset3DModelId()));
            model.setAsset3DModel(asset3DModel);
        } else {
            model.setAsset3DModel(null);
        }
        
        model.setAssetNameKo(request.getAssetNameKo());
        model.setAssetNameEn(request.getAssetNameEn());
        model.setManufacturer(request.getManufacturer());
        model.setModelNumber(request.getModelNumber());
        model.setSerialNumber(request.getSerialNumber());
        model.setInstallationDate(request.getInstallationDate());
        model.setCustomAttributes(request.getCustomAttributes());
        model.setStatus(request.getStatus());
        model.setEnabled(request.getEnabled());
        
        model = repository.save(model);
        log.info("장비 모델 수정: {} ({})", model.getAssetNameKo(), model.getAssetCode());
        
        return toDto(model);
    }
    
    /**
     * 모델 삭제
     */
    @Transactional
    public void deleteModel(Long id) {
        DeviceModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + id));
        
        repository.delete(model);
        log.info("장비 모델 삭제: {} ({})", model.getAssetNameKo(), model.getAssetCode());
    }
    
    /**
     * Entity -> DTO 변환
     */
    private DeviceModelDTO toDto(DeviceModel model) {
        DeviceModelDTO dto = DeviceModelDTO.builder()
                .id(model.getId())
                .assetCode(model.getAssetCode())
                .assetNameKo(model.getAssetNameKo())
                .assetNameEn(model.getAssetNameEn())
                .categoryId(model.getCategory().getId())
                .categoryCode(model.getCategory().getCategoryCode())
                .categoryNameKo(model.getCategory().getCategoryNameKo())
                .categoryPath(buildCategoryPath(model.getCategory()))
                .systemTypeId(model.getSystemType().getId())
                .sysCode(model.getSystemType().getSysCode())
                .sysNameKo(model.getSystemType().getSysNameKo())
                .manufacturer(model.getManufacturer())
                .modelNumber(model.getModelNumber())
                .serialNumber(model.getSerialNumber())
                .installationDate(model.getInstallationDate())
                .customAttributes(model.getCustomAttributes())
                .status(model.getStatus())
                .enabled(model.getEnabled())
                .createdBy(model.getCreatedBy())
                .updatedBy(model.getUpdatedBy())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
        
        // Asset3DModel 정보
        if (model.getAsset3DModel() != null) {
            dto.setAsset3DModelId(model.getAsset3DModel().getId());
            dto.setModelName(model.getAsset3DModel().getModelName());
            dto.setThumbnailUrl(model.getAsset3DModel().getThumbnailUrl());
        }
        
        return dto;
    }
    
    /**
     * 카테고리 전체 경로 생성
     */
    private String buildCategoryPath(Category category) {
        List<String> path = new ArrayList<>();
        Category current = category;
        
        while (current != null) {
            path.add(0, current.getCategoryNameKo());
            current = current.getParent();
        }
        
        return String.join(" > ", path);
    }
}

