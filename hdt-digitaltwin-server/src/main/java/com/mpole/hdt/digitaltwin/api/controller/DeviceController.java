package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.device.*;
import com.mpole.hdt.digitaltwin.api.dto.devicemodel.DeviceModelDTO;
import com.mpole.hdt.digitaltwin.application.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Device Controller
 * Device와 DevicePlacement를 통합 관리하는 API
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {
    
    private final DeviceService service;
    
    // ========================================
    // Device CRUD
    // ========================================
    
    /**
     * 전체 장비 조회
     * GET /api/devices
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> getAllDevices(
            @RequestParam(required = false, defaultValue = "false") Boolean enabledOnly,
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) String status) {
        log.info("장비 조회: enabledOnly={}, floor={}, status={}", enabledOnly, floor, status);
        
        List<DeviceDTO> devices;
        
        if (floor != null) {
            devices = service.getDevicesByFloor(floor);
        } else if (status != null) {
            devices = service.getDevicesByStatus(status);
        } else if (enabledOnly) {
            devices = service.getEnabledDevices();
        } else {
            devices = service.getAllDevices();
        }
        
        return ResponseEntity.ok(ApiResponse.success("조회 성공", devices));
    }
    
    /**
     * ID로 장비 조회
     * GET /api/devices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceDTO>> getDeviceById(@PathVariable Long id) {
        log.info("장비 조회: id={}", id);
        DeviceDTO device = service.getDeviceById(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", device));
    }
    
    /**
     * Device ID로 장비 조회
     * GET /api/devices/code/{deviceId}
     */
    @GetMapping("/code/{deviceCode}")
    public ResponseEntity<ApiResponse<DeviceDTO>> getDeviceByDeviceId(@PathVariable String deviceCode) {
        log.info("장비 조회: deviceId={}", deviceCode);
        DeviceDTO device = service.getDeviceByDeviceCode(deviceCode);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", device));
    }
    
    /**
     * DeviceModel별 장비 조회
     * GET /api/devices/model/{deviceModelId}
     */
    @GetMapping("/model/{deviceModelId}")
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> getDevicesByModel(
            @PathVariable Long deviceModelId) {
        log.info("모델별 장비 조회: deviceModelId={}", deviceModelId);
        List<DeviceDTO> devices = service.getDevicesByModel(deviceModelId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", devices));
    }
    
    /**
     * 검색
     * GET /api/devices/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> searchDevices(
            @RequestParam String keyword) {
        log.info("장비 검색: keyword={}", keyword);
        List<DeviceDTO> devices = service.searchDevices(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", devices));
    }
    
    /**
     * 장비 생성
     * POST /api/devices
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceDTO>> createDevice(
            @Valid @RequestBody DeviceRequest request) {
        log.info("장비 생성: deviceId={}, deviceName={}", 
                request.getDeviceCode(), request.getDeviceName());
        DeviceDTO device = service.createDevice(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", device));
    }
    
    /**
     * 장비 수정
     * PUT /api/devices/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceDTO>> updateDevice(
            @PathVariable Long id,
            @Valid @RequestBody DeviceRequest request) {
        log.info("장비 수정: id={}, deviceCode={}", id, request.getDeviceCode());
        DeviceDTO device = service.updateDevice(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", device));
    }
    
    /**
     * 장비 삭제
     * DELETE /api/devices/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDevice(@PathVariable Long id) {
        log.info("장비 삭제: id={}", id);
        service.deleteDevice(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }
    
    // ========================================
    // DevicePlacement 관리
    // ========================================
    
    /**
     * 장비 배치 정보 조회
     * GET /api/devices/{deviceId}/placement
     */
    @GetMapping("/{deviceId}/placement")
    public ResponseEntity<ApiResponse<DevicePlacementDTO>> getPlacement(
            @PathVariable Long deviceId) {
        log.info("장비 배치 정보 조회: deviceId={}", deviceId);
        DevicePlacementDTO placement = service.getPlacementByDeviceId(deviceId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", placement));
    }
    
    /**
     * 장비 배치 정보 생성/수정
     * PUT /api/devices/{deviceId}/placement
     */
    @PutMapping("/{deviceId}/placement")
    public ResponseEntity<ApiResponse<DevicePlacementDTO>> savePlacement(
            @PathVariable Long deviceId,
            @Valid @RequestBody DevicePlacementRequest request) {
        log.info("장비 배치 정보 저장: deviceId={}", deviceId);
        request.setDeviceId(deviceId);
        DevicePlacementDTO placement = service.savePlacement(request);
        return ResponseEntity.ok(ApiResponse.success("저장 성공", placement));
    }

    /**
     * 카테고리별 조회
     * GET /api/devices/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<DeviceByCategoryDTO>>> getModelsByCategory(
            @PathVariable Long categoryId) {
        log.info("장비 카테고리별 조회: categoryId={}", categoryId);
        List<DeviceByCategoryDTO> models = service.getDevicesByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
}

