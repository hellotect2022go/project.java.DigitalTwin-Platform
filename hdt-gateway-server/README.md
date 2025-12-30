# HDT Gateway Server

Hana Dream Town Digital Twin Platform - HDT Gateway Server

- 역할: 외부/내부 클라이언트 요청을 수신하고, 인증/인가 후 각 내부 서버로 라우팅
- 기술 스택:
    - Java 21
    - Spring Boot 3.x
    - Spring WebFlux
    - Gradle
    - 기타: Prometheus, Grafana, ELK 등 (모니터링/로그)

---

## 1. Overview

HDT Gateway Server는 외부 API(통합 SI, 건물관리앱 API)와 내부 마이크로서비스(Event Server, DigitalTwin Server, Web Server) 사이의 단일 진입점입니다.  
인증/인가, 라우팅, 공통 필터(로깅, 트레이싱)를 담당합니다.

---

## 2. Architecture

- 상위 시스템: HDT Digital Twin Platform
- 이 서버의 위치:
    - Upstream:
        - Web Client (React/Vue/…)
        - 외부 연동 시스템 (필요 시)
    - Downstream:
        - hdt-web-server
        - hdt-event-server
        - hdt-digitaltwin-server

간단 흐름:
1. 클라이언트 → Gateway (`/api/**`)
2. JWT 인증/인가, 공통 필터 처리
3. 라우팅 규칙에 따라 내부 서버로 전달
4. 응답 Aggregation 후 클라이언트로 반환

---

## 3. Port & Profile

- 기본 Port: `:8080`
- Spring Profile
    - `local`
    - `dev`
    - `prod`

