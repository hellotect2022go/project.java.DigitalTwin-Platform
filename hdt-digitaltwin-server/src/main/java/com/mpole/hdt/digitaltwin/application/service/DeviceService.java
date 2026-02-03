package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.device.*;
import com.mpole.hdt.digitaltwin.application.repository.DevicePlacementRepository;
import com.mpole.hdt.digitaltwin.application.repository.DeviceRepository;
import com.mpole.hdt.digitaltwin.application.repository.DeviceModelRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.Device;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceModel;
import com.mpole.hdt.digitaltwin.application.repository.entity.DevicePlacement;
import com.mpole.hdt.digitaltwin.application.repository.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final DevicePlacementRepository placementRepository;
    private final DeviceModelRepository deviceModelRepository;
    
    /**
     * 전체 장비 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 활성화된 장비만 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> getEnabledDevices() {
        return deviceRepository.findByEnabledTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ID로 장비 조회
     */
    @Transactional(readOnly = true)
    public DeviceDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다: " + id));
        return toDto(device);
    }
    
    /**
     * Device ID로 장비 조회
     */
    @Transactional(readOnly = true)
    public DeviceDTO getDeviceByDeviceId(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다: " + deviceId));
        return toDto(device);
    }
    
    /**
     * DeviceModel별 장비 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> getDevicesByModel(Long deviceModelId) {
        return deviceRepository.findByDeviceModel_Id(deviceModelId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 층별 장비 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> getDevicesByFloor(String floor) {
        return deviceRepository.findByFloorAndEnabledTrue(floor).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 상태별 장비 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> getDevicesByStatus(String status) {
        return deviceRepository.findByStatus(status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 검색
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> searchDevices(String keyword) {
        return deviceRepository.searchByKeyword(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 장비 생성
     */
    @Transactional
    public DeviceDTO createDevice(DeviceRequest request) {
        // Device ID 중복 체크
        if (deviceRepository.existsByDeviceId(request.getDeviceId())) {
            throw new IllegalArgumentException("이미 존재하는 Device ID입니다: " + request.getDeviceId());
        }
        
        // DeviceModel 조회
        DeviceModel deviceModel = deviceModelRepository.findById(request.getDeviceModelId())
                .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + request.getDeviceModelId()));
        
        // Device 생성
        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName())
                .deviceModel(deviceModel)
                .status(request.getStatus() != null ? request.getStatus() : "STANDBY")
                .online(request.getOnline() != null ? request.getOnline() : false)
                .location(request.getLocation())
                .floor(request.getFloor())
                .zone(request.getZone())
                .currentValue(request.getCurrentValue())
                .unit(request.getUnit())
                .installationDate(request.getInstallationDate())
                .manufactureDate(request.getManufactureDate())
                .serialNumber(request.getSerialNumber())
                .description(request.getDescription())
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        
        device = deviceRepository.save(device);
        log.info("장비 생성: {} ({})", device.getDeviceName(), device.getDeviceId());
        
        return toDto(device);
    }
    
    /**
     * 장비 수정
     */
    @Transactional
    public DeviceDTO updateDevice(Long id, DeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다: " + id));
        
        // Device ID 변경 시 중복 체크
        if (!device.getDeviceId().equals(request.getDeviceId())) {
            if (deviceRepository.existsByDeviceId(request.getDeviceId())) {
                throw new IllegalArgumentException("이미 존재하는 Device ID입니다: " + request.getDeviceId());
            }
            device.setDeviceId(request.getDeviceId());
        }
        
        // DeviceModel 변경 시
        if (!device.getDeviceModel().getId().equals(request.getDeviceModelId())) {
            DeviceModel deviceModel = deviceModelRepository.findById(request.getDeviceModelId())
                    .orElseThrow(() -> new IllegalArgumentException("장비 모델을 찾을 수 없습니다: " + request.getDeviceModelId()));
            device.setDeviceModel(deviceModel);
        }
        
        device.setDeviceName(request.getDeviceName());
        device.setStatus(request.getStatus());
        device.setOnline(request.getOnline());
        device.setLocation(request.getLocation());
        device.setFloor(request.getFloor());
        device.setZone(request.getZone());
        device.setCurrentValue(request.getCurrentValue());
        device.setUnit(request.getUnit());
        device.setInstallationDate(request.getInstallationDate());
        device.setManufactureDate(request.getManufactureDate());
        device.setSerialNumber(request.getSerialNumber());
        device.setDescription(request.getDescription());
        device.setEnabled(request.getEnabled());
        
        device = deviceRepository.save(device);
        log.info("장비 수정: {} ({})", device.getDeviceName(), device.getDeviceId());
        
        return toDto(device);
    }
    
    /**
     * 장비 삭제
     */
    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다: " + id));
        
        // Placement도 함께 삭제
        if (placementRepository.existsByDevice_Id(id)) {
            placementRepository.deleteByDevice_Id(id);
            log.info("장비 배치 정보 삭제: deviceId={}", id);
        }
        
        deviceRepository.delete(device);
        log.info("장비 삭제: {} ({})", device.getDeviceName(), device.getDeviceId());
    }
    
    /**
     * 장비 배치 정보 조회
     */
    @Transactional(readOnly = true)
    public DevicePlacementDTO getPlacementByDeviceId(Long deviceId) {
        DevicePlacement placement = placementRepository.findByDevice_Id(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("배치 정보를 찾을 수 없습니다: deviceId=" + deviceId));
        return toPlacementDto(placement);
    }
    
    /**
     * 장비 배치 정보 생성/수정
     */
    @Transactional
    public DevicePlacementDTO savePlacement(DevicePlacementRequest request) {
        // Device 조회
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다: " + request.getDeviceId()));
        
        // 기존 Placement 조회 또는 새로 생성
        DevicePlacement placement = placementRepository.findByDevice_Id(request.getDeviceId())
                .orElse(DevicePlacement.builder()
                        .device(device)
                        .build());
        
        // 위치 정보 업데이트
        placement.setPositionX(request.getPositionX() != null ? request.getPositionX() : 0.0f);
        placement.setPositionY(request.getPositionY() != null ? request.getPositionY() : 0.0f);
        placement.setPositionZ(request.getPositionZ() != null ? request.getPositionZ() : 0.0f);
        
        placement.setRotationX(request.getRotationX() != null ? request.getRotationX() : 0.0f);
        placement.setRotationY(request.getRotationY() != null ? request.getRotationY() : 0.0f);
        placement.setRotationZ(request.getRotationZ() != null ? request.getRotationZ() : 0.0f);
        
        placement.setScaleX(request.getScaleX() != null ? request.getScaleX() : 1.0f);
        placement.setScaleY(request.getScaleY() != null ? request.getScaleY() : 1.0f);
        placement.setScaleZ(request.getScaleZ() != null ? request.getScaleZ() : 1.0f);
        
        placement.setFloorLevel(request.getFloorLevel());
        placement.setLayerName(request.getLayerName());
        placement.setParentObject(request.getParentObject());
        placement.setGameObjectName(request.getGameObjectName());
        placement.setCustomAttributes(request.getCustomAttributes());
        placement.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        
        placement = placementRepository.save(placement);
        log.info("장비 배치 정보 저장: deviceId={}", request.getDeviceId());
        
        return toPlacementDto(placement);
    }

    /**
     * Category Id 로 Device 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceByCategoryDTO> getDevicesByCategoryId(Long categoryId) {
        List<Device> devices = deviceRepository.findDevicesByCategoryId(categoryId);

        List<DeviceByCategoryDTO> deviceDTOS = devices.stream().map(device->{
            DeviceByCategoryDTO dto = DeviceByCategoryDTO.builder()
                    .deviceId(device.getDeviceId())
                    .deviceName(device.getDeviceName())
                    .build();
            // DeviceModel 정보
            DeviceModel model = device.getDeviceModel();
            dto.setDeviceModelId(model.getId());
            dto.setModelAssetCode(model.getAssetCode());
            dto.setModelAssetNameKo(model.getAssetNameKo());
            dto.setCategoryId(model.getCategory().getId());
            dto.setCategoryCode(model.getCategory().getCategoryCode());
            dto.setCategoryNameKo(model.getCategory().getCategoryNameKo());
            dto.setCategoryPath(buildCategoryPath(model.getCategory()));

            dto.setSysCode(model.getSystemType().getSysCode());
            dto.setSysNameKo(model.getSystemType().getSysNameKo());

            // Asset3DModel 정보
            if (model.getAsset3DModel() != null) {
                dto.setAsset3DModelName(model.getAsset3DModel().getModelName());
            }

            dto.setSet((device.getPlacement() != null));



            return dto;

        }).toList();


        return deviceDTOS;
    }
    
    /**
     * Entity -> DTO 변환
     */
    private DeviceDTO toDto(Device device) {
        DeviceDTO dto = DeviceDTO.builder()
                .id(device.getId())
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .status(device.getStatus())
                .online(device.getOnline())
//                .location(device.getLocation())
//                .floor(device.getFloor())
//                .zone(device.getZone())
                .currentValue(device.getCurrentValue())
                .unit(device.getUnit())
                .lastCommunication(device.getLastCommunication())
                .installationDate(device.getInstallationDate())
                .manufactureDate(device.getManufactureDate())
                .serialNumber(device.getSerialNumber())
                .description(device.getDescription())
                .enabled(device.getEnabled())
                .createdBy(device.getCreatedBy())
                .updatedBy(device.getUpdatedBy())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
        
        // DeviceModel 정보
        DeviceModel model = device.getDeviceModel();
        dto.setDeviceModelId(model.getId());
        dto.setModelAssetCode(model.getAssetCode());
        dto.setModelAssetNameKo(model.getAssetNameKo());
        dto.setModelAssetNameEn(model.getAssetNameEn());
        
        // Category 정보
        dto.setCategoryId(model.getCategory().getId());
        dto.setCategoryCode(model.getCategory().getCategoryCode());
        dto.setCategoryNameKo(model.getCategory().getCategoryNameKo());
        dto.setCategoryPath(buildCategoryPath(model.getCategory()));
        
        // SystemType 정보
        dto.setSystemTypeId(model.getSystemType().getId());
        dto.setSysCode(model.getSystemType().getSysCode());
        dto.setSysNameKo(model.getSystemType().getSysNameKo());
        
        // Asset3DModel 정보
        if (model.getAsset3DModel() != null) {
            dto.setAsset3DModelId(model.getAsset3DModel().getId());
            dto.setAsset3DModelName(model.getAsset3DModel().getModelName());
            dto.setThumbnailUrl(model.getAsset3DModel().getThumbnailUrl());
        }
        
        // Placement 정보 (있는 경우)
        placementRepository.findByDevice_Id(device.getId())
                .ifPresent(placement -> dto.setPlacement(toPlacementDto(placement)));
        
        return dto;
    }
    
    /**
     * DevicePlacement Entity -> DTO 변환
     */
    private DevicePlacementDTO toPlacementDto(DevicePlacement placement) {
        return DevicePlacementDTO.builder()
                .id(placement.getId())
                .deviceId(placement.getDevice().getId())
                .positionX(placement.getPositionX())
                .positionY(placement.getPositionY())
                .positionZ(placement.getPositionZ())
                .rotationX(placement.getRotationX())
                .rotationY(placement.getRotationY())
                .rotationZ(placement.getRotationZ())
                .scaleX(placement.getScaleX())
                .scaleY(placement.getScaleY())
                .scaleZ(placement.getScaleZ())
                .floorLevel(placement.getFloorLevel())
                .layerName(placement.getLayerName())
                .parentObject(placement.getParentObject())
                .gameObjectName(placement.getGameObjectName())
                .customAttributes(placement.getCustomAttributes())
                .enabled(placement.getEnabled())
                .createdBy(placement.getCreatedBy())
                .updatedBy(placement.getUpdatedBy())
                .build();
    }
    
    /**
     * 카테고리 전체 경로 생성
     */
    private String buildCategoryPath(Category category) {
        List<String> path = new ArrayList<>();
        Category current = category;
        
        while (current != null) {
            path.add(0, current.getCategoryNameKo());
            current = current.getParent();
        }
        
        return String.join(" > ", path);
    }
}

