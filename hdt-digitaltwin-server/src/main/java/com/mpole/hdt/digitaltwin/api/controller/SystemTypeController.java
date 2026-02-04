package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.category.CategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeDTO;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.SystemTypeRequest;
import com.mpole.hdt.digitaltwin.application.service.SystemTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/system-types")
@RequiredArgsConstructor
public class SystemTypeController {

    private final SystemTypeService systemTypeService;

    /**
     * 시스템 타입 생성
     * POST /api/system-types
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SystemTypeDTO>> createSystemType(@Valid @RequestBody SystemTypeRequest request) {
        log.info("시스템 타입 생성 요청: {}", request.getSysNameKo());
        SystemTypeDTO created = systemTypeService.createSystemType(request);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 생성 성공", created));
    }

    /**
     * 시스템 타입 수정
     * PUT /api/system-types/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemTypeDTO>> updateSystemType(
            @PathVariable Long id,
            @Valid @RequestBody SystemTypeRequest request) {
        log.info("시스템 타입 수정 요청: id={}", id);
        SystemTypeDTO updated = systemTypeService.updateSystemType(id, request);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 수정 성공", updated));
    }

    /**
     * 시스템 타입 삭제
     * DELETE /api/system-types/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSystemType(@PathVariable Long id) {
        log.info("시스템 타입 삭제 요청: id={}", id);
        systemTypeService.deleteSystemType(id);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 삭제 성공", null));
    }

    /**
     * 시스템 타입 단건 조회
     * GET /api/system-types/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemTypeDTO>> getSystemType(@PathVariable Long id) {
        log.info("시스템 타입 조회 요청: id={}", id);
        SystemTypeDTO systemType = systemTypeService.getSystemType(id);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 조회 성공", systemType));
    }

    /**
     * 전체 시스템 타입 조회
     * GET /api/system-types
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemTypeDTO>>> getAllSystemTypes(
            @RequestParam(required = false, defaultValue = "false") boolean enabledOnly) {
        log.info("시스템 타입 목록 조회 요청: enabledOnly={}", enabledOnly);
        List<SystemTypeDTO> systemTypes = enabledOnly 
                ? systemTypeService.getEnabledSystemTypes()
                : systemTypeService.getAllSystemTypes();
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 조회 성공", systemTypes));
    }

    /**
     * 시스템 타입 검색
     * GET /api/system-types/search?keyword=검색어
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SystemTypeDTO>>> searchSystemTypes(
            @RequestParam String keyword) {
        log.info("시스템 타입 검색 요청: keyword={}", keyword);
        List<SystemTypeDTO> systemTypes = systemTypeService.searchSystemTypes(keyword);
        return ResponseEntity.ok(ApiResponse.success("시스템 타입 검색 성공", systemTypes));
    }

    /**
     * 시스템 타입의 카테고리 목록 조회
     * GET /api/system-types/{id}/categories
     */
    @GetMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getSystemTypeCategories(@PathVariable Long id) {
        log.info("시스템 타입 카테고리 조회: systemTypeId={}", id);
        List<CategoryDTO> categories = systemTypeService.getCategoriesBySystemType(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 조회 성공", categories));
    }
}

