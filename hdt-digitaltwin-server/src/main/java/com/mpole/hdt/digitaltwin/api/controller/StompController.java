package com.mpole.hdt.digitaltwin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 /pub/chat/message로 메시지를 보내면 호출됨
    @MessageMapping("/chat/message")
    public void message(String message) {
        // /sub/chat/room/{roomId}를 구독 중인 사람들에게 메시지 전달
        System.out.println("수신메세지 : "+message);
        messagingTemplate.convertAndSend("/sub/chat/room", message);
    }
}
