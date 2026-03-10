package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sop_rules", indexes = {
        @Index(name = "idx_sop_rules_rule_type", columnList = "rule_type"),
        @Index(name = "idx_sop_rules_template_id", columnList = "template_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopRule extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private SopTemplate template;

    @Column(name = "rule_type", nullable = false, length = 30)
    private String ruleType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expression;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopRuleCondition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopRuleAction> actions = new ArrayList<>();
}
