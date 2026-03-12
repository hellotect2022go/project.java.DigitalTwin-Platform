package com.mpole.hdt.digitaltwin.persistence.sop;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sop_steps", indexes = {
        @Index(name = "idx_sop_steps_template_id", columnList = "template_id"),
        @Index(name = "idx_sop_steps_order", columnList = "step_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStep extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private SopTemplate template;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activation_rule_id")
    private SopRule activationRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completion_rule_id")
    private SopRule completionRule;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopItem> items = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void validateStepOrder() {
        if (stepOrder == null || stepOrder < 1) {
            stepOrder = 1;
        }
    }
}
