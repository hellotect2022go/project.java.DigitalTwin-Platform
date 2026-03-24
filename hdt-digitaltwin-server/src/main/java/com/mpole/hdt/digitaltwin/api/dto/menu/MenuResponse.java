package com.mpole.hdt.digitaltwin.api.dto.menu;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private String menuCode;
    private Integer sortOrder;
}
