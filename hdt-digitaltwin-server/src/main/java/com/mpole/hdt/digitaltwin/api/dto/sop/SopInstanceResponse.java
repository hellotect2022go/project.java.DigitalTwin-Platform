package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopInstanceResponse {

    private Long id;
    private String eventId;
    private Long templateId;
    private String templateName;  // 템플릿 이름
    private String eventType;     // 이벤트 타입
    private String status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
