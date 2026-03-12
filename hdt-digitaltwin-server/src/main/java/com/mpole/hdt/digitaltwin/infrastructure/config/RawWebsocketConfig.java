package com.mpole.hdt.digitaltwin.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class RawWebsocketConfig implements WebSocketConfigurer {

    private final RawWebsocketHandler rawWebsocketHandler;

    public RawWebsocketConfig(RawWebsocketHandler rawWebsocketHandler) {
        this.rawWebsocketHandler = rawWebsocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(rawWebsocketHandler, "/ws") // 엔드포인트: ws://localhost:8082/raw-ws
                .setAllowedOrigins("*");
    }
}
