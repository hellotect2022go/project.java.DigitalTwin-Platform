package com.mpole.hdt.gateway.ingestion.publisher;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEnvelope;
import reactor.core.publisher.Mono;

public interface EventPublisher<T> {
    /**
     * 표준 이벤트를 다운스트림(Event Server 또는 Kafka)으로 발행
     * 구현체 교체만으로 HTTP -> Kafka 전환 가능
     */
    Mono<Void> publish(StandardEnvelope<T> envelope);
}
