package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name="device_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceModel extends DateEntity{

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
    private Category category;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sys_id", nullable = false)
    private SystemType systemType;

    // ⭐ 3D 모델 참조
    //@OneToOne(fetch=FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name="model_id")
    private Asset3DModel asset3DModel;

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
