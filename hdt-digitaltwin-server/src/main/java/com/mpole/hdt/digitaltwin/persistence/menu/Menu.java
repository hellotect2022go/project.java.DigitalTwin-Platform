package com.mpole.hdt.digitaltwin.persistence.menu;

import com.mpole.hdt.digitaltwin.api.dto.menu.MenuCreateRequest;
import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="tbl_menu", indexes = {
        @Index(name = "idx_menu_sort_order", columnList = "sort_order"),
        @Index(name = "idx_menu_active", columnList = "active")
})
public class Menu extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long menuId;

    @Comment("메뉴명")
    @Column(nullable = false)
    private String menuName;

    @Comment("메뉴 코드")
    @Column(nullable = false, unique = true)
    private String menuCode;

    @Comment("정렬 순서")
    @Column(nullable = false)
    private Integer sortOrder;

    @Comment("활성화 여부")
    private Boolean active;

    @Column(name = "category_id")
    private Long categoryId;

    @OneToMany(mappedBy = "menu")
    @Builder.Default
    private List<RoleMenu> roleMenus = new ArrayList<>();


    public static Menu create(MenuCreateRequest menuCreateRequest) {
        return Menu.builder()
                .menuName(menuCreateRequest.menuName())
                .menuCode(menuCreateRequest.menuCode())
                .sortOrder(menuCreateRequest.sortOrder())
                .categoryId(menuCreateRequest.categoryId())
                .active(true)
                .build();
    }

}
