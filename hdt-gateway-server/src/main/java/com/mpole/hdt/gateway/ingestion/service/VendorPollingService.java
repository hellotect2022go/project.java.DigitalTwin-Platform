package com.mpole.hdt.gateway.ingestion.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class VendorPollingService {         // Dummy 서비스 클래스
    private final WebClient externalWebClient;

    public VendorPollingService(WebClient externalWebClient) {
        this.externalWebClient = externalWebClient;
    }

    public String pollOnce() {
        return externalWebClient
                .get()
                .uri("/dummy")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
