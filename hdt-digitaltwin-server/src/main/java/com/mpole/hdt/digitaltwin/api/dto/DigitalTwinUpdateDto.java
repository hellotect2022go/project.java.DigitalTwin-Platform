package com.mpole.hdt.digitaltwin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 디지털 트윈 업데이트 DTO
 * WebSocket STOMP로 전송할 실시간 변경 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalTwinUpdateDto {
    
    private String assetId;
    private String equipmentId;
    private String updateType;        // 업데이트 타입 (SENSOR_UPDATE, STATUS_CHANGE, OPERATION_CHANGE)
    
    // 변경된 필드와 값
    private String fieldName;         // 변경된 필드명
    private Object oldValue;          // 이전 값
    private Object newValue;          // 새 값
    
    private LocalDateTime timestamp;  // 변경 시각
}

