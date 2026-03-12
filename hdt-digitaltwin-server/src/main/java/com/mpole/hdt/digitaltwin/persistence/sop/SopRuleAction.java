package com.mpole.hdt.digitaltwin.persistence.sop;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sop_rule_actions", indexes = {
        @Index(name = "idx_sop_rule_actions_rule_id", columnList = "rule_id"),
        @Index(name = "idx_sop_rule_actions_step_id", columnList = "step_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleAction extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private SopRule rule;

    @Column(name = "step_id", nullable = false)
    private Long stepId;
}
