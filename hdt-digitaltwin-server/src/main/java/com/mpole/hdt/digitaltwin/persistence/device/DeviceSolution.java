package com.mpole.hdt.digitaltwin.persistence.device;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tbl_device_solution", indexes = {
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSolution extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("솔루션 id")
    private Long solutionId;

    @Column(unique = true)
    private String solutionName;

    private String ipAddress;

    private String protocolType;

    @Comment("관제점 활성여부")
    private Boolean active;

    @Comment("관제점 설명")
    private String description;

    @OneToMany(mappedBy = "deviceSolution")
    @Builder.Default
    List<Device> devices = new ArrayList<>();
}
