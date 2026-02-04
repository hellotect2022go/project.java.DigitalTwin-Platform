package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinDataDto;
import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 디지털 트윈 실시간 데이터 전송 서비스
 * 1초 주기로 센서값 변경을 감지하고 WebSocket STOMP로 전송
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalTwinRealtimeService {

    private final DigitalTwinMockService mockService;
    private final SimpMessagingTemplate messagingTemplate;
    
    private int updateCount = 0;

    /**
     * 1초 주기로 센서 데이터 업데이트 및 전송
     * fixedRate: 이전 작업 시작 시점부터 1000ms 후 실행
     */
    //@Scheduled(fixedRate = 1000)
    public void pushSensorDataUpdates() {
        try {
            updateCount++;
            
            // Mock 서비스에서 센서값 업데이트 (변경 사항 반환)
            Map<String, Object> changes = mockService.updateSensorValues();
            
            // 변경이 있는 경우에만 전송
            if (!changes.isEmpty()) {
                // 각 자산별로 변경사항을 STOMP로 전송
                changes.forEach((assetId, fieldChanges) -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Object>> fields = (Map<String, Map<String, Object>>) fieldChanges;
                    
                    fields.forEach((fieldName, values) -> {
                        DigitalTwinUpdateDto updateDto = DigitalTwinUpdateDto.builder()
                                .assetId(assetId)
                                .updateType("SENSOR_UPDATE")
                                .fieldName(fieldName)
                                .oldValue(values.get("old"))
                                .newValue(values.get("new"))
                                .timestamp(LocalDateTime.now())
                                .build();
                        
                        // 개별 자산 채널로 전송
                        messagingTemplate.convertAndSend("/sub/digitaltwin/" + assetId, updateDto);
                    });
                });
                
                // 전체 변경사항을 공통 채널로도 전송
                messagingTemplate.convertAndSend("/sub/digitaltwin/updates", (Object) changes);
                
                log.debug("===== [{}회] 센서 데이터 업데이트 전송: {} 건 =====", updateCount, changes.size());
            }
            
            // 10초마다 전체 데이터도 전송 (Unity에서 전체 동기화용)
            if (updateCount % 10 == 0) {
                pushAllData();
            }
            
        } catch (Exception e) {
            log.error("실시간 데이터 전송 중 오류 발생", e);
        }
    }

    /**
     * 전체 데이터 전송 (10초 주기)
     */
    private void pushAllData() {
        try {
            List<DigitalTwinDataDto> allData = mockService.getAllData();
            messagingTemplate.convertAndSend("/sub/digitaltwin/all", allData);
            log.info("===== 전체 데이터 전송: {} 건 =====", allData.size());
        } catch (Exception e) {
            log.error("전체 데이터 전송 중 오류 발생", e);
        }
    }

    /**
     * 특정 자산의 상태 변경 이벤트 전송
     */
    public void pushStatusChange(String assetId, String oldStatus, String newStatus) {
        try {
            DigitalTwinUpdateDto updateDto = DigitalTwinUpdateDto.builder()
                    .assetId(assetId)
                    .updateType("STATUS_CHANGE")
                    .fieldName("equipmentStatus")
                    .oldValue(oldStatus)
                    .newValue(newStatus)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            // 개별 자산 채널로 전송
            messagingTemplate.convertAndSend("/sub/digitaltwin/" + assetId, updateDto);
            
            // 전체 상태 변경 채널로도 전송
            messagingTemplate.convertAndSend("/sub/digitaltwin/status", updateDto);
            
            log.info("===== 상태 변경 이벤트 전송: {} ({} -> {}) =====", assetId, oldStatus, newStatus);
        } catch (Exception e) {
            log.error("상태 변경 이벤트 전송 중 오류 발생", e);
        }
    }

    /**
     * 연결 확인용 Heartbeat 전송 (30초 주기)
     */
//    @Scheduled(fixedRate = 30000)
//    public void sendHeartbeat() {
//        try {
//            Map<String, Object> heartbeat = Map.of(
//                    "type", "HEARTBEAT",
//                    "timestamp", LocalDateTime.now(),
//                    "serverStatus", "RUNNING",
//                    "activeDataCount", mockService.getAllData().size()
//            );
//
//            messagingTemplate.convertAndSend("/sub/digitaltwin/heartbeat", (Object) heartbeat);
//            log.debug("===== Heartbeat 전송 =====");
//        } catch (Exception e) {
//            log.error("Heartbeat 전송 중 오류 발생", e);
//        }
//    }
}

