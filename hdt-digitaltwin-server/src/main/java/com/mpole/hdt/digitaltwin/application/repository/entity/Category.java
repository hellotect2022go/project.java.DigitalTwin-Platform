package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="categories", indexes = {
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_depth", columnList = "depth")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

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

}
