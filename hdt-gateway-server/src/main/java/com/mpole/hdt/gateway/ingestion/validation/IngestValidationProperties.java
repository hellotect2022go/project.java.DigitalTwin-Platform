package com.mpole.hdt.gateway.ingestion.validation;

public final class IngestValidationProperties {
    private IngestValidationProperties() {}

    /** 수십만 포인트 대비: 마이크로배치 상한 */
    public static final int MAX_RECORDS_PER_REQUEST = 10_000;

    /** body가 비어있으면 의미 없음 → 400 처리 권장 */
    public static final boolean REJECT_EMPTY_BODY = true;

    /** schemaVersion 기본값(누락 방어) - 필요시 사용 */
    public static final String DEFAULT_SCHEMA_VERSION = "1.0";

    /** senderId 최소 길이 */
    public static final int MIN_SENDER_ID_LEN = 2;
}
