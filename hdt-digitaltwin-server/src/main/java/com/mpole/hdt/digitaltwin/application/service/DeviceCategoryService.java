package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.device.DeviceCategoryDTO;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceCategory;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceCategoryService {

    private final DeviceCategoryRepository deviceCategoryRepository;

    // Device Category 트리구조 조회
    @Transactional(readOnly = true)
    public List<DeviceCategoryDTO> getCtegoryInfoTree() {
        List<DeviceCategory> categories = deviceCategoryRepository.findAllWithParent();

        // 트리구조로 만들기
        Map<Long, DeviceCategoryDTO> map = new HashMap<>();
        List<DeviceCategoryDTO> roots = new ArrayList<>();

        //DTO 변환 <id, dto>
        for (DeviceCategory deviceCategory : categories) {
            DeviceCategoryDTO dto = this.toDto(deviceCategory);
            map.put(dto.getCategoryId(), dto);
        }

        map.forEach((key, value)->{
            if (value.getIsRoot()) {// 부모객체
                roots.add(value);
            }else {
                DeviceCategoryDTO parent = map.get(value.getParentId());
                parent.getChildren().add(value);
            }
        });

        return roots;
    }

    // Device Category CRUD 기능도 추가가 되어야 할 듯 (관리자 페이지용)

    private DeviceCategoryDTO toDto(DeviceCategory entity) {
        return DeviceCategoryDTO.builder()
                .categoryId(entity.getCategoryId())
                .parentId(entity.getParent() != null ? entity.getParent().getCategoryId() : null)
                .depth(entity.getDepth())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.isEnabled())
                .isLeaf(entity.isLeaf())
                .isRoot(entity.isRoot())
                .fullPath(buildFullPath(entity))
                .build();
    }

    private String buildFullPath(DeviceCategory entity) {
        List<String> path = new ArrayList<>();

        DeviceCategory current = entity;
        while (current != null) {
            path.add(current.getCategoryName());
            current = current.getParent();
        }
        return String.join(" > ",path.reversed());

    }


}
