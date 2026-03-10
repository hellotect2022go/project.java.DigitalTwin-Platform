package com.mpole.hdt.digitaltwin.application.repository.sop;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sop_item_options", indexes = {
        @Index(name = "idx_sop_item_options_item_id", columnList = "item_id"),
        @Index(name = "idx_sop_item_options_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemOption extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SopItem item;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "ui_style", length = 30)
    private String uiStyle;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activation_rule_id")
    private SopRule activationRule;
}
