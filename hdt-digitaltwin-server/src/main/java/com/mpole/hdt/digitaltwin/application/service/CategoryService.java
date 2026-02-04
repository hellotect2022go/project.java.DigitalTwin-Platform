package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.category.CategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.category.CategoryRequest;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeDTO;
import com.mpole.hdt.digitaltwin.application.repository.CategoryRepository;
import com.mpole.hdt.digitaltwin.application.repository.CategorySystemMappingRepository;
import com.mpole.hdt.digitaltwin.application.repository.SystemTypeRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.Category;
import com.mpole.hdt.digitaltwin.application.repository.entity.CategorySystemMapping;
import com.mpole.hdt.digitaltwin.application.repository.entity.SystemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategorySystemMappingRepository mappingRepository;
    private final SystemTypeRepository systemTypeRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public CategoryDTO createCategory(CategoryRequest request) {
        // 코드 중복 체크
        if (categoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 코드입니다: " + request.getCategoryCode());
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다"));
        }

        Category category = Category.builder()
                .parent(parent)
                .depth(request.getDepth())
                .categoryCode(request.getCategoryCode())
                .categoryNameKo(request.getCategoryNameKo())
                .categoryNameEn(request.getCategoryNameEn())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .enabled(request.getEnabled())
                .build();

        category = categoryRepository.save(category);
        log.info("카테고리 생성: {} (코드: {})", category.getCategoryNameKo(), category.getCategoryCode());

        return toDTO(category);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        // 코드 변경 시 중복 체크
        if (!category.getCategoryCode().equals(request.getCategoryCode())) {
            if (categoryRepository.existsByCategoryCode(request.getCategoryCode())) {
                throw new IllegalArgumentException("이미 존재하는 카테고리 코드입니다: " + request.getCategoryCode());
            }
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 카테고리를 찾을 수 없습니다"));
        }

        category.setParent(parent);
        category.setDepth(request.getDepth());
        category.setCategoryCode(request.getCategoryCode());
        category.setCategoryNameKo(request.getCategoryNameKo());
        category.setCategoryNameEn(request.getCategoryNameEn());
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setEnabled(request.getEnabled());

        category = categoryRepository.save(category);
        log.info("카테고리 수정: {} (코드: {})", category.getCategoryNameKo(), category.getCategoryCode());

        return toDTO(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        // 매핑 먼저 삭제
        mappingRepository.deleteByCategoryId(id);

        // 카테고리 삭제 (cascade로 자식도 삭제됨)
        categoryRepository.delete(category);
        log.info("카테고리 삭제: {} (ID: {})", category.getCategoryNameKo(), id);
    }

    /**
     * 카테고리 단건 조회
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));
        return toDTO(category);
    }

    /**
     * 전체 카테고리 트리 구조 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findAllWithParent();
        return buildTree(allCategories);
    }

    /**
     * depth별 카테고리 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByDepth(Integer depth) {
        return categoryRepository.findByDepthOrderByDisplayOrderAsc(depth).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 검색
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> searchCategories(String keyword) {
        return categoryRepository.searchCategories(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리의 SystemType 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SystemTypeDTO> getSystemTypesByCategory(Long categoryId) {
        List<CategorySystemMapping> mappings = mappingRepository.findByCategory_Id(categoryId);
        return mappings.stream()
                .map(mapping -> toSystemTypeDTO(mapping.getSystemType()))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리-SystemType 매핑 저장
     */
    @Transactional
    public void updateCategorySystemTypes(Long categoryId, List<Long> systemTypeIds) {
        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        // 기존 매핑 삭제
        mappingRepository.deleteByCategoryId(categoryId);

        // 새 매핑 생성
        if (systemTypeIds != null && !systemTypeIds.isEmpty()) {
            for (Long systemTypeId : systemTypeIds) {
                SystemType systemType = systemTypeRepository.findById(systemTypeId)
                        .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + systemTypeId));

                CategorySystemMapping mapping = CategorySystemMapping.builder()
                        .category(category)
                        .systemType(systemType)
                        .isDefault(false)
                        .build();

                mappingRepository.save(mapping);
            }
        }

        log.info("카테고리 시스템 타입 매핑 업데이트: categoryId={}, count={}", 
                 categoryId, systemTypeIds != null ? systemTypeIds.size() : 0);
    }

    /**
     * 트리 구조 생성
     */
    private List<CategoryDTO> buildTree(List<Category> categories) {
        Map<Long, CategoryDTO> dtoMap = new HashMap<>();
        List<CategoryDTO> roots = new ArrayList<>();

        // DTO 변환
        for (Category category : categories) {
            CategoryDTO dto = toDTO(category);
            dtoMap.put(category.getId(), dto);
        }

        // 트리 구조 구성
        for (Category category : categories) {
            CategoryDTO dto = dtoMap.get(category.getId());
            if (category.getParent() == null) {
                roots.add(dto);
            } else {
                CategoryDTO parentDto = dtoMap.get(category.getParent().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        }

        // 정렬
        sortTree(roots);
        return roots;
    }

    /**
     * 트리 재귀 정렬
     */
    private void sortTree(List<CategoryDTO> categories) {
        if (categories == null || categories.isEmpty()) return;
        
        categories.sort(Comparator.comparing(CategoryDTO::getDisplayOrder));
        for (CategoryDTO category : categories) {
            sortTree(category.getChildren());
        }
    }

    /**
     * Entity -> DTO 변환
     */
    private CategoryDTO toDTO(Category entity) {
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

    /**
     * SystemType Entity -> DTO 변환
     */
    private SystemTypeDTO toSystemTypeDTO(SystemType entity) {
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
}

