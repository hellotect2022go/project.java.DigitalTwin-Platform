package com.mpole.hdt.digitaltwin.persistence.menu;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private Long menuId;
    private Long parentMenuId;
    private String menuName;
    private String menuCode;
    private String menuUrl;
    private String iconPath;
    private Integer sortOrder;
    private Integer depth;
}
