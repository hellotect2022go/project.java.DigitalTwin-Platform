package com.mpole.hdt.digitaltwin.infrastructure.external.mssql.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@Table(name = "si_point_raw")
@IdClass(DevicePointId.class)
public class ExternalMSViewEntity {

    @Id
    @Column(name = "device_code")
    private String deviceCode;

    @Id
    @Column(name = "point_code")
    private String pointCode;

    @Column(name = "point_name")
    private String pointName;

    @Column(name="object_type")
    private String objectType;

    @Column(name="alarm_yn")
    private String alarmYn;

    @Column(name = "value_raw")
    private String valueRaw;

    @Column(name = "update_datetime")
    private OffsetDateTime updateDateTime;
}
