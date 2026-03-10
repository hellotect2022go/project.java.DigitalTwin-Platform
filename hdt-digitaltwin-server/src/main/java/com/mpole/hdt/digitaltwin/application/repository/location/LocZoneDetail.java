package com.mpole.hdt.digitaltwin.application.repository.location;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "loc_zone_detail", indexes = {

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocZoneDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("건물 > 층 > 구역 > 구역상세 ID")
    private Long zoneDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private LocZone locZone;

    private String name;
}
