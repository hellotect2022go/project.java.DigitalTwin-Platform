package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "devices", indexes = {
        @Index(name = "idx_model_id", columnList = "device_model_id"),
        @Index(name = "idx_device_id", columnList = "device_id")
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

    // ⭐ DeviceAsset과 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id", nullable = false, unique = true)
    private DeviceModel deviceModel;

    // ==========================================
    // 실제 장비 식별 정보
    // ==========================================

    @Column(nullable = false, unique = true, length = 200)
    private String deviceId; // 실제 장비의 고유 ID (MAC, GUID, BACnet ID 등)

    @Column(length = 100)
    private String ipAddress; // IP 주소

    @Column(length = 50)
    private String port; // 포트 번호

    @Column(length = 50)
    private String protocol; // BACnet, Modbus, MQTT, HTTP 등

    @Column(length = 200)
    private String connectionString; // 연결 문자열

    // ==========================================
    // 실시간 상태 정보 (Dynamic Data)
    // ==========================================

    @Column(nullable = false)
    @Builder.Default
    private String operationStatus = "OFFLINE";
    // ONLINE, OFFLINE, ERROR, MAINTENANCE, STANDBY

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRunning = false; // 가동 중 여부

    private OffsetDateTime lastCommunicationTime; // 마지막 통신 시간

    private OffsetDateTime lastStatusChangeTime; // 마지막 상태 변경 시간

    // ==========================================
    // 실시간 측정값 (센서 데이터)
    // ==========================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> currentValues;
    // 예: {
    //   "temperature": 22.5,
    //   "humidity": 45.0,
    //   "fan_speed": 1200,
    //   "power_consumption": 3.2,
    //   "flow_rate": 800
    // }

    // ==========================================
    // 제어 설정값
    // ==========================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> setPoints;
    // 예: {
    //   "target_temperature": 24.0,
    //   "target_humidity": 50.0,
    //   "mode": "AUTO",
    //   "schedule": "OFFICE_HOURS"
    // }

    // ==========================================
    // 알람 및 경고
    // ==========================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasAlarm = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasWarning = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> alarms;
    // 예: [
    //   {"code": "HIGH_TEMP", "message": "온도 과상승", "severity": "ERROR"},
    //   {"code": "FILTER_REPLACE", "message": "필터 교체 필요", "severity": "WARNING"}
    // ]

    // ==========================================
    // 통계 정보
    // ==========================================

    private Double todayPowerConsumption; // 금일 전력 소비량 (kWh)

    private Long todayRunningTime; // 금일 가동 시간 (초)

    private Long totalRunningTime; // 총 가동 시간 (초)

    private Integer restartCount; // 재시작 횟수

    // ==========================================
    // 유지보수 정보
    // ==========================================

    private OffsetDateTime lastMaintenanceDate; // 마지막 유지보수 일자

    private OffsetDateTime nextMaintenanceDate; // 다음 유지보수 예정일

    @Column(length = 500)
    private String maintenanceNotes; // 유지보수 노트

    // ==========================================
    // 확장 속성
    // ==========================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> deviceMetadata;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(length = 50)
    private String createdBy;

    @Column(length = 50)
    private String updatedBy;
}
