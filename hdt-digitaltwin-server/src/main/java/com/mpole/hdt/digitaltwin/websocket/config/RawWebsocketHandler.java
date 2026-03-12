package com.mpole.hdt.digitaltwin.websocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawWebsocketHandler extends TextWebSocketHandler {
    // 연결된 모든 세션을 관리 (메모리 누수 방지를 위해 ConcurrentHashMap 사용)
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("🔗 새 웹소켓 연결: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지를 받았을 때 로직 (필요시)
        log.info("받은 메시지: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("❌ 웹소켓 연결 종료: {}", session.getId());
    }

    // 데이터를 브로드캐스팅하는 메서드 (Consumer에서 호출용)
    public <T>void broadcast(T payload) {
        try {
            String jsonString = objectMapper.writeValueAsString(payload);
            sessions.values().forEach(s -> {
                try {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(jsonString));
                    }
                } catch (IOException e) {
                    log.error("전송 에러", e);
                }
            });
        }catch (JsonProcessingException e) {
            log.error("JSON 변환 실패", e);
        }
    }
}
