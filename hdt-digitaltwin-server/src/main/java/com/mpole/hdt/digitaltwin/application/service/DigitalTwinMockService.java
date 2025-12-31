package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.DigitalTwinDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 디지털 트윈 Mock 데이터 서비스
 * 실제 DB 연동 전까지 사용할 임시 데이터 제공
 */
@Slf4j
@Service
public class DigitalTwinMockService {

    // Mock 데이터 저장소 (메모리)
    private final Map<String, DigitalTwinDataDto> mockDataStore = new ConcurrentHashMap<>();
    
    // 센서값 변동을 위한 Random
    private final Random random = new Random();

    public DigitalTwinMockService() {
        initializeMockData();
    }

    /**
     * 초기 Mock 데이터 생성
     */
    private void initializeMockData() {
        log.info("===== Mock 데이터 초기화 시작 =====");
        
        // 하나드림타운 샘플 자산 데이터 생성
        createMockAsset("ASSET_001", "하나드림타운 A동", "BUILDING", "A동 1층", "EQ_A001", "냉난방기_A1", "NORMAL");
        createMockAsset("ASSET_002", "하나드림타운 B동", "BUILDING", "B동 2층", "EQ_B001", "환기장치_B1", "NORMAL");
        createMockAsset("ASSET_003", "하나드림타운 C동", "BUILDING", "C동 3층", "EQ_C001", "조명제어_C1", "NORMAL");
        createMockAsset("ASSET_004", "하나드림타운 D동", "BUILDING", "D동 1층", "EQ_D001", "승강기_D1", "WARNING");
        createMockAsset("ASSET_005", "하나드림타운 E동", "BUILDING", "E동 지하", "EQ_E001", "급수펌프_E1", "NORMAL");
        
        log.info("===== Mock 데이터 {} 건 초기화 완료 =====", mockDataStore.size());
    }

    /**
     * Mock 자산 생성
     */
    private void createMockAsset(String assetId, String assetName, String assetType, 
                                  String location, String equipmentId, String equipmentName, 
                                  String status) {
        DigitalTwinDataDto data = DigitalTwinDataDto.builder()
                .assetId(assetId)
                .assetName(assetName)
                .assetType(assetType)
                .location(location)
                .equipmentId(equipmentId)
                .equipmentName(equipmentName)
                .equipmentStatus(status)
                .temperature(20.0 + random.nextDouble() * 10)
                .humidity(40.0 + random.nextDouble() * 20)
                .power(50.0 + random.nextDouble() * 50)
                .voltage(220.0 + random.nextDouble() * 10)
                .current(10.0 + random.nextDouble() * 5)
                .isOperating(true)
                .operatingTime(random.nextInt(1000))
                .efficiency(80.0 + random.nextDouble() * 15)
                .timestamp(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
        
        mockDataStore.put(assetId, data);
    }

    /**
     * 모든 디지털 트윈 데이터 조회
     */
    public List<DigitalTwinDataDto> getAllData() {
        return new ArrayList<>(mockDataStore.values());
    }

    /**
     * 특정 자산 데이터 조회
     */
    public Optional<DigitalTwinDataDto> getDataByAssetId(String assetId) {
        return Optional.ofNullable(mockDataStore.get(assetId));
    }

    /**
     * 자산 유형별 데이터 조회
     */
    public List<DigitalTwinDataDto> getDataByAssetType(String assetType) {
        return mockDataStore.values().stream()
                .filter(data -> data.getAssetType().equals(assetType))
                .collect(Collectors.toList());
    }

    /**
     * 장비 상태별 데이터 조회
     */
    public List<DigitalTwinDataDto> getDataByStatus(String status) {
        return mockDataStore.values().stream()
                .filter(data -> data.getEquipmentStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * 센서값 업데이트 시뮬레이션 (1초 주기로 호출됨)
     * 실제 DB에서는 트리거나 변경 감지로 처리될 부분
     */
    public Map<String, Object> updateSensorValues() {
        Map<String, Object> changes = new HashMap<>();
        
        mockDataStore.values().forEach(data -> {
            Map<String, Object> fieldChanges = new HashMap<>();
            
            // 온도 변경 (±0.5도 랜덤 변경)
            double oldTemp = data.getTemperature();
            double newTemp = oldTemp + (random.nextDouble() - 0.5);
            if (Math.abs(newTemp - oldTemp) > 0.1) {
                data.setTemperature(newTemp);
                fieldChanges.put("temperature", Map.of("old", oldTemp, "new", newTemp));
            }
            
            // 습도 변경 (±1% 랜덤 변경)
            double oldHumidity = data.getHumidity();
            double newHumidity = oldHumidity + (random.nextDouble() - 0.5) * 2;
            if (Math.abs(newHumidity - oldHumidity) > 0.2) {
                data.setHumidity(newHumidity);
                fieldChanges.put("humidity", Map.of("old", oldHumidity, "new", newHumidity));
            }
            
            // 전력 변경 (±5kW 랜덤 변경)
            double oldPower = data.getPower();
            double newPower = oldPower + (random.nextDouble() - 0.5) * 10;
            if (Math.abs(newPower - oldPower) > 1.0) {
                data.setPower(Math.max(0, newPower));
                fieldChanges.put("power", Map.of("old", oldPower, "new", newPower));
            }
            
            // 전압 변경
            double oldVoltage = data.getVoltage();
            double newVoltage = oldVoltage + (random.nextDouble() - 0.5) * 2;
            if (Math.abs(newVoltage - oldVoltage) > 0.3) {
                data.setVoltage(newVoltage);
                fieldChanges.put("voltage", Map.of("old", oldVoltage, "new", newVoltage));
            }
            
            // 전류 변경
            double oldCurrent = data.getCurrent();
            double newCurrent = oldCurrent + (random.nextDouble() - 0.5);
            if (Math.abs(newCurrent - oldCurrent) > 0.1) {
                data.setCurrent(Math.max(0, newCurrent));
                fieldChanges.put("current", Map.of("old", oldCurrent, "new", newCurrent));
            }
            
            // 효율 변경
            double oldEfficiency = data.getEfficiency();
            double newEfficiency = oldEfficiency + (random.nextDouble() - 0.5) * 2;
            newEfficiency = Math.max(0, Math.min(100, newEfficiency));
            if (Math.abs(newEfficiency - oldEfficiency) > 0.3) {
                data.setEfficiency(newEfficiency);
                fieldChanges.put("efficiency", Map.of("old", oldEfficiency, "new", newEfficiency));
            }
            
            // 운영 시간 증가
            data.setOperatingTime(data.getOperatingTime() + 1);
            
            // 마지막 업데이트 시각 갱신
            data.setLastUpdated(LocalDateTime.now());
            
            if (!fieldChanges.isEmpty()) {
                changes.put(data.getAssetId(), fieldChanges);
            }
        });
        
        return changes;
    }

    /**
     * 특정 자산의 상태 변경
     */
    public boolean updateEquipmentStatus(String assetId, String newStatus) {
        DigitalTwinDataDto data = mockDataStore.get(assetId);
        if (data != null) {
            data.setEquipmentStatus(newStatus);
            data.setLastUpdated(LocalDateTime.now());
            return true;
        }
        return false;
    }
}

