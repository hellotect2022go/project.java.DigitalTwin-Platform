package com.mpole.hdt.digitaltwin.application.repository.patrol;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="patrol_route")
public class PatrolRoute extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long routeId;

    @Comment("경로명")
    private String routeName;

    @Comment("설명")
    private String description;

    @Comment("경로활성 여부")
    private boolean active;

    @OneToMany(mappedBy = "patrolRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PatrolWaypoint> patrolWaypoints = new ArrayList<>();
}
