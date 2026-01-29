package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "device_commands", indexes = {
        @Index(name = "idx_commands_device_id", columnList = "device_id"),
        @Index(name = "idx_commands_issued_at", columnList = "issued_at"),
        @Index(name = "idx_commands_status", columnList = "status"),
        @Index(name = "idx_commands_device_status", columnList = "device_id,status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCommand extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false, length = 100)
    private String commandType; // SET_TEMPERATURE, START, STOP, RESET 등

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> parameters;
    // 예: {"temperature": 24.0, "mode": "COOL"}

    @Column(nullable = false)
    private OffsetDateTime issuedAt; // 명령 발행 시간

    private OffsetDateTime executedAt; // 실행 시간

    private OffsetDateTime completedAt; // 완료 시간

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";
    // PENDING, EXECUTING, COMPLETED, FAILED, TIMEOUT

    @Column(length = 500)
    private String result; // 실행 결과

    @Column(length = 500)
    private String errorMessage; // 에러 메시지

    @Column(length = 50)
    private String issuedBy; // 명령 발행자

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> metadata;
}
