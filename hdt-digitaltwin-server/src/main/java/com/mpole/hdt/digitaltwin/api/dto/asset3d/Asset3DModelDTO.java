package com.mpole.hdt.digitaltwin.api.dto.asset3d;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset3DModelDTO {
    
    private Long id;
    private String modelName;
    private String filePath;
    private String fileExtension;
    private Long fileSize;
    private String fileUrl;
    private String thumbnailUrl;
    private Integer polygonCount;
    private Float defaultScale;
    private String description;
    private String metadata;
    private Boolean enabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    // 추가 정보
    private String fileSizeFormatted; // 예: "2.5 MB"
}

