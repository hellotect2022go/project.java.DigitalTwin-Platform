package com.mpole.hdt.gateway.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// 엑센 솔루션 통합SI 호출 설정 - 통합SI/벤더 호출 용도(=Adapter 전용)
@Configuration
public class ExternalWebClientConfig {
    @Bean
    public WebClient externalWebClient(
            @Value("${external.vendor.base-url}") String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
