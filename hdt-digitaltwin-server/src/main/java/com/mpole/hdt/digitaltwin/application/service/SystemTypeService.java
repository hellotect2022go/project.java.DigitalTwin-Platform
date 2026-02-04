package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.category.CategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeDTO;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeRequest;
import com.mpole.hdt.digitaltwin.application.repository.CategorySystemMappingRepository;
import com.mpole.hdt.digitaltwin.application.repository.SystemTypeRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.Category;
import com.mpole.hdt.digitaltwin.application.repository.entity.CategorySystemMapping;
import com.mpole.hdt.digitaltwin.application.repository.entity.SystemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemTypeService {

    private final SystemTypeRepository systemTypeRepository;
    private final CategorySystemMappingRepository mappingRepository;

    /**
     * 시스템 타입 생성
     */
    @Transactional
    public SystemTypeDTO createSystemType(SystemTypeRequest request) {
        // 코드 중복 체크
        if (systemTypeRepository.existsBySysCode(request.getSysCode())) {
            throw new IllegalArgumentException("이미 존재하는 시스템 코드입니다: " + request.getSysCode());
        }

        SystemType systemType = SystemType.builder()
                .sysCode(request.getSysCode())
                .sysNameKo(request.getSysNameKo())
                .sysNameEn(request.getSysNameEn())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .enabled(request.getEnabled())
                .build();

        systemType = systemTypeRepository.save(systemType);
        log.info("시스템 타입 생성: {} (코드: {})", systemType.getSysNameKo(), systemType.getSysCode());

        return toDTO(systemType);
    }

    /**
     * 시스템 타입 수정
     */
    @Transactional
    public SystemTypeDTO updateSystemType(Long id, SystemTypeRequest request) {
        SystemType systemType = systemTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다"));

        // 코드 변경 시 중복 체크
        if (!systemType.getSysCode().equals(request.getSysCode())) {
            if (systemTypeRepository.existsBySysCode(request.getSysCode())) {
                throw new IllegalArgumentException("이미 존재하는 시스템 코드입니다: " + request.getSysCode());
            }
        }

        systemType.setSysCode(request.getSysCode());
        systemType.setSysNameKo(request.getSysNameKo());
        systemType.setSysNameEn(request.getSysNameEn());
        systemType.setDescription(request.getDescription());
        systemType.setIconUrl(request.getIconUrl());
        systemType.setEnabled(request.getEnabled());

        systemType = systemTypeRepository.save(systemType);
        log.info("시스템 타입 수정: {} (코드: {})", systemType.getSysNameKo(), systemType.getSysCode());

        return toDTO(systemType);
    }

    /**
     * 시스템 타입 삭제
     */
    @Transactional
    public void deleteSystemType(Long id) {
        SystemType systemType = systemTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다"));

        // 매핑 먼저 삭제
        mappingRepository.deleteBySystemTypeId(id);

        // 시스템 타입 삭제
        systemTypeRepository.delete(systemType);
        log.info("시스템 타입 삭제: {} (ID: {})", systemType.getSysNameKo(), id);
    }

    /**
     * 시스템 타입 단건 조회
     */
    @Transactional(readOnly = true)
    public SystemTypeDTO getSystemType(Long id) {
        SystemType systemType = systemTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다"));
        return toDTO(systemType);
    }

    /**
     * 전체 시스템 타입 조회
     */
    @Transactional(readOnly = true)
    public List<SystemTypeDTO> getAllSystemTypes() {
        return systemTypeRepository.findAllByOrderBySysCodeAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 시스템 타입만 조회
     */
    @Transactional(readOnly = true)
    public List<SystemTypeDTO> getEnabledSystemTypes() {
        return systemTypeRepository.findByEnabledTrueOrderBySysCodeAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 시스템 타입 검색
     */
    @Transactional(readOnly = true)
    public List<SystemTypeDTO> searchSystemTypes(String keyword) {
        return systemTypeRepository.searchSystemTypes(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 시스템 타입의 카테고리 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesBySystemType(Long systemTypeId) {
        List<CategorySystemMapping> mappings = mappingRepository.findBySystemType_Id(systemTypeId);
        return mappings.stream()
                .map(mapping -> toCategoryDTO(mapping.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Entity -> DTO 변환
     */
    private SystemTypeDTO toDTO(SystemType entity) {
        return SystemTypeDTO.builder()
                .id(entity.getId())
                .sysCode(entity.getSysCode())
                .sysNameKo(entity.getSysNameKo())
                .sysNameEn(entity.getSysNameEn())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Category Entity -> DTO 변환
     */
    private CategoryDTO toCategoryDTO(Category entity) {
        CategoryDTO dto = CategoryDTO.builder()
                .id(entity.getId())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .depth(entity.getDepth())
                .categoryCode(entity.getCategoryCode())
                .categoryNameKo(entity.getCategoryNameKo())
                .categoryNameEn(entity.getCategoryNameEn())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // 전체 경로 생성
        dto.setFullPath(buildFullPath(entity));
        return dto;
    }

    /**
     * 전체 경로 생성
     */
    private String buildFullPath(Category category) {
        List<String> path = new ArrayList<>();
        Category current = category;
        while (current != null) {
            path.add(0, current.getCategoryNameKo());
            current = current.getParent();
        }
        return String.join(" > ", path);
    }
}

