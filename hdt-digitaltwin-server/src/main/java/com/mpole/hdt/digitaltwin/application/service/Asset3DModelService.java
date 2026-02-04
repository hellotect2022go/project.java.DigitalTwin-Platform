package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.asset3d.Asset3DModelDTO;
import com.mpole.hdt.digitaltwin.api.dto.asset3d.Asset3DModelRequest;
import com.mpole.hdt.digitaltwin.application.repository.Asset3DModelRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.Asset3DModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Asset3DModelService {
    
    private final Asset3DModelRepository repository;
    
    @Value("${file.upload.path:uploads/3d-models}")
    private String uploadPath;
    
    @Value("${file.upload.thumbnail-path:uploads/thumbnails}")
    private String thumbnailPath;
    
    /**
     * 전체 조회
     */
    @Transactional(readOnly = true)
    public List<Asset3DModelDTO> getAllModels() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 활성화된 모델만 조회
     */
    @Transactional(readOnly = true)
    public List<Asset3DModelDTO> getEnabledModels() {
        return repository.findByEnabledTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ID로 조회
     */
    @Transactional(readOnly = true)
    public Asset3DModelDTO getModelById(Long id) {
        Asset3DModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + id));
        return toDto(model);
    }
    
    /**
     * 검색
     */
    @Transactional(readOnly = true)
    public List<Asset3DModelDTO> searchModels(String keyword) {
        return repository.searchByKeyword(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 확장자별 조회
     */
    @Transactional(readOnly = true)
    public List<Asset3DModelDTO> getModelsByExtension(String extension) {
        return repository.findByFileExtensionAndEnabledTrue(extension).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 파일 업로드 및 모델 생성
     */
    @Transactional
    public Asset3DModelDTO uploadModel(MultipartFile file, Asset3DModelRequest request) throws IOException {
        // 파일 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다");
        }
        
        // 모델명 중복 체크
        if (repository.existsByModelName(request.getModelName())) {
            throw new IllegalArgumentException("이미 존재하는 모델명입니다: " + request.getModelName());
        }
        
        // 파일 저장
        String savedFilePath = saveFile(file);
        String fileUrl = "/api/asset-3d-models/download/" + extractFileName(savedFilePath);
        
        // 엔티티 생성
        Asset3DModel model = Asset3DModel.builder()
                .modelName(request.getModelName())
                .filePath(savedFilePath)
                .fileExtension(getFileExtension(file.getOriginalFilename()))
                .fileSize(file.getSize())
                .fileUrl(fileUrl)
                .polygonCount(request.getPolygonCount())
                .defaultScale(request.getDefaultScale())
                .description(request.getDescription())
                .metadata(request.getMetadata())
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        
        model = repository.save(model);
        log.info("3D 모델 업로드 완료: {} ({})", model.getModelName(), model.getFilePath());
        
        return toDto(model);
    }
    
    /**
     * 썸네일 업로드
     */
    @Transactional
    public Asset3DModelDTO uploadThumbnail(Long id, MultipartFile thumbnail) throws IOException {
        Asset3DModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + id));
        
        if (thumbnail.isEmpty()) {
            throw new IllegalArgumentException("썸네일 파일이 비어있습니다");
        }
        
        // 썸네일 저장
        String savedThumbnailPath = saveThumbnail(thumbnail);
        String thumbnailUrl = "/api/asset-3d-models/thumbnail/" + extractFileName(savedThumbnailPath);
        
        model.setThumbnailUrl(thumbnailUrl);
        model = repository.save(model);
        
        log.info("썸네일 업로드 완료: {} ({})", model.getModelName(), savedThumbnailPath);
        
        return toDto(model);
    }
    
    /**
     * 모델 정보 수정 (파일 제외)
     */
    @Transactional
    public Asset3DModelDTO updateModel(Long id, Asset3DModelRequest request) {
        Asset3DModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + id));
        
        // 모델명 변경 시 중복 체크
        if (!model.getModelName().equals(request.getModelName())) {
            if (repository.existsByModelName(request.getModelName())) {
                throw new IllegalArgumentException("이미 존재하는 모델명입니다: " + request.getModelName());
            }
            model.setModelName(request.getModelName());
        }
        
        model.setPolygonCount(request.getPolygonCount());
        model.setDefaultScale(request.getDefaultScale());
        model.setDescription(request.getDescription());
        model.setMetadata(request.getMetadata());
        model.setEnabled(request.getEnabled());
        
        model = repository.save(model);
        log.info("3D 모델 정보 수정: {}", model.getModelName());
        
        return toDto(model);
    }
    
    /**
     * 모델 삭제
     */
    @Transactional
    public void deleteModel(Long id) {
        Asset3DModel model = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델을 찾을 수 없습니다: " + id));
        
        // 파일 삭제
        deleteFile(model.getFilePath());
        if (model.getThumbnailUrl() != null) {
            // 썸네일 URL에서 파일명 추출하여 삭제
            String thumbnailFileName = model.getThumbnailUrl().substring(model.getThumbnailUrl().lastIndexOf("/") + 1);
            deleteFile(Paths.get(thumbnailPath, thumbnailFileName).toString());
        }
        
        repository.delete(model);
        log.info("3D 모델 삭제: {} ({})", model.getModelName(), model.getFilePath());
    }
    
    /**
     * 파일 저장
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 고유 파일명 생성 (타임스탬프 + UUID + 원본 확장자)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = timestamp + "_" + uuid + extension;
        
        Path filePath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
    
    /**
     * 썸네일 저장
     */
    private String saveThumbnail(MultipartFile thumbnail) throws IOException {
        Path thumbnailDir = Paths.get(thumbnailPath);
        if (!Files.exists(thumbnailDir)) {
            Files.createDirectories(thumbnailDir);
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(thumbnail.getOriginalFilename());
        String newFilename = "thumb_" + timestamp + "_" + uuid + extension;
        
        Path filePath = thumbnailDir.resolve(newFilename);
        Files.copy(thumbnail.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
    
    /**
     * 파일 삭제
     */
    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("파일 삭제 완료: {}", filePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }
    
    /**
     * 파일 경로에서 파일명만 추출
     */
    private String extractFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }
    
    /**
     * 파일 크기 포맷팅
     */
    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * Entity -> DTO 변환
     */
    private Asset3DModelDTO toDto(Asset3DModel model) {
        return Asset3DModelDTO.builder()
                .id(model.getId())
                .modelName(model.getModelName())
                .filePath(model.getFilePath())
                .fileExtension(model.getFileExtension())
                .fileSize(model.getFileSize())
                .fileSizeFormatted(formatFileSize(model.getFileSize()))
                .fileUrl(model.getFileUrl())
                .thumbnailUrl(model.getThumbnailUrl())
                .polygonCount(model.getPolygonCount())
                .defaultScale(model.getDefaultScale())
                .description(model.getDescription())
                .metadata(model.getMetadata())
                .enabled(model.getEnabled())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}

