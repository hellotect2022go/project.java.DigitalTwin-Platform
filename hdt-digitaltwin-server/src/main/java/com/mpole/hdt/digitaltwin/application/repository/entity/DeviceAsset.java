package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name="device_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceAsset extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String assetCode;

    @Column(nullable = false, length = 200)
    private String assetNameKo;

    @Column(length = 200)
    private String assetNameEn;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id", nullable = false)
    private DeviceCategory category;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sys_id", nullable = false)
    private DeviceSystemType systemType;

    @Column(length=100)
    private String building;

    @Column(length = 50)
    private String floor;

    @Column(length = 100)
    private String zone;

    private Long object3dId;

    private Float unityPositionX;

    private Float unityPositionY;

    private Float unityPositionZ;

    private Float unityRotationX;

    private Float unityRotationY;

    private Float unityRotationZ;

    @Column(nullable = false)
    @Builder.Default
    private Float unityScale = 1.0f;

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 100)
    private String modelNumber;

    @Column(length = 100)
    private String serialNumber;

    private OffsetDateTime installationDate;

    // 확장 속성 (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_attributes", columnDefinition = "TEXT")
    private Map<String, Object> customAttributes;


    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;


    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;



}
