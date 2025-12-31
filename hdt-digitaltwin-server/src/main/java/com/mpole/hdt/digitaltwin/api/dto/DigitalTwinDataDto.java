package com.mpole.hdt.digitaltwin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 디지털 트윈 데이터 DTO
 * vwDigitalTwin_01 뷰테이블 기반 샘플 인터페이스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalTwinDataDto {
    
    // 자산 식별 정보
    private String assetId;           // 자산 ID
    private String assetName;         // 자산명
    private String assetType;         // 자산 유형 (예: BUILDING, EQUIPMENT, FACILITY)
    private String location;          // 위치
    
    // 장비 데이터
    private String equipmentId;       // 장비 ID
    private String equipmentName;     // 장비명
    private String equipmentStatus;   // 장비 상태 (NORMAL, WARNING, ERROR, OFFLINE)
    
    // 센서 데이터
    private Double temperature;       // 온도 (°C)
    private Double humidity;          // 습도 (%)
    private Double power;             // 전력 (kW)
    private Double voltage;           // 전압 (V)
    private Double current;           // 전류 (A)
    
    // 운영 데이터
    private Boolean isOperating;      // 운영 중 여부
    private Integer operatingTime;    // 운영 시간 (분)
    private Double efficiency;        // 효율 (%)
    
    // 메타 정보
    private LocalDateTime timestamp;  // 데이터 생성 시각
    private LocalDateTime lastUpdated; // 최종 업데이트 시각
}

