package com.mpole.hdt.digitaltwin.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;  // Unity에서 로컬 저장 (암호화 권장)
    private Long expiresIn;  // 밀리초
    private UserInfo userInfo;
    private boolean passwordChangeRequired;
    private Integer daysUntilPasswordExpiry;  // 비밀번호 만료까지 남은 일수
    
    // 다중 기기 정보
    private String currentDeviceId;  // 현재 로그인한 기기 ID
    private List<ActiveDevice> activeDevices;  // 활성 기기 목록
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String loginId;
        private String email;
        private String name;
        private String role;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActiveDevice {
        private String deviceId;
        private String deviceName;
        private String deviceType;
        private String ipAddress;
        private String lastUsedAt;
        private boolean current;  // 현재 로그인한 기기 여부
    }
}
