package com.mpole.hdt.gateway.ingestion.validation;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

import static com.mpole.hdt.gateway.ingestion.validation.IngestValidationProperties.*;

public final class IngestValidator {
    private IngestValidator() {}

    @Getter
    public static class ValidationResult {
        private final boolean ok;
        private final String message;

        private ValidationResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static ValidationResult ok() { return new ValidationResult(true, "OK"); }
        public static ValidationResult fail(String msg) { return new ValidationResult(false, msg); }
    }

    @SuppressWarnings("rawtypes")
    public static ValidationResult validate(StandardEnvelope envelope) {
        if (envelope == null) return ValidationResult.fail("envelope is null");
        if (envelope.getHeader() == null) return ValidationResult.fail("header is null");

        // header 필수
        String trxId = safeTrim(envelope.getHeader().getTrxId());
        String ts = safeTrim(envelope.getHeader().getTimestamp());
        String senderId = safeTrim(envelope.getHeader().getSenderId());
        String schemaVersion = safeTrim(envelope.getHeader().getSchemaVersion());

        if (trxId.isEmpty()) return ValidationResult.fail("header.trxId is required");
        if (ts.isEmpty()) return ValidationResult.fail("header.timestamp is required");
        if (senderId.isEmpty() || senderId.length() < MIN_SENDER_ID_LEN)
            return ValidationResult.fail("header.senderId is required");
        if (schemaVersion.isEmpty()) return ValidationResult.fail("header.schemaVersion is required");

        // messageType은 표준서/내부규칙에 따라 "권장 필수"로 가져가고 싶으면 여기서 체크
        // if (envelope.getHeader().getMessageType() == null) return ValidationResult.fail("header.messageType is required");

        // body 필수
        List body = envelope.getBody();
        if (body == null) return ValidationResult.fail("body is null");

        if (REJECT_EMPTY_BODY && body.isEmpty())
            return ValidationResult.fail("body must not be empty");

        if (body.size() > MAX_RECORDS_PER_REQUEST)
            return ValidationResult.fail("body size exceeds max=" + MAX_RECORDS_PER_REQUEST);

        // body 원소 null 방지(가끔 들어옴)
        boolean hasNull = body.stream().anyMatch(Objects::isNull);
        if (hasNull) return ValidationResult.fail("body contains null element");

        return ValidationResult.ok();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
