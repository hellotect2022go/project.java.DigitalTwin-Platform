package com.mpole.hdt.digitaltwin.persistence.menu;

import com.mpole.hdt.digitaltwin.persistence.user.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="tbl_role_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "menu_id"})
})
public class RoleMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleMenuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="menu_id", nullable = false)
    private Menu menu;
}
