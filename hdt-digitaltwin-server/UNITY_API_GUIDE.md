# HDT 디지털트윈 서버 - Unity 통신 가이드

## 📋 개요
이 문서는 Unity와 Java 서버 간 통신 검증을 위한 API 가이드입니다.

**서버 정보**
- 포트: `8082`
- 로컬 URL: `http://localhost:8082`
- WebSocket URL: `ws://localhost:8082/stomp`

---

## 🔌 통신 방식

### 1️⃣ REST API (HTTP)
일반적인 HTTP 요청/응답 방식으로 데이터를 조회합니다.

### 2️⃣ WebSocket STOMP
실시간 양방향 통신으로 1초 주기의 센서 데이터를 Push 받습니다.

---

## 🌐 REST API 엔드포인트

### 헬스 체크
```
GET /api/digitaltwin/health
```
**응답 예시:**
```
DigitalTwin Server is running!
```

---

### 전체 데이터 조회
```
GET /api/digitaltwin/data
```

**응답 예시:**
```json
{
  "success": true,
  "message": "데이터 조회 성공",
  "totalCount": 5,
  "data": [
    {
      "assetId": "ASSET_001",
      "assetName": "하나드림타운 A동",
      "assetType": "BUILDING",
      "location": "A동 1층",
      "equipmentId": "EQ_A001",
      "equipmentName": "냉난방기_A1",
      "equipmentStatus": "NORMAL",
      "temperature": 23.5,
      "humidity": 52.3,
      "power": 75.2,
      "voltage": 225.5,
      "current": 12.3,
      "isOperating": true,
      "operatingTime": 345,
      "efficiency": 87.5,
      "timestamp": "2025-12-31T10:30:00",
      "lastUpdated": "2025-12-31T10:30:00"
    }
  ]
}
```

---

### 특정 자산 데이터 조회
```
GET /api/digitaltwin/data/{assetId}
```

**예시:**
```
GET /api/digitaltwin/data/ASSET_001
```

**응답:** 위와 동일한 구조, data 배열에 1개 항목

---

### 자산 유형별 조회
```
GET /api/digitaltwin/data/type/{assetType}
```

**자산 유형:**
- `BUILDING` - 건물
- `EQUIPMENT` - 장비
- `FACILITY` - 시설

**예시:**
```
GET /api/digitaltwin/data/type/BUILDING
```

---

### 장비 상태별 조회
```
GET /api/digitaltwin/data/status/{status}
```

**장비 상태:**
- `NORMAL` - 정상
- `WARNING` - 경고
- `ERROR` - 오류
- `OFFLINE` - 오프라인

**예시:**
```
GET /api/digitaltwin/data/status/WARNING
```

---

### 장비 상태 변경 (테스트용)
```
PUT /api/digitaltwin/equipment/{assetId}/status?status={newStatus}
```

**예시:**
```
PUT /api/digitaltwin/equipment/ASSET_001/status?status=WARNING
```

---

## 🔄 WebSocket STOMP 통신

### 연결 방법

#### 1. WebSocket 엔드포인트
```
ws://localhost:8082/stomp
```

#### 2. Unity에서 연결 코드 예시 (개념)
```csharp
// WebSocket 라이브러리 사용 (예: websocket-sharp)
var ws = new WebSocket("ws://localhost:8082/stomp");
ws.OnOpen += (sender, e) => {
    Debug.Log("WebSocket 연결 성공!");
};
```

---

### 구독 채널 (Subscribe)

서버에서 Unity로 데이터를 Push하는 채널입니다.

#### 📡 `/sub/digitaltwin/all`
**전체 데이터 전송 (10초 주기)**
```json
[
  {
    "assetId": "ASSET_001",
    "assetName": "하나드림타운 A동",
    "temperature": 23.5,
    ...
  }
]
```

---

#### 📡 `/sub/digitaltwin/updates`
**전체 변경사항 전송 (1초 주기, 변경 시에만)**
```json
{
  "ASSET_001": {
    "temperature": {
      "old": 23.5,
      "new": 23.7
    },
    "power": {
      "old": 75.2,
      "new": 76.1
    }
  },
  "ASSET_002": {
    "humidity": {
      "old": 52.3,
      "new": 52.8
    }
  }
}
```

---

#### 📡 `/sub/digitaltwin/{assetId}`
**특정 자산의 변경사항 (1초 주기)**

예시: `/sub/digitaltwin/ASSET_001` 구독

```json
{
  "assetId": "ASSET_001",
  "equipmentId": "EQ_A001",
  "updateType": "SENSOR_UPDATE",
  "fieldName": "temperature",
  "oldValue": 23.5,
  "newValue": 23.7,
  "timestamp": "2025-12-31T10:30:01"
}
```

**updateType 종류:**
- `SENSOR_UPDATE` - 센서 데이터 변경
- `STATUS_CHANGE` - 상태 변경
- `OPERATION_CHANGE` - 운영 정보 변경

---

#### 📡 `/sub/digitaltwin/status`
**장비 상태 변경 알림**
```json
{
  "assetId": "ASSET_001",
  "updateType": "STATUS_CHANGE",
  "fieldName": "equipmentStatus",
  "oldValue": "NORMAL",
  "newValue": "WARNING",
  "timestamp": "2025-12-31T10:30:05"
}
```

---

#### 📡 `/sub/digitaltwin/heartbeat`
**서버 연결 확인 (30초 주기)**
```json
{
  "type": "HEARTBEAT",
  "timestamp": "2025-12-31T10:30:00",
  "serverStatus": "RUNNING",
  "activeDataCount": 5
}
```

---

#### 📡 `/sub/digitaltwin/error`
**에러 메시지**
```json
{
  "error": "NOT_FOUND",
  "assetId": "INVALID_ID",
  "message": "자산을 찾을 수 없습니다"
}
```

---

### 발행 채널 (Publish)

Unity에서 서버로 메시지를 보내는 채널입니다.

#### 📤 `/pub/digitaltwin/ping`
**연결 확인**

**보낼 메시지:**
```json
{
  "message": "ping from Unity",
  "clientId": "unity-client-001"
}
```

**구독할 응답 채널:** `/sub/digitaltwin/pong`
```json
{
  "type": "pong",
  "timestamp": "2025-12-31T10:30:00",
  "receivedMessage": {
    "message": "ping from Unity",
    "clientId": "unity-client-001"
  }
}
```

---

#### 📤 `/pub/digitaltwin/request/{assetId}`
**특정 자산 데이터 요청**

**예시:** `/pub/digitaltwin/request/ASSET_001`

**보낼 메시지:** (빈 메시지 또는 JSON)
```json
{}
```

**구독할 응답 채널:** `/sub/digitaltwin/ASSET_001`

---

#### 📤 `/pub/digitaltwin/request/all`
**전체 데이터 요청**

**보낼 메시지:**
```json
{}
```

**구독할 응답 채널:** `/sub/digitaltwin/all`

---

#### 📤 `/pub/digitaltwin/subscribe/{assetId}`
**특정 자산 구독 시작 (초기 데이터 즉시 전송)**

**예시:** `/pub/digitaltwin/subscribe/ASSET_001`

**보낼 메시지:**
```json
{
  "clientId": "unity-client-001"
}
```

**구독할 채널:** `/sub/digitaltwin/ASSET_001`

---

## 🧪 테스트 시나리오

### 시나리오 1: REST API 테스트
```bash
# 1. 서버 헬스 체크
curl http://localhost:8082/api/digitaltwin/health

# 2. 전체 데이터 조회
curl http://localhost:8082/api/digitaltwin/data

# 3. 특정 자산 조회
curl http://localhost:8082/api/digitaltwin/data/ASSET_001

# 4. 상태 변경
curl -X PUT "http://localhost:8082/api/digitaltwin/equipment/ASSET_001/status?status=WARNING"
```

---

### 시나리오 2: WebSocket 기본 연결
1. `ws://localhost:8082/stomp` 연결
2. `/sub/digitaltwin/heartbeat` 구독
3. 30초마다 heartbeat 수신 확인

---

### 시나리오 3: 실시간 데이터 수신
1. WebSocket 연결
2. `/sub/digitaltwin/all` 구독
3. 10초마다 전체 데이터 수신 확인
4. `/sub/digitaltwin/updates` 구독
5. 1초마다 변경사항 수신 확인

---

### 시나리오 4: 특정 자산 모니터링
1. WebSocket 연결
2. `/pub/digitaltwin/subscribe/ASSET_001` 메시지 전송
3. `/sub/digitaltwin/ASSET_001` 구독
4. 초기 데이터 수신
5. 1초마다 변경사항 수신

---

## 📊 Mock 데이터 목록

현재 서버에 하드코딩된 테스트 데이터:

| Asset ID | Asset Name | Equipment ID | Equipment Name | Status |
|----------|-----------|--------------|----------------|--------|
| ASSET_001 | 하나드림타운 A동 | EQ_A001 | 냉난방기_A1 | NORMAL |
| ASSET_002 | 하나드림타운 B동 | EQ_B001 | 환기장치_B1 | NORMAL |
| ASSET_003 | 하나드림타운 C동 | EQ_C001 | 조명제어_C1 | NORMAL |
| ASSET_004 | 하나드림타운 D동 | EQ_D001 | 승강기_D1 | WARNING |
| ASSET_005 | 하나드림타운 E동 | EQ_E001 | 급수펌프_E1 | NORMAL |

---

## 🔧 센서 데이터 변경 주기

| 항목 | 변경 주기 | 변동 범위 |
|------|---------|----------|
| temperature (온도) | 1초 | ±0.5°C |
| humidity (습도) | 1초 | ±1% |
| power (전력) | 1초 | ±5kW |
| voltage (전압) | 1초 | ±1V |
| current (전류) | 1초 | ±0.5A |
| efficiency (효율) | 1초 | ±1% |
| operatingTime (운영시간) | 1초 | +1분 |

---

## 🚀 서버 실행 방법

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/digitaltwin-0.0.1-SNAPSHOT.jar
```

서버 시작 후 로그에서 다음 메시지 확인:
```
===== Mock 데이터 5 건 초기화 완료 =====
===== WebSocket STOMP 엔드포인트 등록 완료 =====
연결 URL: ws://localhost:8082/stomp
```

---

## ⚠️ 주의사항

1. **Mock 데이터**: 현재는 실제 DB 연동 없이 메모리상의 Mock 데이터를 사용합니다.
2. **데이터 영속성 없음**: 서버 재시작 시 모든 변경사항이 초기화됩니다.
3. **단순 인메모리 브로커**: RabbitMQ 등 외부 메시지 브로커 없이 Spring 내장 SimpleBroker 사용합니다.
4. **보안 없음**: 현재는 인증/인가 없이 모든 요청을 허용합니다.
5. **CORS 전체 허용**: `setAllowedOriginPatterns("*")`로 모든 Origin 허용 중입니다.

---

## 📝 다음 단계 (실 서비스 준비 시)

- [ ] 실제 DB 연동 (차세대 TwinX DB)
- [ ] vwDigitalTwin_01 뷰테이블 매핑
- [ ] 인증/인가 추가 (Spring Security)
- [ ] CORS 설정 강화
- [ ] 로깅 및 모니터링 강화
- [ ] 에러 핸들링 고도화
- [ ] RabbitMQ 등 외부 메시지 브로커 연동
- [ ] 클러스터링 및 스케일아웃 대응

---

## 💬 문의

통신 테스트 중 문제가 발생하면 서버 로그를 확인하세요.
모든 주요 이벤트는 로그로 출력됩니다.

**로그 확인 포인트:**
- `===== WebSocket 연결 성공 =====`
- `===== 채널 구독 =====`
- `===== [N회] 센서 데이터 업데이트 전송 =====`
- `===== 전체 데이터 전송 =====`

---

**문서 작성일:** 2025-12-31  
**서버 버전:** 0.0.1-SNAPSHOT  
**Spring Boot 버전:** 4.0.1  
**Java 버전:** 21

