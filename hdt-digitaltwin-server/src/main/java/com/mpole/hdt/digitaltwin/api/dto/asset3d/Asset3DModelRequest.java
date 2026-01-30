package com.mpole.hdt.digitaltwin.api.dto.asset3d;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset3DModelRequest {
    
    private String modelName;
    private String fileExtension;
    private Integer polygonCount;
    private Float defaultScale;
    private String description;
    private String metadata;
    private Boolean enabled;
}

