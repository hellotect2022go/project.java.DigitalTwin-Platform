package com.mpole.hdt.digitaltwin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 활성 세션(기기) 조회 응답
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSessionsResponse {
    
    private int totalCount;
    private List<SessionInfo> sessions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionInfo {
        private String deviceId;
        private String deviceName;
        private String deviceType;
        private String ipAddress;
        private LocalDateTime lastUsedAt;
        private LocalDateTime expiresAt;
        private boolean current;  // 현재 세션 여부
    }
}


