package com.mpole.hdt.digitaltwin.persistence.location;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_loc_zone", indexes = {

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("건물 > 층 > 구역 ID")
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private LocFloor locFloor;

    @OneToMany(mappedBy = "locZone", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LocZoneDetail> locZoneDetailList = new ArrayList<>();

    private String name;

    private String meshName;
}
