package com.mpole.hdt.digitaltwin.persistence.user;

import com.mpole.hdt.digitaltwin.api.dto.user.UserCreateRequest;
import com.mpole.hdt.digitaltwin.api.dto.user.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_user", indexes = {
        @Index(name = "idx_user_active",columnList = "active"),
        @Index(name = "idx_user_created_at",columnList = "created_at")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)
    @Comment("사용자 로그인 계정")
    private String loginId;
    
    @Comment("사용자 이름")
    private String username;

    @Comment("사죵자 비밀번호 (단방향)")
    private String passwordHash;

    @Comment("사용자 메일 계정")
    private String email;

    @Column(name="active")
    @Comment("활성 사용자 여부")
    private Boolean active;

    @Column(nullable = false)
    @Comment("사용자 계정 잠금 여부")
    private Boolean accountNonLocked;

    @Column(nullable = false)
    @Comment("로그인 실패 횟수")
    private Integer failedLoginAttempts;

    @Comment("비밀번호 변경일")
    private OffsetDateTime lastPasswordChangeDate;

    @Column
    private OffsetDateTime lastLoginDate;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column
    private OffsetDateTime updatedAt;

    // CascadeType.All (영속성 전이 부모 -> 자식)
    // orpahnRemoval (부모 삭제시 자식도 삭제)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default  // 빌더 작성시 리스트 초기화 보장
    private List<UserRole> userRoles = new ArrayList<>();


    /**
     * 비밀번호 변경 필요 여부 확인
     * @param passwordExpiryDays 비밀번호 변경 주기 (일)
     * @return 변경 필요 여부
     */
    public boolean isPasswordChangeRequired(int passwordExpiryDays) {
        if (lastPasswordChangeDate == null) {
            return true;
        }
        OffsetDateTime expiryDate = lastPasswordChangeDate.plusDays(passwordExpiryDays);
        return OffsetDateTime.now().isAfter(expiryDate);
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
        this.lastLoginDate = OffsetDateTime.now();
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
        this.lastPasswordChangeDate = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.active = true;
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.lastPasswordChangeDate = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateInfo(UserUpdateRequest req, String encodedPassword) {
        // 유효성 검사 로직을 여기에 넣을 수도 있습니다.
        this.username = req.username();
        this.email = req.email();
        this.active = req.active();
        this.accountNonLocked = req.accountNonLocked();
        this.failedLoginAttempts = req.failedLoginAttempts();
        this.updatedAt = OffsetDateTime.now();

        if (StringUtils.hasText(encodedPassword)) {
            this.passwordHash = encodedPassword;
            this.lastPasswordChangeDate = OffsetDateTime.now();
        }
    }

    public static User create(UserCreateRequest req, String encodedPassword) {
        return User.builder()
                .loginId(req.loginId())
                .username(req.username())
                .email(req.email())
                .passwordHash(encodedPassword)
                // 기본값 설정 (PrePersist가 있지만 명시적으로 넣는게 안전함)
                .active(req.active())
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .lastPasswordChangeDate(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .build();
    }

}
