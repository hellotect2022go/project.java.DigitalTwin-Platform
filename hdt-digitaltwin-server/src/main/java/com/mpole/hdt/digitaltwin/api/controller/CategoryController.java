package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.category.CategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.category.CategoryRequest;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeDTO;
import com.mpole.hdt.digitaltwin.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 생성
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("카테고리 생성 요청: {}", request.getCategoryNameKo());
        CategoryDTO created = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 생성 성공", created));
    }

    /**
     * 카테고리 수정
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.info("카테고리 수정 요청: id={}", id);
        CategoryDTO updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 수정 성공", updated));
    }

    /**
     * 카테고리 삭제
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("카테고리 삭제 요청: id={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제 성공", null));
    }

    /**
     * 카테고리 단건 조회
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategory(@PathVariable Long id) {
        log.info("카테고리 조회 요청: id={}", id);
        CategoryDTO category = categoryService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 조회 성공", category));
    }

    /**
     * 전체 카테고리 트리 구조 조회
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoryTree() {
        log.info("카테고리 트리 조회 요청");
        List<CategoryDTO> tree = categoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.success("카테고리 트리 조회 성공", tree));
    }

    /**
     * depth별 카테고리 조회
     * GET /api/categories?depth=0
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesByDepth(
            @RequestParam(required = false) Integer depth) {
        if (depth != null) {
            log.info("카테고리 depth별 조회 요청: depth={}", depth);
            List<CategoryDTO> categories = categoryService.getCategoriesByDepth(depth);
            return ResponseEntity.ok(ApiResponse.success("카테고리 조회 성공", categories));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("depth 파라미터가 필요합니다", null));
    }

    /**
     * 카테고리 검색
     * GET /api/categories/search?keyword=검색어
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> searchCategories(
            @RequestParam String keyword) {
        log.info("카테고리 검색 요청: keyword={}", keyword);
        List<CategoryDTO> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(ApiResponse.success("카테고리 검색 성공", categories));
    }

    /**
     * 카테고리의 SystemType 목록 조회
     * GET /api/categories/{id}/system-types
     */
    @GetMapping("/{id}/system-types")
    public ResponseEntity<ApiResponse<List<SystemTypeDTO>>> getCategorySystemTypes(@PathVariable Long id) {
        log.info("카테고리 시스템 타입 조회: categoryId={}", id);
        List<SystemTypeDTO> systemTypes = categoryService.getSystemTypesByCategory(id);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 조회 성공", systemTypes));
    }

    /**
     * 카테고리-SystemType 매핑 저장
     * PUT /api/categories/{id}/system-types
     */
    @PutMapping("/{id}/system-types")
    public ResponseEntity<ApiResponse<Void>> updateCategorySystemTypes(
            @PathVariable Long id,
            @RequestBody List<Long> systemTypeIds) {
        log.info("카테고리 시스템 타입 매핑 업데이트: categoryId={}, systemTypeIds={}", id, systemTypeIds);
        categoryService.updateCategorySystemTypes(id, systemTypeIds);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 매핑 저장 성공", null));
    }
}

