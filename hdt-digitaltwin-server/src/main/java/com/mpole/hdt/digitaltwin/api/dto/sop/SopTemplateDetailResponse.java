package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopTemplateDetailResponse {

    private Long id;
    private String eventType;
    private String name;
    private Integer version;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<SopStepDetailResponse> steps;
}
