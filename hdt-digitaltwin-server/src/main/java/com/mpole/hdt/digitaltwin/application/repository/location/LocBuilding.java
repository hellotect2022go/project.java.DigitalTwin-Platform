package com.mpole.hdt.digitaltwin.application.repository.location;

import com.mpole.hdt.digitaltwin.application.repository.device.Device;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loc_building", indexes = {

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("건물 ID")
    private Long buildingId;

    private String name;

    @OneToMany(mappedBy = "locBuilding", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LocFloor> locFloorList = new ArrayList<>();

}
