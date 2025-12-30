# HDT Event Server

Hana Dream Town Digital Twin Platform - Event Ingestion & Streaming Server.

- 역할: 센서/장비/AI 이벤트 실시간 수집, 변환, 저장, 디지털트윈 Core로 전달
- 기술 스택:
    - Java 21
    - Spring Boot 3.x
    - Spring WebFlux
    - Gradle
    - R2DBC / JPA (서버별로 선택)
    - Redis / Kafka (이벤트 서버인 경우)
    - 기타: Prometheus, Grafana, ELK 등 (모니터링/로그)

---

## 1. Overview

HDT Event Server는 현장 설비/AI 서버에서 발생하는 이벤트를 비동기/논블로킹 방식으로 수집하는 서버입니다.  
수집된 이벤트는 저장소(DB/Redis/Kafka)에 적재되며, DigitalTwin Server로 전달되어 디지털트윈 상태 업데이트에 사용됩니다.

---

## 2. Architecture

- 상위 시스템: HDT Digital Twin Platform
- 이 서버의 위치:
    - Upstream:
          - Field Devices / Edge Gateway
          - AI Detection Server
    - Downstream:
          - hdt-digitaltwin-server
          - DB (이벤트 이력 저장)
          - Redis/Kafka (실시간 스트리밍)

간단 흐름:
1. `/events/**` WebFlux 엔드포인트로 이벤트 수신
2. 이벤트 Validate & Normalization
3. 비동기 저장 (DB/R2DBC 등)
4. DigitalTwin Server로 이벤트 전달 (REST/Message)

---

## 3. Port & Profile

- 기본 Port: `:8081`
- Spring Profile
    - `local`
    - `dev`
    - `prod`

```bash
# 로컬 실행 예시
./gradlew bootRun --args='--spring.profiles.active=local'
