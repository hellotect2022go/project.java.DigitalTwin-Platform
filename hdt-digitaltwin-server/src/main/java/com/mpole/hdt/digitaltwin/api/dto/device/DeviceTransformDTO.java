package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceTransformDTO {

    private Long locationId;
    
    // Position
    private Float positionX;
    private Float positionY;
    private Float positionZ;
    
    // Rotation
    private Float rotationX;
    private Float rotationY;
    private Float rotationZ;
    
    // Scale
    private Float scaleX;
    private Float scaleY;
    private Float scaleZ;

    private Boolean enabled;
    private String createdBy;
    private String updatedBy;
}

