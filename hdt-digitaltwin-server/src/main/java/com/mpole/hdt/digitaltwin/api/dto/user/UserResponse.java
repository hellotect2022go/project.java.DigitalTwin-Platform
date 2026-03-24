package com.mpole.hdt.digitaltwin.api.dto.user;

import com.mpole.hdt.digitaltwin.persistence.user.User;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record UserResponse(
        Long userId,
        String loginId,
        String username,
        String email,
        Boolean active,
        Boolean accountNonLocked,
        Integer failedLoginAttempts,
        OffsetDateTime lastPasswordChangeDate,
        OffsetDateTime lastLoginDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.getActive())
                .accountNonLocked(user.getAccountNonLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lastPasswordChangeDate(user.getLastPasswordChangeDate())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
