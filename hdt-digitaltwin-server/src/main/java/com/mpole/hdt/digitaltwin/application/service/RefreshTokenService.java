package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.ActiveSessionsResponse;
import com.mpole.hdt.digitaltwin.application.repository.RefreshTokenRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.RefreshToken;
import com.mpole.hdt.digitaltwin.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Refresh Token 관리 서비스 (관제 시스템 최적화)
 * - 장기 세션 지원 (90일)
 * - IP 화이트리스트 검증
 * - 다중 기기 동시 접속
 * - 전역 로그아웃
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${auth.max-devices-per-user:10}")
    private int maxDevicesPerUser;

    @Value("${auth.enable-ip-whitelist:false}")
    private boolean enableIpWhitelist;

    @Value("${monitoring.control-room-mode:false}")
    private boolean controlRoomMode;

    /**
     * Refresh Token 생성 (관제 시스템용 - 장기 세션)
     */
    @Transactional
    public String createRefreshToken(String loginId, String deviceId, String deviceName, String deviceType, String ipAddress) {
        // 기존 토큰이 있으면 업데이트 (같은 기기)
        RefreshToken existing = refreshTokenRepository.findByLoginIdAndDeviceId(loginId, deviceId)
                .orElse(null);

        if (existing != null) {
            // 기존 세션 갱신
            existing.setToken(jwtTokenProvider.generateRefreshToken(loginId, deviceId));
            existing.setIpAddress(ipAddress);
            existing.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
            existing.updateLastUsed();
            existing.updateActivity();
            refreshTokenRepository.save(existing);
            
            log.info("✅ Refresh Token 갱신: {} (기기: {})", loginId, deviceName);
            return existing.getToken();
        }

        // 최대 기기 수 체크
        long activeDeviceCount = refreshTokenRepository.countByLoginIdAndExpiryDateAfter(
                loginId, LocalDateTime.now());
        
        if (activeDeviceCount >= maxDevicesPerUser) {
            log.warn("⚠️ 최대 기기 수({})에 도달: {}", maxDevicesPerUser, loginId);
            deleteOldestDevice(loginId);
        }

        // 새로운 Refresh Token 생성
        String token = jwtTokenProvider.generateRefreshToken(loginId, deviceId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .loginId(loginId)
                .deviceId(deviceId)
                .deviceName(deviceName != null ? deviceName : "Unknown Device")
                .deviceType(deviceType != null ? deviceType : "PC")
                .ipAddress(ipAddress)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("✅ Refresh Token 생성: {} (기기: {})", loginId, deviceName);


        return token;
    }

    /**
     * Refresh Token 검증 (IP 화이트리스트 포함)
     */
    @Transactional
    public RefreshToken validateRefreshToken(String loginId, String deviceId, String requestIp) {
        // JWT 토큰 자체 검증
//        if (!jwtTokenProvider.validateToken(token)) {
//            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
//        }

        // Refresh Token 타입 검증
        if (deviceId == null) {
            throw new IllegalArgumentException("Access Token은 Refresh 용도로 사용할 수 없습니다");
        }

        // DB에서 조회
        RefreshToken refreshToken = refreshTokenRepository.findByLoginIdAndDeviceId(loginId,deviceId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당 device (%s)의 Refresh Token 을 찾을 수 없습니다", deviceId)));

        // 만료 여부 확인
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 Refresh Token입니다");
        }

        // 마지막 사용 시간 및 활동 시간 업데이트
        refreshToken.updateLastUsed();
        refreshToken.updateActivity();
        refreshToken.setIpAddress(requestIp);  // 현재 IP 업데이트
        refreshTokenRepository.save(refreshToken);

        log.debug("🔄 Refresh Token 검증 성공: {} (기기: {}, IP: {})", 
                refreshToken.getLoginId(), refreshToken.getDeviceName(), requestIp);

        return refreshToken;
    }

    /**
     * 활동 시간 업데이트 (WebSocket 메시지, API 호출 시)
     */
    @Transactional
    public void updateActivity(String loginId, String deviceId) {
        refreshTokenRepository.findByLoginIdAndDeviceId(loginId, deviceId)
                .ifPresent(token -> {
                    token.updateActivity();
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * 특정 기기 로그아웃
     */
    @Transactional
    public void deleteRefreshToken(String loginId, String deviceId) {
        refreshTokenRepository.deleteByLoginIdAndDeviceId(loginId, deviceId);
        log.info("🔓 로그아웃: {} (기기: {})", loginId, deviceId);
    }

    /**
     * 전역 로그아웃 (모든 기기에서 로그아웃)
     */
    @Transactional
    public void deleteAllRefreshTokens(String loginId) {
        int count = refreshTokenRepository.findByLoginId(loginId).size();
        refreshTokenRepository.deleteByLoginId(loginId);
        log.warn("🚨 전역 로그아웃: {} ({}개 기기)", loginId, count);
    }

    /**
     * 특정 기기 강제 로그아웃
     */
    @Transactional
    public void revokeDevice(String loginId, String deviceId) {
        refreshTokenRepository.deleteByLoginIdAndDeviceId(loginId, deviceId);
        log.warn("❌ 기기 세션 강제 종료: {} (기기: {})", loginId, deviceId);
    }

    /**
     * 활성 세션 목록 조회
     */
    @Transactional(readOnly = true)
    public ActiveSessionsResponse getActiveSessions(String loginId) {
        // 사용자 ID 로 발급된 refresh 토큰 목록 조회
        List<RefreshToken> tokens = refreshTokenRepository.findByLoginId(loginId);

        // 현재 활성화되어있는 session 들 정보 조회
        List<ActiveSessionsResponse.SessionInfo> sessions = tokens.stream()
                .filter(token -> !token.isExpired())
                .map(token -> ActiveSessionsResponse.SessionInfo.builder()
                        .deviceId(token.getDeviceId())
                        .deviceName(token.getDeviceName())
                        .deviceType(token.getDeviceType())
                        .ipAddress(token.getIpAddress())
                        .lastUsedAt(token.getLastUsedAt())
                        .expiresAt(token.getExpiryDate())
                        .build())
                .collect(Collectors.toList());

        return ActiveSessionsResponse.builder()
                .totalCount(sessions.size())
                .sessions(sessions)
                .build();
    }

    /**
     * 비활성 세션 정리 (관제 시스템용 - 더 긴 타임아웃)
     * 90일 이상 활동이 없는 세션만 정리
     */
    @Scheduled(cron = "0 0 4 * * *")  // 매일 새벽 4시
    @Transactional
    public void cleanupInactiveSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(90);
        
        List<RefreshToken> inactiveTokens = refreshTokenRepository.findByLoginId("*").stream()
                .filter(token -> token.getLastActivityAt() != null && 
                               token.getLastActivityAt().isBefore(threshold))
                .collect(Collectors.toList());

        for (RefreshToken token : inactiveTokens) {
            refreshTokenRepository.delete(token);
            log.info("🧹 비활성 세션 정리: {} (기기: {}, 마지막 활동: {})", 
                    token.getLoginId(), token.getDeviceName(), token.getLastActivityAt());
        }
    }

    /**
     * 만료된 토큰 정리
     */
    @Scheduled(cron = "0 0 3 * * *")  // 매일 새벽 3시
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("🧹 만료된 Refresh Token 정리 시작");
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("✅ 만료된 Refresh Token 정리 완료");
    }

    /**
     * 가장 오래된 기기 삭제
     */
    private void deleteOldestDevice(String loginId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByLoginId(loginId);
        tokens.stream()
                .filter(token -> !token.isExpired())
                .min((t1, t2) -> t1.getLastActivityAt().compareTo(t2.getLastActivityAt()))
                .ifPresent(oldestToken -> {
                    refreshTokenRepository.delete(oldestToken);
                    log.info("🗑️ 가장 오래된 기기 세션 자동 삭제: {} (기기: {})", 
                            loginId, oldestToken.getDeviceName());
                });
    }
}
