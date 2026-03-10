package com.mpole.hdt.digitaltwin.application.repository.device;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Device (실제 장비 인스턴스)
 * DeviceModel을 기반으로 생성된 실제 설치된 장비
 */
@Entity
@Table(name = "device_point", indexes = {
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicePoint extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Comment("우리쪽 device 아이디")
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Comment("view 테이블로 부터 device_code")
    private String deviceCode;

    @Column(unique = true)
    @Comment("view 테이블로 부터 point_code")
    private String pointCode;

    @Comment("view 테이블로 부터 point_code")
    private String pointName;

    @Comment("디바이스 활성여부")
    private Boolean active;

    @Comment("단위 (예: ℃, kWh, %)")
    private String unit;

    private String pointType;

    private Boolean alarm;

}
