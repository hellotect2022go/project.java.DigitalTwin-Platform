package com.mpole.hdt.digitaltwin.service;

import com.mpole.hdt.digitaltwin.api.dto.auth.ActiveSessionsResponse;
import com.mpole.hdt.digitaltwin.api.dto.auth.ChangePasswordRequest;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginRequest;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginResponse;
import com.mpole.hdt.digitaltwin.persistence.user.RefreshToken;
import com.mpole.hdt.digitaltwin.persistence.user.User;
import com.mpole.hdt.digitaltwin.persistence.user.UserRepo;
import com.mpole.hdt.digitaltwin.config.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserLockService userLockService;
    private final RefreshTokenService refreshTokenService;  // Refresh Token 서비스 추가

    @Value("${password.change-period-days}")
    private int passwordChangePeriodDays;

    /**
     * 로그인 처리 (관제 시스템용 - 장기 세션)
     */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("로그인 시도: {} (기기: {})", request.loginId(), getClientIP(httpRequest));

        // 1. 사용자 체크
        User user = userRepo.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다"));

        // 2. 계정 잠금 확인
        if (!user.getAccountNonLocked()) {
            throw new IllegalStateException("계정이 잠겨있습니다. 관리자에게 문의하세요.");
        }

        // 3. 계정 활성화 확인
        if (!user.getActive()) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        // 4. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            userLockService.handleLoginFailure(user);
            throw new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다");
        }

        // 로그인 성공 처리
        user.resetFailedAttempts();
        userRepo.save(user);

        List<String> userRoles = user.getUserRoles().stream().map(userRole -> userRole.getRole().getRoleName()).toList();

        // 기기 정보 처리
        // deviceId가 없으면 자동 생성
        String deviceId = UUID.randomUUID().toString();
        // deviceName이 없으면 자동 설정
        String deviceName = generateDeviceName(httpRequest);
        // deviceType이 없으면 User-Agent로 추론
        String deviceType = detectDeviceType(httpRequest);

        // 토큰 생성 (관제 시스템용 - 장기 세션)
        String accessToken = jwtTokenProvider.generateAccessToken(user.getLoginId(), userRoles);

        String ipAddress = getClientIP(httpRequest);
        String refreshToken = refreshTokenService.createRefreshToken(user.getLoginId(), deviceId, deviceName, deviceType, ipAddress);

        // 비밀번호 변경 필요 여부 확인
        boolean passwordChangeRequired = user.isPasswordChangeRequired(passwordChangePeriodDays);
        Integer daysUntilExpiry = calculateDaysUntilPasswordExpiry(user);

        // 활성 기기 목록
        ActiveSessionsResponse activeSessions = refreshTokenService.getActiveSessions(user.getLoginId());


        log.info("✅ 로그인 성공: {}, 기기: {} ({}), 비밀번호 변경 필요: {}", user.getLoginId(), deviceName, deviceType, passwordChangeRequired);


        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .loginId(user.getLoginId())
                        .email(user.getEmail())
                        .name(user.getUsername())
                        .role(userRoles)
                        .build())
                .passwordChangeRequired(passwordChangeRequired)
                .daysUntilPasswordExpiry(daysUntilExpiry)
                .currentDeviceId(deviceId)
                .activeDevices(activeSessions.sessions().stream()
                        .map(session -> LoginResponse.ActiveDevice.builder()
                                .deviceId(session.deviceId())
                                .deviceName(session.deviceName())
                                .deviceType(session.deviceType())
                                .ipAddress(session.ipAddress())
                                .lastUsedAt(session.lastUsedAt().toString())
                                .current(session.current())
                                .build())
                        .collect(java.util.stream.Collectors.toList()))
                .build();
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급 (IP 검증 포함)
     */
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(String loginId, String deviceId, HttpServletRequest httpRequest) {

        String ipAddress = getClientIP(httpRequest);
        // Refresh Token 검증 (IP 화이트리스트 포함)
        RefreshToken validatedToken = refreshTokenService.validateRefreshToken(loginId, deviceId, ipAddress);

        // 사용자 조회
        User user = userRepo.findByLoginId(validatedToken.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 계정 상태 확인
        if (!user.getAccountNonLocked()) {
            throw new IllegalStateException("계정이 잠겨있습니다. 관리자에게 문의하세요.");
        }

        if (!user.getActive()) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }


        List<String> userRoles = user.getUserRoles().stream().map(userRole1 -> userRole1.getRole().getRoleName()).toList();

        // 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getLoginId(), userRoles);
        log.debug("🔄 토큰 갱신 성공: {} (기기: {})", user.getLoginId(), validatedToken.getDeviceName());



        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(validatedToken.getToken())  // 기존 Refresh Token 그대로 사용
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .userInfo(LoginResponse.UserInfo.builder()
                        .loginId(user.getLoginId())
                        .email(user.getEmail())
                        .name(user.getUsername())
                        .role(userRoles)
                        .build())
                .passwordChangeRequired(user.isPasswordChangeRequired(passwordChangePeriodDays))
                .daysUntilPasswordExpiry(calculateDaysUntilPasswordExpiry(user))
                .build();
    }

    /**
     * 로그아웃 (특정 기기)
     */
    @Transactional
    public void logout(String loginId, String deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }
        refreshTokenService.deleteRefreshToken(loginId, deviceId);
    }

    /**
     * 모든 기기에서 로그아웃
     */
    @Transactional
    public void logoutAllDevices(String loginId) {
        refreshTokenService.deleteAllRefreshTokens(loginId);
        log.info("🔓 모든 기기에서 로그아웃 완료: {}", loginId);
    }

    /**
     * 활성 세션(기기) 목록 조회
     */
    @Transactional(readOnly = true)
    public ActiveSessionsResponse getActiveSessions(String loginId) {
        return refreshTokenService.getActiveSessions(loginId);
    }

    /**
     * 특정 기기 세션 강제 종료
     */
    @Transactional
    public void revokeDevice(String loginId, String deviceId) {
        refreshTokenService.revokeDevice(loginId, deviceId);
        log.info("❌ 기기 세션 강제 종료: {} (기기: {})", loginId, deviceId);
    }

    /**
     * 기기 ID 생성 (클라이언트가 제공하지 않은 경우)
     */
    private String generateDeviceId(String ipAddress) {
        return "device_" + ipAddress + "_" + System.currentTimeMillis();
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String loginId, ChangePasswordRequest request) {
        User user = userRepo.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다");
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다");
        }

        // 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (request.currentPassword().equals(request.newPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다");
        }

        // 비밀번호 암호화 및 저장
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.updatePasswordChangeDate();
        userRepo.save(user);

        log.info("비밀번호 변경 완료: {}", loginId);
    }

    /**
     * 비밀번호 만료까지 남은 일수 계산
     */
    private Integer calculateDaysUntilPasswordExpiry(User user) {
        if (user.getLastPasswordChangeDate() == null) {
            return 0;
        }

        OffsetDateTime expiryDate = user.getLastPasswordChangeDate().plusDays(passwordChangePeriodDays);
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDateTime.now(), expiryDate);

        return (int) Math.max(0, daysUntilExpiry);
    }

    /**
     * 계정 잠금 해제 (UserLockService로 위임)
     */
    @Transactional
    public void unlockAccount(String loginId) {
        userLockService.unlockAccount(loginId);
    }

    private String generateDeviceName(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent.contains("Chrome")) return "Chrome Browser";
        if (userAgent.contains("Firefox")) return "Firefox Browser";
        if (userAgent.contains("Safari")) return "Safari Browser";
        if (userAgent.contains("Mobile")) return "Mobile Browser";

        return "Unknown Browser";
    }

    private String detectDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();

        if (userAgent.contains("mobile") || userAgent.contains("android")) {
            return "MOBILE";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "TABLET";
        } else {
            return "PC";
        }
    }

    /**
     * 클라이언트 IP 추출
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

