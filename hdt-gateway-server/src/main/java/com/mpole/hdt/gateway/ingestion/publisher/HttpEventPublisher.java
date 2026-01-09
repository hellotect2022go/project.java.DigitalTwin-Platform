package com.mpole.hdt.gateway.ingestion.publisher;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpEventPublisher<T> implements EventPublisher<T> {

    @Qualifier("eventServerWebClient")
    private final WebClient webClient;

    @Override
    public Mono<Void> publish(StandardEnvelope<T> envelope) {
        String trxId = (envelope != null && envelope.getHeader() != null) ? envelope.getHeader().getTrxId() : "UNKNOWN";

        return webClient.post()
                .uri("/internal/events")
                .header("X-Trx-Id", trxId)
                .bodyValue(envelope)
                .exchangeToMono(resp -> {
                    HttpStatusCode sc = resp.statusCode();

                    // 성공은 202만 인정 (계약 강제)
                    if (sc.value() == 202) return Mono.empty();

                    // 4xx: 재시도 금지 (데이터/요청이 잘못됨)
                    if (sc.is4xxClientError()) {
                        return resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new NonRetryablePublishException(trxId, sc.value(), body)));
                    }

                    // 5xx 또는 기타: 재시도 대상
                    return resp.bodyToMono(String.class).defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new RetryablePublishException(trxId, sc.value(), body)));
                })
                .doOnError(e -> log.warn("[Publish] fail trxId={}", trxId, e))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .then();
    }

    private boolean isRetryable(Throwable t) {
        // 5xx 계열(우리가 명시한 RetryablePublishException) + 네트워크/타임아웃만 재시도
        return t instanceof RetryablePublishException
                || t instanceof TimeoutException
                || t instanceof IOException;
    }

    public static class RetryablePublishException extends RuntimeException {
        public RetryablePublishException(String trxId, int status, String body) {
            super("Retryable publish failure trxId=" + trxId + " status=" + status + " body=" + body);
        }
    }

    public static class NonRetryablePublishException extends RuntimeException {
        public NonRetryablePublishException(String trxId, int status, String body) {
            super("Non-retryable publish failure trxId=" + trxId + " status=" + status + " body=" + body);
        }
    }
}