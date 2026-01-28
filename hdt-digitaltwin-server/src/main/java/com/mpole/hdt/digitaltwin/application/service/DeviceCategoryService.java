package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.devicecategory.DeviceCategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.request.DeviceCategoryRequest;
import com.mpole.hdt.digitaltwin.application.repository.DeviceCategoryRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceCategory;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceCategoryService {

    private final DeviceCategoryRepository categoryRepository;

    /**
     * 전체 계층 구조 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> getTreeStructure() {
        List<DeviceCategory> allCategories = categoryRepository.findAllRecursive();
        return buildTree(allCategories);
    }

    /**
     * 대분류 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNullOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 특정 부모의 자식 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> getChildrenCategories(Long parentId) {
        return categoryRepository.findByParent_IdOrderByDisplayOrderAsc(parentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 특정 깊이의 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> getCategoriesByDepth(Integer depth) {
        return categoryRepository.findByDepthOrderByDisplayOrderAsc(depth)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * ID로 카테고리 조회
     */
    @Transactional(readOnly = true)
    public Optional<DeviceCategoryDTO> getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .map(this::toDtoWithFullPath);
    }

    /**
     * 검색
     */
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> searchCategories(String keyword) {
        return categoryRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toDtoWithFullPath)
                .toList();
    }

    /**
     * 카테고리 생성
     */
    @Transactional
    public DeviceCategoryDTO createCategory(DeviceCategoryRequest request) {
        DeviceCategory parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다."));
        }

        DeviceCategory category = DeviceCategory.builder()
                .parent(parent)
                .depth(request.getDepth())
                .categoryCode(request.getCategoryCode())
                .categoryNameKo(request.getCategoryNameKo())
                .categoryNameEn(request.getCategoryNameEn())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();

        category = categoryRepository.save(category);
        log.info("새 카테고리 생성: {} (ID: {})", category.getCategoryNameKo(), category.getId());

        return toDto(category);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public DeviceCategoryDTO updateCategory(Long catId, DeviceCategoryRequest request) {
        DeviceCategory category = categoryRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        DeviceCategory parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다."));
        }

        category.setParent(parent);
        category.setDepth(request.getDepth());
        category.setCategoryCode(request.getCategoryCode());
        category.setCategoryNameKo(request.getCategoryNameKo());
        category.setCategoryNameEn(request.getCategoryNameEn());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        category = categoryRepository.save(category);
        log.info("카테고리 수정: {} (ID: {})", category.getCategoryNameKo(), category.getId());

        return toDto(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long catId) {
        DeviceCategory category = categoryRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        categoryRepository.delete(category);
        log.info("카테고리 삭제: {} (ID: {})", category.getCategoryNameKo(), category.getId());
    }

    // ===== Private Methods =====

    private List<DeviceCategoryDTO> buildTree(List<DeviceCategory> allCategories) {
        Map<Long, DeviceCategoryDTO> categoryMap = allCategories.stream()
                .map(this::toDto)
                .collect(Collectors.toMap(DeviceCategoryDTO::getCategoryId, Function.identity()));

        List<DeviceCategoryDTO> rootCategories = new ArrayList<>();
        for (DeviceCategoryDTO dto : categoryMap.values()) {
            if (dto.getParentId() == null) {
                rootCategories.add(dto);
            } else {
                DeviceCategoryDTO parent = categoryMap.get(dto.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dto);
                }
            }
        }

        sortTree(rootCategories);
        return rootCategories;
    }

    private void sortTree(List<DeviceCategoryDTO> categories) {
        if (categories == null) return;
        categories.sort(Comparator.comparing(DeviceCategoryDTO::getDisplayOrder));
        for (DeviceCategoryDTO category : categories) {
            sortTree(category.getChildren());
        }
    }

    private DeviceCategoryDTO toDto(DeviceCategory entity) {
        if (entity == null) return null;

        return DeviceCategoryDTO.builder()
                .categoryId(entity.getId())
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
    }

    private DeviceCategoryDTO toDtoWithFullPath(DeviceCategory entity) {
        DeviceCategoryDTO dto = toDto(entity);
        dto.setFullPath(buildFullPath(entity));
        return dto;
    }

    // 부모가 null 이 될때까지 상위로 올라간다.
    public String buildFullPath(DeviceCategory category) {
        List<String> path = new ArrayList<>();
        DeviceCategory current = category;
        while (current != null) {
            path.add(0, current.getCategoryNameKo());
            current = current.getParent();
        }
        return String.join(" > ", path);
    }
}
