package com.mpole.hdt.digitaltwin.service;

import com.mpole.hdt.digitaltwin.api.dto.menu.MenuCreateRequest;
import com.mpole.hdt.digitaltwin.api.dto.menu.MenuPutSortOrderRequest;
import com.mpole.hdt.digitaltwin.persistence.menu.Menu;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuBulkRepository;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuBulkRepository menuBulkRepository;

    @Transactional
    public void createMenu(MenuCreateRequest menuCreateRequest) {
        if (menuRepository.existsByCategoryId(menuCreateRequest.categoryId())) {
            throw new IllegalStateException("이미 존재하는 메뉴 입니다.");
        }

        Menu menu = Menu.create(menuCreateRequest);
        menuRepository.save(menu);
    }

    @Transactional
    public void putMenuSortOrder(List<MenuPutSortOrderRequest> requestLists) {
        menuBulkRepository.updateMenuSortOrder(requestLists);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }
}
