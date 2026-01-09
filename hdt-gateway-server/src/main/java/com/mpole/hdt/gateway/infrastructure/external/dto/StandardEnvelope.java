package com.mpole.hdt.gateway.infrastructure.external.dto;


import lombok.*;
import org.osgi.annotation.bundle.Header;

import javax.xml.transform.Result;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardEnvelope<T> {
    // [필수] 메타데이터
    private StandardHeader header;

    // [필수] 데이터 목록 (Micro-Batch 지원을 위해 List 사용)
    private List<StandardBodyItem> body;

    // [선택] 처리 결과 (요청 시 생략 가능, 응답 시 필수)
    private StandardResult result;
}
