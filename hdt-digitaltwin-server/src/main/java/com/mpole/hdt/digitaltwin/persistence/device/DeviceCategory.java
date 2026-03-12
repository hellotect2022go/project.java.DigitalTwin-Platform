package com.mpole.hdt.digitaltwin.persistence.device;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tbl_device_category", indexes = {
        @Index(name = "idx_category_parent_id", columnList = "category_parent_id"),
        @Index(name = "idx_depth", columnList = "depth")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCategory extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_parent_id")
    private DeviceCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeviceCategory> children = new ArrayList<>();

    private Integer depth; // 0=대분류, 1=중분류, 2=소분류

    @Column(nullable = false, length = 200)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    private boolean isRoot;
    private boolean isLeaf;

}
