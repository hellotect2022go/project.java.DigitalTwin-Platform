package com.mpole.hdt.digitaltwin.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * WebSocket STOMP 설정
 * Unity와의 실시간 양방향 통신을 위한 설정
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureWebSocketTransport(org.springframework.web.socket.config.annotation.WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(50 * 1024 * 1024);      // 최대 메시지 크기: 50MB
        registration.setSendBufferSizeLimit(50 * 1024 * 1024);   // 전송 버퍼 크기: 50MB
        registration.setSendTimeLimit(20 * 1000);               // 전송 시간 제한: 20초
    }

    /**
     * STOMP 엔드포인트 등록
     * Unity에서 연결할 WebSocket 엔드포인트 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS 지원 (브라우저 호환성)
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // 순수 WebSocket (Unity는 보통 이쪽 사용)
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*");
        
        log.info("===== WebSocket STOMP 엔드포인트 등록 완료 =====");
        log.info("연결 URL: ws://localhost:8082/stomp");
    }

    /**
     * 메시지 브로커 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 서버로 메시지 보낼 때 prefix
        registry.setApplicationDestinationPrefixes("/pub");
        
        // 서버가 클라이언트로 메시지 보낼 때 prefix
        // 심플 브로커 활성화 (인메모리 방식)
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();
        
        registry.enableSimpleBroker("/sub")
                .setHeartbeatValue(new long[]{10000, 10000}) // 10초마다 heartbeat
                .setTaskScheduler(scheduler);
        
        log.info("===== 메시지 브로커 설정 완료 =====");
        log.info("Publisher prefix: /pub");
        log.info("Subscriber prefix: /sub");
    }

    /**
     * WebSocket 연결 성공 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("===== 🔗 WebSocket 연결 성공 =====");
        log.info("Session ID: {}", sessionId);
        log.info("Connect Time: {}", java.time.LocalDateTime.now());
    }

    /**
     * WebSocket 구독 이벤트
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        log.info("===== 📡 채널 구독 =====");
        log.info("Session ID: {}", sessionId);
        log.info("Destination: {}", destination);
    }

    /**
     * WebSocket 연결 종료 이벤트
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("===== ❌ WebSocket 연결 종료 =====");
        log.info("Session ID: {}", sessionId);
        log.info("Disconnect Time: {}", java.time.LocalDateTime.now());
    }
}
