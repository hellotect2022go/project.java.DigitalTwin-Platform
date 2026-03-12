package com.mpole.hdt.digitaltwin.application.repository.device;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import com.mpole.hdt.digitaltwin.application.repository.location.LocBuilding;
import com.mpole.hdt.digitaltwin.application.repository.location.LocFloor;
import com.mpole.hdt.digitaltwin.application.repository.location.LocZone;
import com.mpole.hdt.digitaltwin.application.repository.location.LocZoneDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Device (실제 장비 인스턴스)
 * DeviceModel을 기반으로 생성된 실제 설치된 장비
 */
@Entity
@Table(name = "device", indexes = {

})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("우리쪽 device 아이디")
    private Long deviceId;

    @Column(unique = true, name = "join_key")
    @Comment("view 테이블로 부터 unique key 를 만들어서 저장하는 필드")
    private String joinKey;

//    @Comment("view 테이블로 부터 device_code")
//    private String deviceCode;

//    @Comment("view 테이블로 부터 point_code")
//    private String pointCode;

    @Comment("디바이스 이름")
    private String deviceName;

    @Comment("디바이스 활성여부")
    private Boolean active;

    @Comment("디바이스 설명")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    @Comment("FK 장비 카테고리 참조")
    private DeviceCategory deviceCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="asset_id")
    @Comment("FK 장비 3D 모델정보 참조")
    private Device3dAsset device3dAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_device_id")
    @Comment("부모장비 참조 id")
    private Device parentDevice;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeviceSystemMap> deviceSystemMaps = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="solution_id")
    @Comment("솔루션 정보 참조 (ADAM, VMS, 등등)")
    private DeviceSolution deviceSolution;

    //@OneToOne(mappedBy = "device", fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transform_id", unique = true)
    private DeviceTransform deviceTransform;

    // ======================================
    // 위치 관련 정보
    // =====================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private LocBuilding locBuilding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private LocFloor locFloor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private LocZone locZone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_detail_id")
    private LocZoneDetail locZoneDetail;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device")
    private List<DevicePoint> devicePoints;

    // 정보 삭제 (Delete)
    public void removeTransform() {
        this.deviceTransform = null;
    }
    public void removeLocInfo() {
        this.locBuilding = null;
        this.locFloor = null;
        this.locZone = null;
        this.locZoneDetail = null;
    }

    
}
