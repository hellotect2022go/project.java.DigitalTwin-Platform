package com.mpole.hdt.digitaltwin.application.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_system_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSystemType extends DateEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sysCode;

    @Column(nullable = false, length = 100)
    private String sysNameKo;

    @Column(length = 100)
    private String sysNameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String iconUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;


}
