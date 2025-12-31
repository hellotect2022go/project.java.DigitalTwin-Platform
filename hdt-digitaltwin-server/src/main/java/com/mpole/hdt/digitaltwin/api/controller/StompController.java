package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinDataDto;
import com.mpole.hdt.digitaltwin.application.service.DigitalTwinMockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * WebSocket STOMP 컨트롤러
 * Unity에서 WebSocket 통신으로 데이터 요청 시 사용
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final DigitalTwinMockService mockService;

    /**
     * Unity에서 연결 확인용 메시지
     * /pub/digitaltwin/ping -> /sub/digitaltwin/pong
     */
    @MessageMapping("/digitaltwin/ping")
    @SendTo("/sub/digitaltwin/pong")
    public Map<String, Object> ping(@Payload Map<String, Object> message) {
        log.info("===== Ping 수신: {} =====", message);
        return Map.of(
                "type", "pong",
                "timestamp", java.time.LocalDateTime.now(),
                "receivedMessage", message
        );
    }

    /**
     * Unity에서 특정 자산 데이터 요청
     * /pub/digitaltwin/request/{assetId}
     */
    @MessageMapping("/digitaltwin/request/{assetId}")
    public void requestAssetData(@DestinationVariable String assetId) {
        log.info("===== 자산 데이터 요청: {} =====", assetId);
        
        Optional<DigitalTwinDataDto> data = mockService.getDataByAssetId(assetId);
        
        if (data.isPresent()) {
            // 요청한 자산의 데이터를 해당 채널로 전송
            messagingTemplate.convertAndSend("/sub/digitaltwin/" + assetId, data.get());
        } else {
            // 데이터가 없으면 에러 메시지 전송
            Map<String, String> error = Map.of(
                    "error", "NOT_FOUND",
                    "assetId", assetId,
                    "message", "자산을 찾을 수 없습니다"
            );
            messagingTemplate.convertAndSend("/sub/digitaltwin/error", error);
        }
    }

    /**
     * Unity에서 전체 데이터 요청
     * /pub/digitaltwin/request/all
     */
    @MessageMapping("/digitaltwin/request/all")
    public void requestAllData() {
        log.info("===== 전체 데이터 요청 =====");
        
        List<DigitalTwinDataDto> allData = mockService.getAllData();
        messagingTemplate.convertAndSend("/sub/digitaltwin/all", allData);
    }

    /**
     * Unity에서 특정 자산 구독 시작
     * /pub/digitaltwin/subscribe/{assetId}
     */
    @MessageMapping("/digitaltwin/subscribe/{assetId}")
    public void subscribeAsset(@DestinationVariable String assetId, @Payload Map<String, Object> message) {
        log.info("===== 자산 구독 요청: {} =====", assetId);
        
        // 현재 데이터를 즉시 전송
        Optional<DigitalTwinDataDto> data = mockService.getDataByAssetId(assetId);
        if (data.isPresent()) {
            messagingTemplate.convertAndSend("/sub/digitaltwin/" + assetId, data.get());
            log.info("자산 {} 초기 데이터 전송 완료", assetId);
        }
    }

    /**
     * 채팅/메시지 테스트용 (기존 기능 유지)
     * /pub/chat/message -> /sub/chat/room
     */
    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/room")
    public String message(@Payload String message) {
        log.info("===== 채팅 메시지 수신: {} =====", message);
        return message;
    }
}
