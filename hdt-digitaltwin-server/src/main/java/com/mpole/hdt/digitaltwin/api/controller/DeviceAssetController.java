package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.devicecategory.DeviceAssetDTO;
import com.mpole.hdt.digitaltwin.api.dto.request.DeviceAssetRequest;
import com.mpole.hdt.digitaltwin.application.service.DeviceAssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class DeviceAssetController {

    private final DeviceAssetService assetService;

    /**
     * 전체 에셋 조회
     * GET /api/assets
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> getAllAssets() {
        log.info("전체 에셋 조회 요청");
        List<DeviceAssetDTO> assets = assetService.getAllAssets();
        return ResponseEntity.ok(ApiResponse.success("에셋 목록 조회 성공", assets));
    }

    /**
     * ID로 에셋 조회
     * GET /api/assets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceAssetDTO>> getAssetById(@PathVariable Long id) {
        log.info("에셋 상세 조회 요청: id={}", id);
        return assetService.getAssetById(id)
                .map(asset -> ResponseEntity.ok(ApiResponse.success("에셋 조회 성공", asset)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error("에셋을 찾을 수 없습니다", "NOT_FOUND")));
    }

    /**
     * 에셋 코드로 조회
     * GET /api/assets/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DeviceAssetDTO>> getAssetByCode(@PathVariable String code) {
        log.info("에셋 코드로 조회 요청: code={}", code);
        return assetService.getAssetByCode(code)
                .map(asset -> ResponseEntity.ok(ApiResponse.success("에셋 조회 성공", asset)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error("에셋을 찾을 수 없습니다", "NOT_FOUND")));
    }

    /**
     * 시스템별 에셋 조회 (계층 구조 포함)
     * GET /api/assets/system/{sysCode}
     */
    @GetMapping("/system/{sysCode}")
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> getAssetsBySystemCode(@PathVariable String sysCode) {
        log.info("시스템별 에셋 조회 요청: sysCode={}", sysCode);
        List<DeviceAssetDTO> assets = assetService.getAssetsBySystemCode(sysCode);
        return ResponseEntity.ok(ApiResponse.success("시스템별 에셋 조회 성공", assets));
    }

    /**
     * 카테고리별 에셋 조회
     * GET /api/assets/category/{catId}
     */
    @GetMapping("/category/{catId}")
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> getAssetsByCategory(@PathVariable Long catId) {
        log.info("카테고리별 에셋 조회 요청: catId={}", catId);
        List<DeviceAssetDTO> assets = assetService.getAssetsByCategory(catId);
        return ResponseEntity.ok(ApiResponse.success("카테고리별 에셋 조회 성공", assets));
    }

    /**
     * 건물별 에셋 조회
     * GET /api/assets/building/{building}
     */
    @GetMapping("/building/{building}")
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> getAssetsByBuilding(@PathVariable String building) {
        log.info("건물별 에셋 조회 요청: building={}", building);
        List<DeviceAssetDTO> assets = assetService.getAssetsByBuilding(building);
        return ResponseEntity.ok(ApiResponse.success("건물별 에셋 조회 성공", assets));
    }

    /**
     * 건물+층별 에셋 조회
     * GET /api/assets/location?building=본관&floor=5F
     */
    @GetMapping("/location")
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> getAssetsByLocation(
            @RequestParam String building,
            @RequestParam String floor) {
        log.info("위치별 에셋 조회 요청: building={}, floor={}", building, floor);
        List<DeviceAssetDTO> assets = assetService.getAssetsByBuildingAndFloor(building, floor);
        return ResponseEntity.ok(ApiResponse.success("위치별 에셋 조회 성공", assets));
    }

    /**
     * 검색
     * GET /api/assets/search?keyword=패널
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DeviceAssetDTO>>> searchAssets(@RequestParam String keyword) {
        log.info("에셋 검색 요청: keyword={}", keyword);
        List<DeviceAssetDTO> assets = assetService.searchAssets(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", assets));
    }

    /**
     * 에셋 생성
     * POST /api/assets
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceAssetDTO>> createAsset(@Valid @RequestBody DeviceAssetRequest request) {
        log.info("에셋 생성 요청: {}", request.getAssetNameKo());
        DeviceAssetDTO created = assetService.createAsset(request);
        return ResponseEntity.ok(ApiResponse.success("에셋 생성 성공", created));
    }

    /**
     * 에셋 수정
     * PUT /api/assets/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceAssetDTO>> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody DeviceAssetRequest request) {
        log.info("에셋 수정 요청: id={}", id);
        DeviceAssetDTO updated = assetService.updateAsset(id, request);
        return ResponseEntity.ok(ApiResponse.success("에셋 수정 성공", updated));
    }

    /**
     * 에셋 삭제
     * DELETE /api/assets/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long id) {
        log.info("에셋 삭제 요청: id={}", id);
        assetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponse.success("에셋 삭제 성공"));
    }


}
