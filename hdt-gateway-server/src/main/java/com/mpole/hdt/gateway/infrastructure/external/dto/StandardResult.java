package com.mpole.hdt.gateway.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardResult {
    // [필수] 성공 여부
    private Boolean success;

    // [필수] 응답 코드 (성공 : 0000, 에러 : 4xxx, 5xxx)
    private String code;

    // [필수] 응답 메시지
    private String message;

    // [필수] 응답 생성 일시
    private String timestamp;

    public static StandardResult accepted() {
        return StandardResult.builder()
                .success(true)
                .code("0000")
                .message("Accepted")
                .timestamp(java.time.OffsetDateTime.now().toString())
                .build();
    }
}
