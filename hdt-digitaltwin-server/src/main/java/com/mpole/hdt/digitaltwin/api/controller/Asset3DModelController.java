package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.asset3d.Asset3DModelDTO;
import com.mpole.hdt.digitaltwin.api.dto.asset3d.Asset3DModelRequest;
import com.mpole.hdt.digitaltwin.application.service.Asset3DModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/asset-3d-models")
@RequiredArgsConstructor
@Slf4j
public class Asset3DModelController {
    
    private final Asset3DModelService service;
    
    /**
     * 전체 조회
     * GET /api/asset-3d-models
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset3DModelDTO>>> getAllModels(
            @RequestParam(required = false, defaultValue = "false") Boolean enabledOnly) {
        log.info("3D 모델 전체 조회 요청 (enabledOnly={})", enabledOnly);
        
        List<Asset3DModelDTO> models = enabledOnly 
                ? service.getEnabledModels() 
                : service.getAllModels();
        
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
    
    /**
     * ID로 조회
     * GET /api/asset-3d-models/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset3DModelDTO>> getModelById(@PathVariable Long id) {
        log.info("3D 모델 조회: id={}", id);
        Asset3DModelDTO model = service.getModelById(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", model));
    }
    
    /**
     * 검색
     * GET /api/asset-3d-models/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Asset3DModelDTO>>> searchModels(
            @RequestParam String keyword) {
        log.info("3D 모델 검색: keyword={}", keyword);
        List<Asset3DModelDTO> models = service.searchModels(keyword);
        return ResponseEntity.ok(ApiResponse.success("검색 성공", models));
    }
    
    /**
     * 확장자별 조회
     * GET /api/asset-3d-models/extension/{extension}
     */
    @GetMapping("/extension/{extension}")
    public ResponseEntity<ApiResponse<List<Asset3DModelDTO>>> getModelsByExtension(
            @PathVariable String extension) {
        log.info("3D 모델 확장자별 조회: extension={}", extension);
        List<Asset3DModelDTO> models = service.getModelsByExtension(extension);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", models));
    }
    
    /**
     * 파일 업로드 및 모델 생성
     * POST /api/asset-3d-models/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Asset3DModelDTO>> uploadModel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") Asset3DModelRequest request) {

        log.info("3D 모델 업로드 요청: modelName={}, fileSize={}", 
                request.getModelName(), file.getSize());
        
        try {
            Asset3DModelDTO model = service.uploadModel(file, request);
            return ResponseEntity.ok(ApiResponse.success("업로드 성공", model));
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new IllegalStateException("파일 업로드 실패: " + e.getMessage());
        }
    }
    
    /**
     * 썸네일 업로드
     * POST /api/asset-3d-models/{id}/thumbnail
     */
    @PostMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Asset3DModelDTO>> uploadThumbnail(
            @PathVariable Long id,
            @RequestPart("thumbnail") MultipartFile thumbnail) {
        log.info("썸네일 업로드 요청: id={}, fileSize={}", id, thumbnail.getSize());
        
        try {
            Asset3DModelDTO model = service.uploadThumbnail(id, thumbnail);
            return ResponseEntity.ok(ApiResponse.success("썸네일 업로드 성공", model));
        } catch (IOException e) {
            log.error("썸네일 업로드 실패", e);
            throw new IllegalStateException("썸네일 업로드 실패" + e.getMessage());
        }
    }
    
    /**
     * 모델 정보 수정
     * PUT /api/asset-3d-models/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset3DModelDTO>> updateModel(
            @PathVariable Long id,
            @RequestBody Asset3DModelRequest request) {
        log.info("3D 모델 정보 수정: id={}, modelName={}", id, request.getModelName());
        Asset3DModelDTO model = service.updateModel(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", model));
    }
    
    /**
     * 모델 삭제
     * DELETE /api/asset-3d-models/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModel(@PathVariable Long id) {
        log.info("3D 모델 삭제: id={}", id);
        service.deleteModel(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }
    
    /**
     * 파일 다운로드
     * GET /api/asset-3d-models/download/{filename}
     */
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/3d-models").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 썸네일 조회
     * GET /api/asset-3d-models/thumbnail/{filename}
     */
    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/thumbnails").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // 이미지 타입 결정
                String contentType = "image/jpeg";
                if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("썸네일 조회 실패: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

