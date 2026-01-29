package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.request.DeviceSystemTypeRequest;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.DeviceSystemTypeDTO;
import com.mpole.hdt.digitaltwin.application.service.DeviceSystemTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/system-types")
@RequiredArgsConstructor
public class DeviceSystemTypeController {

    private final DeviceSystemTypeService systemTypeService;

    /**
     * 시스템 타입 생성
     * POST /api/system-types
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceSystemTypeDTO>> createSystemType(
            @Valid @RequestBody DeviceSystemTypeRequest request) {
        try {
            DeviceSystemTypeDTO systemType = systemTypeService.createSystemType(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("시스템 타입이 생성되었습니다", systemType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("시스템 타입 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 생성에 실패했습니다", null));
        }
    }

    /**
     * 시스템 타입 수정
     * PUT /api/system-types/{systemId}
     */
    @PutMapping("/{systemId}")
    public ResponseEntity<ApiResponse<DeviceSystemTypeDTO>> updateSystemType(
            @PathVariable Long systemId,
            @Valid @RequestBody DeviceSystemTypeRequest request) {
        try {
            DeviceSystemTypeDTO systemType = systemTypeService.updateSystemType(systemId, request);
            return ResponseEntity.ok(ApiResponse.success("시스템 타입이 수정되었습니다", systemType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("시스템 타입 수정 실패: {}", systemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 수정에 실패했습니다", null));
        }
    }

    /**
     * 시스템 타입 삭제
     * DELETE /api/system-types/{systemId}
     */
    @DeleteMapping("/{systemId}")
    public ResponseEntity<ApiResponse<Void>> deleteSystemType(@PathVariable Long systemId) {
        try {
            systemTypeService.deleteSystemType(systemId);
            return ResponseEntity.ok(ApiResponse.success("시스템 타입이 삭제되었습니다", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("시스템 타입 삭제 실패: {}", systemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 삭제에 실패했습니다", null));
        }
    }

    /**
     * 시스템 타입 단건 조회 (ID)
     * GET /api/system-types/{systemId}
     */
    @GetMapping("/{systemId}")
    public ResponseEntity<ApiResponse<DeviceSystemTypeDTO>> getSystemType(@PathVariable Long systemId) {
        try {
            DeviceSystemTypeDTO systemType = systemTypeService.getSystemType(systemId);
            return ResponseEntity.ok(ApiResponse.success("시스템 타입 조회 성공", systemType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("시스템 타입 조회 실패: {}", systemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 조회에 실패했습니다", null));
        }
    }

    /**
     * 시스템 타입 조회 (코드)
     * GET /api/system-types/code/{sysCode}
     */
    @GetMapping("/code/{sysCode}")
    public ResponseEntity<ApiResponse<DeviceSystemTypeDTO>> getSystemTypeByCode(@PathVariable String sysCode) {
        try {
            DeviceSystemTypeDTO systemType = systemTypeService.getSystemTypeByCode(sysCode);
            return ResponseEntity.ok(ApiResponse.success("시스템 타입 조회 성공", systemType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("시스템 타입 조회 실패 (코드: {})", sysCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 조회에 실패했습니다", null));
        }
    }

    /**
     * 전체 시스템 타입 목록 조회
     * GET /api/system-types
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceSystemTypeDTO>>> getAllSystemTypes(
            @RequestParam(required = false) Boolean enabledOnly) {
        try {
            List<DeviceSystemTypeDTO> systemTypes;
            
            if (Boolean.TRUE.equals(enabledOnly)) {
                systemTypes = systemTypeService.getEnabledSystemTypes();
            } else {
                systemTypes = systemTypeService.getAllSystemTypes();
            }
            
            return ResponseEntity.ok(ApiResponse.success("시스템 타입 목록 조회 성공", systemTypes));
        } catch (Exception e) {
            log.error("시스템 타입 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("시스템 타입 목록 조회에 실패했습니다", null));
        }
    }
}

