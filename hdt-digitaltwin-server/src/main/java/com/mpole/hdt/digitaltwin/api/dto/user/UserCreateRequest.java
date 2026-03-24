package com.mpole.hdt.digitaltwin.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        @NotBlank String loginId,
        @NotBlank String username,
        @NotBlank String email,
        @NotNull Boolean active,
        @NotBlank String password
) {

}
