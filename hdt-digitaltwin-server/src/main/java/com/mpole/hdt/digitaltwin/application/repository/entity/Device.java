package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Device (실제 장비 인스턴스)
 * DeviceModel을 기반으로 생성된 실제 설치된 장비
 */
@Entity
@Table(name = "devices", indexes = {
        @Index(name = "idx_devices_device_id", columnList = "device_id"),
        @Index(name = "idx_devices_model_id", columnList = "device_model_id"),
        @Index(name = "idx_devices_status", columnList = "status"),
        @Index(name = "idx_devices_floor", columnList = "floor")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 장비 고유 식별자 (예: "CH-01-001", "CH-01-002")
     */
    @Column(name = "device_id", nullable = false, unique = true, length = 100)
    private String deviceId;

    /**
     * 장비명 (예: "흡수식 냉동기 1호기")
     */
    @Column(name = "device_name", nullable = false, length = 200)
    private String deviceName;

    /**
     * DeviceModel 참조 (N:1)
     * 하나의 모델로 여러 실제 장비 생성 가능
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id", nullable = false)
    private DeviceModel deviceModel;

    /**
     * 장비 상태
     * RUNNING: 가동중
     * STOPPED: 정지
     * ERROR: 오류
     * MAINTENANCE: 점검중
     * STANDBY: 대기
     */
    @Column(length = 50)
    @Builder.Default
    private String status = "STANDBY";

    /**
     * 온라인 여부 (통신 상태)
     */
    @Column(name = "online")
    @Builder.Default
    private Boolean online = false;

    /**
     * 설치 위치 (텍스트)
     */
    @Column(length = 200)
    private String location;

    /**
     * 층 정보 (예: "B1F", "1F", "2F")
     */
    @Column(length = 20)
    private String floor;

    /**
     * 구역 정보 (예: "ZONE-A", "ZONE-B")
     */
    @Column(length = 50)
    private String zone;

    /**
     * 현재 측정값
     */
    @Column(name = "current_value")
    private Double currentValue;

    /**
     * 단위 (예: "℃", "kW", "㎥/h")
     */
    @Column(length = 20)
    private String unit;

    /**
     * 마지막 통신 시간
     */
    @Column(name = "last_communication")
    private OffsetDateTime lastCommunication;

    /**
     * 설치 일자
     */
    @Column(name = "installation_date")
    private OffsetDateTime installationDate;

    /**
     * 제조 일자
     */
    @Column(name = "manufacture_date")
    private OffsetDateTime manufactureDate;

    /**
     * 시리얼 번호
     */
    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    /**
     * 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

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
