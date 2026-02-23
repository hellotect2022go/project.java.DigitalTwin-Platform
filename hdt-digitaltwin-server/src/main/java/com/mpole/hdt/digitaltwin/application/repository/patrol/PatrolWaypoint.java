package com.mpole.hdt.digitaltwin.application.repository.patrol;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="patrol_waypoint", indexes = {
})
public class PatrolWaypoint {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long waypointId;

    @ManyToOne
    @JoinColumn(columnDefinition = "route_id")
    @Comment("경로 ID")
    private PatrolRoute patrolRoute;

    @Comment("순서")
    private Integer seqNum;

    @Comment("X 좌표")
    @Column(name = "pos_x")
    private float posX;

    @Comment("Y 좌표")
    @Column(name = "pos_y")
    private float posY;

    @Comment("Z 좌표")
    @Column(name = "pos_z")
    private float posZ;

    @Comment("체류시간 (초)")
    private int durSec;


}
