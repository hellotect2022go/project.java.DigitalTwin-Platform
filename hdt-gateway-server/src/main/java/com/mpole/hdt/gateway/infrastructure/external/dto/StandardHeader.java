package com.mpole.hdt.gateway.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardHeader {

    /** [필수] 트랜잭션 추적 ID (UUID 권장) */
    private String trxId;

    /** [필수] Gateway 수신 시각 (ISO-8601, timezone 포함) ex: 2026-01-09T14:10:00+09:00 */
    private String timestamp;

    /** [필수] 발신 시스템 ID (예: HDT_GATEWAY, INTEGRATION_SI) */
    private String senderId;

    /** [필수] 스키마 버전 (예: 1.0) */
    @Builder.Default
    private String schemaVersion = "1.0";

    // [권장] 메시지 타입 (SNAPSHOT, UPDATE, ALARM, CONTROL_REQ 등)
    private MessageType messageType;

}
