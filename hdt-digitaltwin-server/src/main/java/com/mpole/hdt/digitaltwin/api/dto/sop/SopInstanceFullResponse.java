package com.mpole.hdt.digitaltwin.api.dto.sop;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * SOP 인스턴스 전체 정보 응답 (조회 최적화)
 * 한 번의 API 호출로 인스턴스, 템플릿, 결과, 상태를 모두 제공
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SopInstanceFullResponse {

    // 인스턴스 기본 정보
    private Long id;
    private String eventId;
    private Long templateId;
    private String status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // 템플릿 정보
    private String templateName;
    private String eventType;
    private Integer templateVersion;

    // 템플릿 구조 (Steps + Items)
    private List<SopStepWithItemsResponse> steps;

    // 현재 선택된 항목 결과
    private List<SopItemResultResponse> itemResults;

    // 각 Step의 활성화 상태
    private List<SopStepStatusResponse> stepStatuses;

    // 완료 가능 여부
    private Boolean canComplete;
}
