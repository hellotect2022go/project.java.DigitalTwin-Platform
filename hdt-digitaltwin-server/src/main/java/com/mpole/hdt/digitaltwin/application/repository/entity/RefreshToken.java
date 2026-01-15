package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Refresh Token 엔티티 (관제 시스템 최적화)
 * - 장기 세션 지원 (90일)
 * - IP 화이트리스트
 * - 다중 모니터/PC 동시 접속
 */
@Entity
@Table(name = "refresh_tokens", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"login_id", "device_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String deviceId;  // 기기 식별자

    @Column(length = 100)
    private String deviceName;  // 기기 이름

    @Column(length = 50)
    private String deviceType;  // PC, MONITOR_1, MONITOR_2 등

    @Column(length = 100)
    private String ipAddress;  // 현재 IP

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUsedAt;  // 마지막 사용 시간 (활성도 추적)

    @Column
    private LocalDateTime lastActivityAt;  // 마지막 활동 시간 (API 호출, WebSocket 통신 등)

    /*
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * 마지막 사용 시간 업데이트
     */
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 활동 시간 업데이트 (API 호출, WebSocket 메시지 등)
     */
    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }
}
