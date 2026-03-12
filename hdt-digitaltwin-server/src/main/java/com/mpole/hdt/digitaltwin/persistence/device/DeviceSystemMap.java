package com.mpole.hdt.digitaltwin.persistence.device;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Device (실제 장비 인스턴스)
 * DeviceModel을 기반으로 생성된 실제 설치된 장비
 */
@Entity
@Table(name = "tbl_device_system_map", indexes = {
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSystemMap extends DateEntity {

    @EmbeddedId
    private DeviceSystemMapId id;

    @MapsId("deviceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @MapsId("systemId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="system_id", nullable = false)
    private DeviceSystem deviceSystem;

    // --- 내부 정적 클래스로 정의 ---
    @Embeddable
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class DeviceSystemMapId implements Serializable {
        private Long deviceId;
        private Long systemId;
    }

    // 생성자나 빌더에서 id를 초기화해주면 됩니다.
    public DeviceSystemMap(Device device, DeviceSystem deviceSystem) {
        this.device = device;
        this.deviceSystem = deviceSystem;
        this.id = new DeviceSystemMapId(device.getDeviceId(), deviceSystem.getSystemId());
    }
}
