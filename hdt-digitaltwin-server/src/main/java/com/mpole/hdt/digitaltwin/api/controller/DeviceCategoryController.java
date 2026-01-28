package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.devicecategory.DeviceCategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.request.DeviceCategoryRequest;
import com.mpole.hdt.digitaltwin.application.service.DeviceCategoryService;
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
public class DeviceCategoryController {

    private final DeviceCategoryService categoryService;

    /**
     * 전체 계층 구조 조회
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse> getCategoryTree() {
        log.info("카테고리 트리 조회 요청");

        List<DeviceCategoryDTO> tree = categoryService.getTreeStructure();
        return ResponseEntity.ok(ApiResponse.success("카테고리 트리 조회 성공", tree));
    }

    /**
     * 대분류 목록 조회
     * GET /api/categories/root
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse> getRootCategories() {
        log.info("대분류 목록 조회 요청");
        List<DeviceCategoryDTO> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success("대분류 목록 조회 성공", categories));
    }

    /**
     * 특정 부모의 자식 목록 조회
     * GET /api/categories/{parentId}/children
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse>  getChildrenCategories(@PathVariable Long parentId) {
        log.info("자식 카테고리 조회 요청: parentId={}", parentId);
        List<DeviceCategoryDTO> categories = categoryService.getChildrenCategories(parentId);
        return ResponseEntity.ok(ApiResponse.success("자식 카테고리 조회 성공", categories));
    }

    /**
     * 특정 깊이의 카테고리 목록 조회
     * GET /api/categories?depth=0
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCategoriesByDepth(@RequestParam(required = false) Integer depth) {
        if (depth != null) {
            log.info("카테고리 깊이별 조회 요청: depth={}", depth);
            List<DeviceCategoryDTO> categories = categoryService.getCategoriesByDepth(depth);
            return ResponseEntity.ok(ApiResponse.success(depth + "레벨 카테고리 조회 성공", categories));
        } else {
            log.warn("깊이 파라미터가 없습니다");
            return ResponseEntity.badRequest().body(ApiResponse.error("depth 파라미터가 필요합니다", "INVALID_REQUEST"));
        }
    }

    /**
     * ID로 카테고리 조회
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceCategoryDTO>> getCategoryById(@PathVariable Long id) {
        log.info("카테고리 상세 조회 요청: id={}", id);
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.success("카테고리 조회 성공", category)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("카테고리를 찾을 수 없습니다", "NOT_FOUND")));
    }

    /**
     * 검색
     * GET /api/categories/search?keyword=제어
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> getRootCategories(@RequestParam String keyword) {
        log.info("카테고리 검색 요청: keyword={}", keyword);
        List<DeviceCategoryDTO> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", categories));
    }

    /**
     * 카테고리 생성
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody DeviceCategoryRequest request) {
        log.info("카테고리 생성 요청: {}", request.getCategoryNameKo());
        DeviceCategoryDTO created = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 생성 성공", created));
    }

    /**
     * 카테고리 수정
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id,
                                                      @Valid @RequestBody DeviceCategoryRequest request) {

        log.info("카테고리 수정 요청: id={}", id);
        DeviceCategoryDTO updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 수정 성공", updated));
    }

    /**
     * 카테고리 삭제
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        log.info("카테고리 삭제 요청: id={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제 성공"));
    }


}
