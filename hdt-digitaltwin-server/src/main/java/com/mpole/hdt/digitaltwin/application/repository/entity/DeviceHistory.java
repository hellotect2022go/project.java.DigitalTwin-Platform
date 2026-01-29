package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "device_history", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id,timestamp"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    // 상태 정보
    private String operationStatus;
    private Boolean isRunning;

    // 측정값 스냅샷
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> values;

    // 알람 발생 여부
    private Boolean hasAlarm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> alarms;

    // 추가 정보
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> metadata;
}
