package com.mpole.hdt.digitaltwin.application.repository.menu;

import com.mpole.hdt.digitaltwin.application.repository.user.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="role_menu", uniqueConstraints = {
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
