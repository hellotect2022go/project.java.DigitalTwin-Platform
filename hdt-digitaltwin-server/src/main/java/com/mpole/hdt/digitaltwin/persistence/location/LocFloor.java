package com.mpole.hdt.digitaltwin.persistence.location;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_loc_floor", indexes = {

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocFloor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("건물 층 ID")
    private Long floorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private LocBuilding locBuilding;

    @OneToMany(mappedBy = "locFloor", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LocZone> locZoneList = new ArrayList<>();

    private String name;

    @Comment("층 구분 지하(-)/ 지상(+)")
    private Integer floorNum;
}
