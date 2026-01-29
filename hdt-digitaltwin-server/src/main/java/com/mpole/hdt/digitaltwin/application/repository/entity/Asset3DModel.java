package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Unity 3D 에셋 파일 정보 엔티티
 */
@Entity
@Table(name = "asset_3d_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset3DModel extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 모델명 (고유 식별자)
     */
    @Column(name = "model_name", nullable = false, unique = true, length = 200)
    private String modelName;

    /**
     * 파일 경로
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 파일 확장자 (예: .fbx, .obj, .prefab)
     */
    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    /**
     * 파일 크기 (bytes)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 파일 URL (다운로드/접근 URL)
     */
    @Column(name = "file_url", length = 500)
    private String fileUrl;

    /**
     * 썸네일 URL
     */
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /**
     * 폴리곤 수
     */
    @Column(name = "polygon_count")
    private Integer polygonCount;

    /**
     * 기본 스케일
     */
    @Column(name = "default_scale")
    private Float defaultScale;

    /**
     * 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 메타데이터 (JSON)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * 활성화 여부
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}

