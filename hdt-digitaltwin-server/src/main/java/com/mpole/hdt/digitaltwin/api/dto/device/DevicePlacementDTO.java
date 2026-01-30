package com.mpole.hdt.digitaltwin.api.dto.device;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicePlacementDTO {
    
    private Long id;
    private Long deviceId;
    
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
    
    // Unity 구조 정보
    private String floorLevel;
    private String layerName;
    private String parentObject;
    private String gameObjectName;
    
    // 메타데이터
    private Map<String, Object> customAttributes;
    
    private Boolean enabled;
    private String createdBy;
    private String updatedBy;
}

