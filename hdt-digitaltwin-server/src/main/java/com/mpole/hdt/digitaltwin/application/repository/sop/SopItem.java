package com.mpole.hdt.digitaltwin.application.repository.sop;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sop_items", indexes = {
        @Index(name = "idx_sop_items_step_id", columnList = "step_id"),
        @Index(name = "idx_sop_items_item_type", columnList = "item_type"),
        @Index(name = "idx_sop_items_group_key", columnList = "group_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItem extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private SopStep step;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;  // SINGLE_BUTTON, MULTI_BUTTON

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;

    @Column(name = "group_key", length = 50)
    private String groupKey;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "ui_style", length = 30)
    private String uiStyle;

    @Column(name = "action_label", length = 50)
    private String actionLabel;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopItemOption> options = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activation_rule_id")
    private SopRule activationRule;

    @PrePersist
    @PreUpdate
    public void validateItem() {
        // SINGLE_BUTTON일 때 actionLabel 기본값 설정
        if ("SINGLE_BUTTON".equals(itemType) && (actionLabel == null || actionLabel.isBlank())) {
            actionLabel = "완료";
        }
    }
}
