package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.*;
import com.mpole.hdt.digitaltwin.api.dto.auth.ActiveSessionsResponse;
import com.mpole.hdt.digitaltwin.api.dto.auth.ChangePasswordRequest;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginRequest;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginResponse;
import com.mpole.hdt.digitaltwin.application.service.AuthService;
import com.mpole.hdt.digitaltwin.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 (다중 기기 지원)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request, httpRequest);
        
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    /**
     * 토큰 갱신 (Refresh Token) - IP 검증 포함
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @AuthenticationPrincipal JwtAuthenticationFilter.UserPrincipal userPrincipal,
            HttpServletRequest httpRequest) {
        log.debug("토큰 갱신 요청");

        LoginResponse response = authService.refreshToken(userPrincipal.loginId(), userPrincipal.deviceId(), httpRequest);
        
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }

    /**
     * 로그아웃 (현재 기기)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal JwtAuthenticationFilter.UserPrincipal userPrincipal) {

        log.info("로그아웃 요청: {} (기기: {})", userPrincipal.loginId(), userPrincipal.deviceId());
        authService.logout(userPrincipal.loginId(), userPrincipal.deviceId());
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다"));
    }

    /**
     * 모든 기기에서 로그아웃
     */
    @PostMapping("/logout/all")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices(@AuthenticationPrincipal(expression = "loginId") String loginId ) {
        log.info("모든 기기에서 로그아웃 요청: {}", loginId);
        
        authService.logoutAllDevices(loginId);
        
        return ResponseEntity.ok(ApiResponse.success("모든 기기에서 로그아웃되었습니다"));
    }

    /**
     * 활성 세션(기기) 목록 조회
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<ActiveSessionsResponse>> getActiveSessions(
            @AuthenticationPrincipal(expression = "loginId") String loginId,
            @RequestParam(required = false) String deviceId) {
        log.info("활성 세션 조회: {}", loginId);
        
        ActiveSessionsResponse response = authService.getActiveSessions(loginId);
        
        return ResponseEntity.ok(ApiResponse.success("활성 세션 목록", response));
    }

    /**
     * 특정 기기 세션 강제 종료
     */
    @DeleteMapping("/sessions/{deviceId}")
    public ResponseEntity<ApiResponse<Void>> revokeDevice(
            @AuthenticationPrincipal(expression = "loginId") String loginId,
            @PathVariable String deviceId) {
        log.info("기기 세션 강제 종료 요청: {} (기기: {})", loginId, deviceId);
        
        authService.revokeDevice(loginId, deviceId);
        
        return ResponseEntity.ok(ApiResponse.success("기기 세션이 종료되었습니다"));
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal String loginId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("비밀번호 변경 요청: {}", loginId);
        
        authService.changePassword(loginId, request);
        
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다"));
    }

    /**
     * 계정 잠금 해제 (관리자 전용)
     */
    @PostMapping("/unlock/{loginId}")
    public ResponseEntity<ApiResponse<Void>> unlockAccount(@PathVariable String loginId) {
        log.info("계정 잠금 해제 요청: {}", loginId);
        
        authService.unlockAccount(loginId);
        
        return ResponseEntity.ok(ApiResponse.success("계정 잠금이 해제되었습니다"));
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@AuthenticationPrincipal JwtAuthenticationFilter.UserPrincipal userPrincipal) {
        return ResponseEntity.ok(ApiResponse.success("현재 사용자", userPrincipal));
    }

    /**
     * 헬스 체크 (인증 불필요)
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running!");
    }
}

