package com.mpole.hdt.digitaltwin.api.dto.menu;

import lombok.Builder;

@Builder
public record MenuCreateRequest(
        String menuName,
        String menuCode,
        Integer sortOrder,
        Long categoryId
) {
}
