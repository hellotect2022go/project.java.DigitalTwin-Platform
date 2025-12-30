# HDT DigitalTwin Server

Hana Dream Town Digital Twin Platform - Digital Twin Core Server.

- 역할: 자산/장비/공간 모델 관리, 실시간 상태 계산, 디지털트윈 뷰 제공
- 기술 스택:
    - Java 21
    - Spring Boot 3.x
    - Spring WebFlux
    - Gradle

---

## 1. Overview

HDT DigitalTwin Server는 건물/설비/공간에 대한 디지털트윈 도메인을 관리하는 코어 서버입니다.  
장비 메타정보, 상태, 이벤트 히스토리, 알람 로직 등을 관리하고, Web Server 및 외부 시스템에 도메인 API를 제공합니다.

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
1. 

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
