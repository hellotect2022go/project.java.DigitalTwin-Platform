package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.Location.LocationResponse;
import com.mpole.hdt.digitaltwin.api.dto.device.ChangeDevicePlacementRequest;
import com.mpole.hdt.digitaltwin.api.dto.device.DeviceCategoryDTO;
import com.mpole.hdt.digitaltwin.api.dto.device.DeviceDTO;
import com.mpole.hdt.digitaltwin.api.dto.device.DevicePlacementResponse;
import com.mpole.hdt.digitaltwin.external.model.ExternalMSViewEntity;
import com.mpole.hdt.digitaltwin.external.repository.MssqlRepository;
import com.mpole.hdt.digitaltwin.service.DeviceCategoryService;
import com.mpole.hdt.digitaltwin.service.DeviceService;
import com.mpole.hdt.digitaltwin.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final MssqlRepository mssqlRepository;
    private final SyncService syncService;
    private final DeviceService deviceService;
    private final DeviceCategoryService deviceCategoryService;

    /**
     * view 테이블 장치 조회
     */
    @GetMapping
    public ResponseEntity getViewTableDevices() {
        log.info("view 테이블 장비 조회");

        List<ExternalMSViewEntity> list = mssqlRepository.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * 장치 동기화
     */
    @GetMapping("/sync")
    public ResponseEntity syncDevice() {
        log.info("장비 동기화 시작합니다.");
        syncService.syncInBatches();
        return ResponseEntity.ok("동기화가 완료되었습니다.");
    }


    /**
     * 장비 갯수 조회 
     */
    @GetMapping("/cnt-all")
    public ResponseEntity<ApiResponse<Long>> getAllDevicesCnt() {
        Long cnt = deviceService.getAllDevicesCnt();
        return ResponseEntity.ok(ApiResponse.success("장비 갯수 조회", cnt));
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> getAllDevices() {
        List<DeviceDTO> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(ApiResponse.success("장비 목록 조회", devices));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceDTO>> getTargetDevice(@PathVariable Long deviceId) {
        DeviceDTO device = deviceService.getTargetDevice(deviceId);
        return ResponseEntity.ok(ApiResponse.success("장비 정보 조회", device));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> getSearchDevices(
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) String zone
    ) {
        List<DeviceDTO> devices = deviceService.getSearchDevices(floor, zone);
        return ResponseEntity.ok(ApiResponse.success("층, 구역 별 장비 목록 조회", devices));
    }

    @GetMapping("/device-categories")
    public ResponseEntity<ApiResponse<List<DeviceCategoryDTO>>> getCategoryInfos() {
        List<DeviceCategoryDTO> response = deviceCategoryService.getCtegoryInfoTree();
        return ResponseEntity.ok(ApiResponse.success("장비 카테고리 목록 조회",response));
    }

    @GetMapping("/device-categories/{categoryId}/devices")
    public ResponseEntity<ApiResponse<List<DeviceDTO>>> getCategoryInfos(@PathVariable int categoryId) {
        List<DeviceDTO> response = deviceService.getTargetCategoryDevices(categoryId);
        return ResponseEntity.ok(ApiResponse.success("카테고리 별 장비 목록 조회",response));
    }

    // Unity 에서 맵별로 건물 > 층 > 구역 > 상세 구역  과 관련된 정보를 내려주는 api
    @GetMapping("/location-info")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocationInfos() {
        List<LocationResponse> response = deviceService.getLocationInfoAll();
        return ResponseEntity.ok(ApiResponse.success("전체 구역 목록 정보 조회",response));
    }


    // ========================================
    // 장비 배치 정보 관련된 API 항목들
    // ========================================
    @PutMapping("/placement")
    public ResponseEntity<ApiResponse<DevicePlacementResponse>> saveDevicesPlacement(@RequestBody ChangeDevicePlacementRequest request) {
        DevicePlacementResponse device = deviceService.savePlacementInfo(request);
        return ResponseEntity.ok(ApiResponse.success("장비 배치정보 업데이트",device));
    }


    @DeleteMapping("/placement")
    public ResponseEntity<ApiResponse> deleteDevicesPlacement(@RequestBody Map<String, List<Long>> request) {
        List<Long> deviceIds = request.get("deviceIds");
        deviceService.deletePlacementInfo(deviceIds);
        return ResponseEntity.ok(ApiResponse.success("장비 배치정보 삭제"));
    }



}
