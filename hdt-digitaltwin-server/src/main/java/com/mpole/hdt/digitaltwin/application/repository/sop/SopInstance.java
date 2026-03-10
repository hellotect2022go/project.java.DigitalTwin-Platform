package com.mpole.hdt.digitaltwin.application.repository.sop;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sop_instances", indexes = {
        @Index(name = "idx_sop_instances_event_id", columnList = "event_id"),
        @Index(name = "idx_sop_instances_template_id", columnList = "template_id"),
        @Index(name = "idx_sop_instances_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopInstance extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 50)
    private String eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private SopTemplate template;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopItemResult> itemResults = new ArrayList<>();

    @OneToMany(mappedBy = "instance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SopStepStatus> stepStatuses = new ArrayList<>();
}
