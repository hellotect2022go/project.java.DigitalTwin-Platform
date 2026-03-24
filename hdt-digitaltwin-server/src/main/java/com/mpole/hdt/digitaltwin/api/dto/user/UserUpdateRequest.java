package com.mpole.hdt.digitaltwin.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserUpdateRequest(
        @NotBlank String username,
        @NotBlank String email,
        @NotNull Boolean active,
        Boolean accountNonLocked,
        Integer failedLoginAttempts,
        String password
) {
    public UserUpdateRequest {
        accountNonLocked = accountNonLocked == null || accountNonLocked;
        failedLoginAttempts = (failedLoginAttempts == null) ? 0 : failedLoginAttempts;
    }
}
