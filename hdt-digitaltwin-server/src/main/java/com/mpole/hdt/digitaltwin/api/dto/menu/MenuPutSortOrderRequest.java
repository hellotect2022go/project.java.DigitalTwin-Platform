package com.mpole.hdt.digitaltwin.api.dto.menu;


public record MenuPutSortOrderRequest(
        Long menuId,
        Integer sortOrder
) {
}
