package com.mpole.hdt.digitaltwin.application.repository.menu;

import com.mpole.hdt.digitaltwin.application.repository.entity.DateEntity;
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
@Table(name="menu", indexes = {
        @Index(name = "idx_menu_parent_menu_id", columnList = "parent_menu_id"),
        @Index(name = "idx_menu_sort_order", columnList = "sort_order"),
        @Index(name = "idx_menu_active", columnList = "active")
})
public class Menu extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long menuId;

    @Comment("상위 메뉴 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_menu_id")
    private Menu parentMenu;

    @Comment("메뉴명")
    @Column(nullable = false)
    private String menuName;

    @Comment("메뉴 코드")
    @Column(nullable = false, unique = true)
    private String menuCode;

    @Comment("메뉴 URL")
    private String menuUrl;

    @Comment("아이콘 경로")
    private String iconPath;

    @Comment("정렬 순서")
    @Column(nullable = false)
    private Integer sortOrder;

    @Comment("메뉴 depth")
    @Column(nullable = false)
    private Integer depth;

    @Comment("활성화 여부")
    private Boolean active;

    @OneToMany(mappedBy = "menu")
    @Builder.Default
    private List<RoleMenu> roleMenus = new ArrayList<>();

}
