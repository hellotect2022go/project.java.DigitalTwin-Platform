package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopStepStatusResponse {

    private Long id;
    private Long instanceId;
    private Long stepId;
    private Boolean isActive;
    private Boolean isCompleted;
    private OffsetDateTime updatedAt;
}
