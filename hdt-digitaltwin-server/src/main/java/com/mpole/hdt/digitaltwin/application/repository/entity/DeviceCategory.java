package com.mpole.hdt.digitaltwin.application.repository.entity;

import com.mpole.hdt.digitaltwin.api.dto.devicecategory.DeviceCategoryDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="device_categories", indexes = {
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_depth", columnList = "depth")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCategory extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private DeviceCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeviceCategory> children = new ArrayList<>();

    private Integer depth; // 0=대분류, 1=중분류, 2=소분류

    @Column(nullable = false, unique = true, length = 100)
    private String categoryCode;

    @Column(nullable = false, length = 200)
    private String categoryNameKo;

    @Column(length = 200)
    private String categoryNameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    // 비즈니스 로직
    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

//    public DeviceCategoryDTO toDto() {
//        return DeviceCategoryDTO.builder()
//                .categoryId(this.getId())
//                .parentId(this.getParent() != null ? this.getParent().getId() : null)
//                .depth(this.getDepth())
//                .categoryCode(this.getCategoryCode())
//                .categoryNameKo(this.getCategoryNameKo())
//                .categoryNameEn(this.getCategoryNameEn())
//                .description(this.getDescription())
//                .displayOrder(this.getDisplayOrder())
//                .enabled(this.isEnabled())
//                .createdAt(this.getCreatedAt())
//                .updatedAt(this.getUpdatedAt())
//                .build();
//    }

//    public DeviceCategoryDTO toDtoWithFullPath() {
//        DeviceCategoryDTO dto = toDto();
//        dto.setFullPath(buildFullPath(this));
//        return dto;
//    }
//
//    // 부모가 null 이 될때까지 상위로 올라간다.
//    private String buildFullPath(DeviceCategory category) {
//        List<String> path = new ArrayList<>();
//        DeviceCategory current = category;
//        while (current != null) {
//            path.add(0, current.getCategoryNameKo());
//            current = current.getParent();
//        }
//        return String.join(" > ", path);
//    }

}
