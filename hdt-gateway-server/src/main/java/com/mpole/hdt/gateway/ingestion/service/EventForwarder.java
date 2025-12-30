package com.mpole.hdt.gateway.ingestion.service;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class EventForwarder {
    private final WebClient eventServerWebClient;

    public EventForwarder(WebClient eventServerWebClient) {
        this.eventServerWebClient = eventServerWebClient;
    }

//    public void forward(StandardEvent event) {
//        eventServerWebClient
//                .post()
//                .uri("/internal/events")
//                .bodyValue(event)
//                .retrieve()
//                .toBodilessEntity()
//                .block(); // 지금은 뼈대라 block 허용
//    }

    public Mono<Void> forward(StandardEvent event) {
        return eventServerWebClient
                .post()
                .uri("/internal/events")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
