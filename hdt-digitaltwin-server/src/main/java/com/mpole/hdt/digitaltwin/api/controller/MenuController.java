package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.menu.MenuCreateRequest;
import com.mpole.hdt.digitaltwin.api.dto.menu.MenuPutSortOrderRequest;
import com.mpole.hdt.digitaltwin.persistence.menu.Menu;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuRepository;
import com.mpole.hdt.digitaltwin.api.dto.menu.MenuResponse;
import com.mpole.hdt.digitaltwin.persistence.menu.RoleMenuRepository;
import com.mpole.hdt.digitaltwin.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuRepository menuRepo;
    private final RoleMenuRepository roleMenuRepo;
    private final MenuService menuService;


    @PostMapping
    public ResponseEntity<ApiResponse> createMenuFromCategory(@RequestBody MenuCreateRequest request) {
        menuService.createMenu(request);
        return ResponseEntity.ok(ApiResponse.success("메뉴생성"));
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse> deleteMenu(@PathVariable("menuId") Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok(ApiResponse.success("메뉴삭제"));
    }

    @PutMapping("/sort-orders")
    public ResponseEntity<ApiResponse> putMenuSortOrder(@RequestBody List<MenuPutSortOrderRequest> requests) {
        menuService.putMenuSortOrder(requests);
        return ResponseEntity.ok(ApiResponse.success("메뉴 순서 변경 저장"));
    }



    @GetMapping
    public ResponseEntity fetchMenuByRole(@AuthenticationPrincipal(expression = "authorities") Collection<? extends GrantedAuthority> authorities) {

        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        List<MenuResponse> list = menuRepo.findMenusByRole(roles).stream().map(menu->{
            return MenuResponse.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .sortOrder(menu.getSortOrder())
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
