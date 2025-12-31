package com.mpole.hdt.digitaltwin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 디지털 트윈 응답 DTO
 * REST API 응답용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalTwinResponseDto {
    
    private boolean success;
    private String message;
    private List<DigitalTwinDataDto> data;
    private int totalCount;
}

