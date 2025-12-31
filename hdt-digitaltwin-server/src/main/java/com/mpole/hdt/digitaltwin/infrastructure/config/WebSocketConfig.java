package com.mpole.hdt.digitaltwin.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker // <--- 이 어노테이션이 핵심입니다!
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override // 클라이언트가 서버에 연결할 엔드포인트 주소
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/stomp").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");


//        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
//        te.setPoolSize(1);
//        te.initialize();
//
//        registry.enableSimpleBroker("/sub")
//                .setHeartbeatValue(new long[]{10000, 10000}) // 10초마다 체크
//                .setTaskScheduler(te);
    }

    // 연결 성공 감지
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String message = headerAccessor.getMessage();
        String command = headerAccessor.getCommand().toString();

        System.out.println("==== 새로운 연결 발생! ====");
        System.out.println("sessionId : "+sessionId);
        System.out.println("message : "+message);
        System.out.println("command : "+command);
    }

    // 연결 끊김감지
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        System.out.println("연결 종료됨 - 세션 ID: " + sessionId);
        // 여기서 DB 상태를 '오프라인'으로 바꾸거나 로그를 남깁니다.
    }
}
