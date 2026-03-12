package com.mpole.hdt.digitaltwin.persistence.sop;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sop_step_statuses", indexes = {
        @Index(name = "idx_sop_step_status_instance_id", columnList = "instance_id"),
        @Index(name = "idx_sop_step_status_step_id", columnList = "step_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStepStatus extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private SopInstance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private SopStep step;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;
}
