package com.mpole.hdt.digitaltwin.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 활성 세션(기기) 조회 응답
 */
@Builder
public record ActiveSessionsResponse(
        int totalCount,
        List<SessionInfo> sessions
) {
    @Builder
    public record SessionInfo(
            String deviceId,
            String deviceName,
            String deviceType,
            String ipAddress,
            LocalDateTime lastUsedAt,
            LocalDateTime expiresAt,
            boolean current
    ) {}
}


