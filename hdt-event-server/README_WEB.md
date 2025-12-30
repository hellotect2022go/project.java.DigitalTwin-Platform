# HDT Web Server

Hana Dream Town Digital Twin Platform - Web/BFF Server.

- 역할: Web 대시보드/운영자 화면을 위한 API 제공(BFF), 인증 처리, 화면 단위 조회 API 구성
- 기술 스택:
    - Java 21
    - Spring Boot 3.x
    - Spring WebFlux
    - Gradle

---

## 1. Overview

HDT Web Server는 React/Vue 기반의 Web Dashboard를 위한 백엔드입니다.  
화면 단위로 Aggregation된 API를 제공하여, Gateway 뒤에서 BFF 역할을 수행합니다.

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