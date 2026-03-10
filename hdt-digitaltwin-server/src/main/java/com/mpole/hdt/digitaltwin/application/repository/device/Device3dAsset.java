package com.mpole.hdt.digitaltwin.application.repository.device;

import com.mpole.hdt.digitaltwin.application.repository.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="device_3d_asset", indexes = {
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device3dAsset extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;

    @Column(name = "asset_name", nullable = false, unique = true, length = 200)
    private String assetName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean active = true;
}
