package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String loginId;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 50)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    
    @Column(nullable = false)
    private Boolean enabled;
    
    @Column(nullable = false)
    private Boolean accountNonLocked;
    
    @Column(nullable = false)
    private Integer failedLoginAttempts;
    
    @Column
    private LocalDateTime lastPasswordChangeDate;
    
    @Column
    private LocalDateTime lastLoginDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    /**
     * 비밀번호 변경 필요 여부 확인
     * @param passwordChangePeriodDays 비밀번호 변경 주기 (일)
     * @return 변경 필요 여부
     */
    public boolean isPasswordChangeRequired(int passwordChangePeriodDays) {
        if (lastPasswordChangeDate == null) {
            return true;
        }
        LocalDateTime expiryDate = lastPasswordChangeDate.plusDays(passwordChangePeriodDays);
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    /**
     * 로그인 실패 횟수 증가
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }
    
    /**
     * 로그인 성공 시 초기화
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lastLoginDate = LocalDateTime.now();
    }
    
    /**
     * 계정 잠금
     */
    public void lockAccount() {
        this.accountNonLocked = false;
    }
    
    /**
     * 비밀번호 변경 일자 업데이트
     */
    public void updatePasswordChangeDate() {
        this.lastPasswordChangeDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.lastPasswordChangeDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

