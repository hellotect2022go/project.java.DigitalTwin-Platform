package com.mpole.hdt.gateway.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardBodyItem {
    // [필수] 관제점 코드 (Unique Key)
    private String pointCode;

    // [필수] 관제값 (숫자, 불리언 모두 String으로 변환하여 전송)
    private String value;

    // [필수] 원천(SI) 데이터 발생/갱신 시간 (ISO-8601)
    private String soureTimestamp;

    // [선택] 데이터 품질 (GOOD, BAD, UNCERTAIN) - 기본값 GOOD
    @Builder.Default
    private String quality = "GOOD";

    // [선택] 장비 코드 (상위 자산 매핑용)
    private String deviceCode;

    // [선택] 알람 여부 (true일 경우 알람 발생)
    private Boolean alarmYn;

    /**
     * 시스템별 확장 필드
     * - unit
     * - severity
     * - vendorField...
     */
    // [선택] 확장 필드 (표준 필드 위에 시스템별 특수 데이터가 필요할 때 사용)
    private Map<String, Object> payload;
}
