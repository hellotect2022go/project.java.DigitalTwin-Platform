package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.devicemodel.DeviceModelDTO;
import com.mpole.hdt.digitaltwin.api.dto.devicemodel.DeviceModelRequest;
import com.mpole.hdt.digitaltwin.application.service.DeviceModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/device-models")
@RequiredArgsConstructor
@Slf4j
public class DeviceModelController {
    
    private final DeviceModelService service;
    
    /**
     * 전체 조회
     * GET /api/device-models
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceModelDTO>>> getAllModels(
            @RequestParam(required = false, defaultValue = "false") Boolean enabledOnly) {
        log.info("장비 모델 전체 조회 (enabledOnly={})", enabledOnly);
        
        List<DeviceModelDTO> models = enabledOnly 
                ? service.getEnabledModels() 
                : service.getAllModels();
        
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
    
    /**
     * ID로 조회
     * GET /api/device-models/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceModelDTO>> getModelById(@PathVariable Long id) {
        log.info("장비 모델 조회: id={}", id);
        DeviceModelDTO model = service.getModelById(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", model));
    }
    
    /**
     * Asset 코드로 조회
     * GET /api/device-models/code/{assetCode}
     */
    @GetMapping("/code/{assetCode}")
    public ResponseEntity<ApiResponse<DeviceModelDTO>> getModelByAssetCode(@PathVariable String assetCode) {
        log.info("장비 모델 조회: assetCode={}", assetCode);
        DeviceModelDTO model = service.getModelByAssetCode(assetCode);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", model));
    }
    
    /**
     * 카테고리별 조회
     * GET /api/device-models/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<DeviceModelDTO>>> getModelsByCategory(
            @PathVariable Long categoryId) {
        log.info("장비 모델 카테고리별 조회: categoryId={}", categoryId);
        List<DeviceModelDTO> models = service.getModelsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
    
    /**
     * 시스템 타입별 조회
     * GET /api/device-models/system-type/{systemTypeId}
     */
    @GetMapping("/system-type/{systemTypeId}")
    public ResponseEntity<ApiResponse<List<DeviceModelDTO>>> getModelsBySystemType(
            @PathVariable Long systemTypeId) {
        log.info("장비 모델 시스템 타입별 조회: systemTypeId={}", systemTypeId);
        List<DeviceModelDTO> models = service.getModelsBySystemType(systemTypeId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
    
    /**
     * 검색
     * GET /api/device-models/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceModelDTO>>> searchModels(
            @RequestParam String keyword) {
        log.info("장비 모델 검색: keyword={}", keyword);
        List<DeviceModelDTO> models = service.searchModels(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", models));
    }
    
    /**
     * 모델 생성
     * POST /api/device-models
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceModelDTO>> createModel(
            @Valid @RequestBody DeviceModelRequest request) {
        log.info("장비 모델 생성: assetCode={}, assetNameKo={}", 
                request.getAssetCode(), request.getAssetNameKo());
        DeviceModelDTO model = service.createModel(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", model));
    }
    
    /**
     * 모델 수정
     * PUT /api/device-models/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceModelDTO>> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody DeviceModelRequest request) {
        log.info("장비 모델 수정: id={}, assetCode={}", id, request.getAssetCode());
        DeviceModelDTO model = service.updateModel(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", model));
    }
    
    /**
     * 모델 삭제
     * DELETE /api/device-models/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModel(@PathVariable Long id) {
        log.info("장비 모델 삭제: id={}", id);
        service.deleteModel(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }
}

