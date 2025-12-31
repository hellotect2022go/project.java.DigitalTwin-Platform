package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinDataDto;
import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinResponseDto;
import com.mpole.hdt.digitaltwin.application.service.DigitalTwinMockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 디지털 트윈 REST API 컨트롤러
 * Unity에서 HTTP 통신으로 데이터 조회 시 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/digitaltwin")
@RequiredArgsConstructor
public class DigitalTwinRestController {

    private final DigitalTwinMockService mockService;

    /**
     * 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("DigitalTwin Server is running!");
    }

    /**
     * 모든 디지털 트윈 데이터 조회
     * GET /api/digitaltwin/data
     */
    @GetMapping("/data")
    public ResponseEntity<DigitalTwinResponseDto> getAllData() {
        log.info("===== 전체 데이터 조회 요청 =====");
        
        List<DigitalTwinDataDto> dataList = mockService.getAllData();
        
        DigitalTwinResponseDto response = DigitalTwinResponseDto.builder()
                .success(true)
                .message("데이터 조회 성공")
                .data(dataList)
                .totalCount(dataList.size())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 자산 데이터 조회
     * GET /api/digitaltwin/data/{assetId}
     */
    @GetMapping("/data/{assetId}")
    public ResponseEntity<DigitalTwinResponseDto> getDataByAssetId(@PathVariable String assetId) {
        log.info("===== 자산 ID로 데이터 조회: {} =====", assetId);
        
        Optional<DigitalTwinDataDto> data = mockService.getDataByAssetId(assetId);
        
        if (data.isPresent()) {
            DigitalTwinResponseDto response = DigitalTwinResponseDto.builder()
                    .success(true)
                    .message("데이터 조회 성공")
                    .data(List.of(data.get()))
                    .totalCount(1)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 자산 유형별 데이터 조회
     * GET /api/digitaltwin/data/type/{assetType}
     */
    @GetMapping("/data/type/{assetType}")
    public ResponseEntity<DigitalTwinResponseDto> getDataByAssetType(@PathVariable String assetType) {
        log.info("===== 자산 유형별 데이터 조회: {} =====", assetType);
        
        List<DigitalTwinDataDto> dataList = mockService.getDataByAssetType(assetType);
        
        DigitalTwinResponseDto response = DigitalTwinResponseDto.builder()
                .success(true)
                .message("데이터 조회 성공")
                .data(dataList)
                .totalCount(dataList.size())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * 장비 상태별 데이터 조회
     * GET /api/digitaltwin/data/status/{status}
     */
    @GetMapping("/data/status/{status}")
    public ResponseEntity<DigitalTwinResponseDto> getDataByStatus(@PathVariable String status) {
        log.info("===== 장비 상태별 데이터 조회: {} =====", status);
        
        List<DigitalTwinDataDto> dataList = mockService.getDataByStatus(status);
        
        DigitalTwinResponseDto response = DigitalTwinResponseDto.builder()
                .success(true)
                .message("데이터 조회 성공")
                .data(dataList)
                .totalCount(dataList.size())
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * 장비 상태 변경 (테스트용)
     * PUT /api/digitaltwin/equipment/{assetId}/status
     */
    @PutMapping("/equipment/{assetId}/status")
    public ResponseEntity<String> updateEquipmentStatus(
            @PathVariable String assetId,
            @RequestParam String status) {
        log.info("===== 장비 상태 변경 요청: {} -> {} =====", assetId, status);
        
        boolean success = mockService.updateEquipmentStatus(assetId, status);
        
        if (success) {
            return ResponseEntity.ok("상태 변경 성공: " + assetId + " -> " + status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

