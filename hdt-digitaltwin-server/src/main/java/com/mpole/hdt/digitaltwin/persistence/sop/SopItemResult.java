package com.mpole.hdt.digitaltwin.persistence.sop;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "sop_item_results", indexes = {
        @Index(name = "idx_sop_item_results_instance_id", columnList = "instance_id"),
        @Index(name = "idx_sop_item_results_item_id", columnList = "item_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopItemResult extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private SopInstance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SopItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private SopItemOption option;

    @Column(nullable = false)
    @Builder.Default
    private Boolean selected = false;

    @Column(name = "selected_by", length = 50)
    private String selectedBy;

    @Column(name = "selected_at")
    private OffsetDateTime selectedAt;
}
