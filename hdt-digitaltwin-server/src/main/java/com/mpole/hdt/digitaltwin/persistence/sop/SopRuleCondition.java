package com.mpole.hdt.digitaltwin.persistence.sop;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sop_rule_conditions", indexes = {
        @Index(name = "idx_sop_rule_conditions_rule_id", columnList = "rule_id"),
        @Index(name = "idx_sop_rule_conditions_item_id", columnList = "item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRuleCondition extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private SopRule rule;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "option_id")
    private Long optionId;
}
