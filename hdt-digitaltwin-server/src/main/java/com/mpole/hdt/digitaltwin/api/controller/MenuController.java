package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.persistence.menu.Menu;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuRepo;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuResponse;
import com.mpole.hdt.digitaltwin.persistence.menu.RoleMenuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuRepo menuRepo;
    private final RoleMenuRepo roleMenuRepo;

    @GetMapping("")
    public ResponseEntity fetchMenuByRole(@AuthenticationPrincipal(expression = "authorities") Collection<? extends GrantedAuthority> authorities) {

        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        List<MenuResponse> list = menuRepo.findMenusByRole(roles).stream().map(menu->{
            return MenuResponse.builder()
                    .menuId(menu.getMenuId())
                    .parentMenuId(menu.getParentMenu() != null ? menu.getParentMenu().getMenuId() : null)
                    .menuName(menu.getMenuName())
                    .menuCode(menu.getMenuCode())
                    .menuUrl(menu.getMenuUrl())
                    .iconPath(menu.getIconPath())
                    .sortOrder(menu.getSortOrder())
                    .depth(menu.getDepth())
                    .build();
        }).toList();

        return ResponseEntity.ok(list);
    }



    @GetMapping("/all")
    public ResponseEntity test() {

        List<Menu> list = menuRepo.findAllMenus();

        return ResponseEntity.ok(list);
    }

}
