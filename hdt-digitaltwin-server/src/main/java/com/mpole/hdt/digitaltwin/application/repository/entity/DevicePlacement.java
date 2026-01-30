package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * DevicePlacement (장비 Unity 3D 배치 정보)
 * Device와 1:1 관계로 Unity 내 위치, 회전, 스케일 정보 관리
 */
@Entity
@Table(name = "device_placements", indexes = {
        @Index(name = "idx_placements_device_id", columnList = "device_id"),
        @Index(name = "idx_placements_floor", columnList = "floor_level")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicePlacement extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Device와 1:1 관계
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    // ========================================
    // Unity 3D Position (위치)
    // ========================================

    @Column(name = "position_x", nullable = false)
    @Builder.Default
    private Float positionX = 0.0f;

    @Column(name = "position_y", nullable = false)
    @Builder.Default
    private Float positionY = 0.0f;

    @Column(name = "position_z", nullable = false)
    @Builder.Default
    private Float positionZ = 0.0f;

    // ========================================
    // Unity 3D Rotation (회전 - Euler Angles)
    // ========================================

    @Column(name = "rotation_x", nullable = false)
    @Builder.Default
    private Float rotationX = 0.0f;

    @Column(name = "rotation_y", nullable = false)
    @Builder.Default
    private Float rotationY = 0.0f;

    @Column(name = "rotation_z", nullable = false)
    @Builder.Default
    private Float rotationZ = 0.0f;

    // ========================================
    // Unity 3D Scale (크기)
    // ========================================

    @Column(name = "scale_x", nullable = false)
    @Builder.Default
    private Float scaleX = 1.0f;

    @Column(name = "scale_y", nullable = false)
    @Builder.Default
    private Float scaleY = 1.0f;

    @Column(name = "scale_z", nullable = false)
    @Builder.Default
    private Float scaleZ = 1.0f;

    // ========================================
    // Unity Scene 구조 정보
    // ========================================

    /**
     * Unity Scene 내 Floor/Level (예: "Floor_1F", "Floor_B1")
     */
    @Column(name = "floor_level", length = 50)
    private String floorLevel;

    /**
     * Unity Layer 이름
     */
    @Column(name = "layer_name", length = 50)
    private String layerName;

    /**
     * 부모 GameObject 경로 (예: "Building/Floor1/MachineRoom")
     */
    @Column(name = "parent_object", length = 200)
    private String parentObject;

    /**
     * Unity GameObject 이름
     */
    @Column(name = "game_object_name", length = 100)
    private String gameObjectName;

    // ========================================
    // 추가 메타데이터
    // ========================================

    /**
     * 커스텀 속성 (JSON)
     * 예: {"lighting": true, "collision": false, "interactive": true}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_attributes", columnDefinition = "TEXT")
    private Map<String, Object> customAttributes;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    /**
     * 생성자
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 수정자
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}

