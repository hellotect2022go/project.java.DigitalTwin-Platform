package com.mpole.hdt.digitaltwin.application.repository.device;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * Device (실제 장비 인스턴스)
 * DeviceModel을 기반으로 생성된 실제 설치된 장비
 */
@Entity
@Table(name = "device_transform", indexes = {
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceTransform extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("공간 배치 고유 ID")
    private Long transformId;

    //@OneToOne(fetch = FetchType.LAZY, mappedBy = "deviceTransform")
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "device_id", nullable = false, unique = true)
//    @Comment("참조 장비 ID")
//    private Device device;


    // ========================================
    // Unity 3D Position (위치)
    // ========================================

    @Column(name = "pos_x", nullable = false)
    @Builder.Default
    private Float posX = 0.0f;

    @Column(name = "pos_y", nullable = false)
    @Builder.Default
    private Float posY = 0.0f;

    @Column(name = "pos_z", nullable = false)
    @Builder.Default
    private Float posZ = 0.0f;

    // ========================================
    // Unity 3D Rotation (회전 - Euler Angles)
    // ========================================

    @Column(name = "rot_x", nullable = false)
    @Builder.Default
    private Float rotX = 0.0f;

    @Column(name = "rot_y", nullable = false)
    @Builder.Default
    private Float rotY = 0.0f;

    @Column(name = "rot_z", nullable = false)
    @Builder.Default
    private Float rotZ = 0.0f;

    @Column(name = "scale_x", nullable = false)
    @Builder.Default
    private Float scaleX = 1.0f;

    @Column(name = "scale_y", nullable = false)
    @Builder.Default
    private Float scaleY = 1.0f;

    @Column(name = "scale_z", nullable = false)
    @Builder.Default
    private Float scaleZ = 1.0f;

//    /**
//     * Unity Scene 내 Floor/Level (예: "Floor_1F", "Floor_B1")
//     */
//    @Column(name = "floor_level", length = 50)
//    private String floorLevel;
//
//    @Column(name = "zone", length = 50)
//    private String zone;
    
}
