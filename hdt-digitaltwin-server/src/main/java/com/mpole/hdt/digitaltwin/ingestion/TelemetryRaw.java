package com.mpole.hdt.digitaltwin.ingestion;

import java.util.List;

public record TelemetryRaw(
        Header header,
        List<Body> body,
        Object result
) {
    public record Header(
            String trxId,
            String timestamp,
            String senderId,
            String schemaVersion,
            String messageType
    ) {}

    public record Body(
            String pointCode,
            String value,
            @com.fasterxml.jackson.annotation.JsonProperty("soureTimestamp") // 게이트웨이 JSON 키가 이 철자라면 그대로
            String sourceTimestamp,
            String quality,
            String deviceCode
    ) {}
}