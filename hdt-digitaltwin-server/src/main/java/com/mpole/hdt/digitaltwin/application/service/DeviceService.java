package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.Location.LocFloorDTO;
import com.mpole.hdt.digitaltwin.api.dto.Location.LocZoneDTO;
import com.mpole.hdt.digitaltwin.api.dto.Location.LocationResponse;
import com.mpole.hdt.digitaltwin.api.dto.device.*;
//import com.mpole.hdt.digitaltwin.api.dto.device.bak.DeviceLocationDTO;
//import com.mpole.hdt.digitaltwin.api.dto.device.bak.DeviceTransformDTO;
import com.mpole.hdt.digitaltwin.application.repository.device.Device;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceCategory;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceRepository;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceTransform;
import com.mpole.hdt.digitaltwin.application.repository.location.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final LocBuildingRepository locBuildingRepository;
    private final LocFloorRepository locFloorRepository;
    private final LocZoneRepository locZoneRepository;
    private final LocZoneDetailRepository locZoneDetailRepository;

    @Transactional(readOnly = true)
    public Long getAllDevicesCnt() {
        return deviceRepository.count();
    }

    // 모든 장치 목록 조회
    @Transactional(readOnly = true)
    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.fetchAllDevices().stream().map(this::toDto).toList();
    }

    // 특정 장치 정보 조회
    @Transactional(readOnly = true)
    public DeviceDTO getTargetDevice(Long deviceId) {
        return deviceRepository.findById(deviceId).map(this::toDto).get();
    }

    // 장치 목록 검색
    @Transactional(readOnly = true)
    public List<DeviceDTO> getSearchDevices(String floor, String zone) {
        return deviceRepository.fetchSearchDevices(floor, zone).stream().map(this::toDto).toList();
    }

    // 특정 카테고리 기반 장비들만 조회
    @Transactional(readOnly = true)
    public List<DeviceDTO> getTargetCategoryDevices(Integer categoryId) {
        return deviceRepository.fetchTargetCategoryDevices(categoryId)
                .stream().map(this::toDto).toList();
    }

    // 시스템의 location 정보 목록 조회
    @Transactional(readOnly = true)
    public List<LocationResponse> getLocationInfoAll() {
        List<LocBuilding> list = locBuildingRepository.findAll();

        return list.stream().map(locBuilding->{
            LocationResponse locationResponse = LocationResponse.builder()
                    .buildingId(locBuilding.getBuildingId())
                    .buildingName(locBuilding.getName())
                    .build();

            List<LocFloorDTO> floorList = new ArrayList<>();

            List<LocFloor> locFloors = locBuilding.getLocFloorList();
            for (LocFloor locFloor : locFloors) {
                LocFloorDTO locFloorDTO = LocFloorDTO.builder()
                        .floorId(locFloor.getFloorId())
                        .floorName(locFloor.getName())
                        .build();

                List<LocZone> locZones = locFloor.getLocZoneList();
                List<LocZoneDTO> zoneList = new ArrayList<>();
                for (LocZone locZone : locZones) {
                    LocZoneDTO locZoneDTO = LocZoneDTO.builder()
                            .zoneId(locZone.getZoneId())
                            .zoneName(locZone.getName())
                            .meshName(locZone.getMeshName())
                            .build();
                    zoneList.add(locZoneDTO);
                }
                locFloorDTO.setZoneList(zoneList);
                floorList.add(locFloorDTO);
            }
            locationResponse.setFloorList(floorList);

            return locationResponse;
        }).toList();
    }

    @Transactional
    public DevicePlacementDTO savePlacementInfo(ChangeDevicePlacementRequest request) {
        Device device = deviceRepository.fetchDeviceById(request.deviceId());
        // 장비 상세 위치
        device.setDescription(request.description());

        if (device.getDeviceTransform() == null) {
            device.setDeviceTransform(new DeviceTransform());
        }
        device.getDeviceTransform().setPosX(request.posX());
        device.getDeviceTransform().setPosY(request.posY());
        device.getDeviceTransform().setPosZ(request.posZ());
        device.getDeviceTransform().setRotX(request.rotX());
        device.getDeviceTransform().setRotY(request.rotY());
        device.getDeviceTransform().setRotZ(request.rotZ());
        device.getDeviceTransform().setScaleX(request.scaleX());
        device.getDeviceTransform().setScaleY(request.scaleY());
        device.getDeviceTransform().setScaleZ(request.scaleZ());

        // location 정보 삽입
        if (request.locBuildingId() != null) {
            device.setLocBuilding(locBuildingRepository.getReferenceById(request.locBuildingId()));        }
        if (request.locFloorId() != null) {
            device.setLocFloor(locFloorRepository.getReferenceById(request.locFloorId()));
        }
        if (request.locZoneId() != null) {
            device.setLocZone(locZoneRepository.getReferenceById(request.locZoneId()));
        }
        if (request.locZoneDetailId() != null) {
            device.setLocZoneDetail(locZoneDetailRepository.getReferenceById(request.locZoneDetailId()));
        }

        return DevicePlacementDTO.from(device);
    }

    @Transactional
    public void deletePlacementInfo(List<Long> deviceIds) {
        List<Device> devices = deviceRepository.findAllById(deviceIds);
        for (Device device : devices) {
            device.removeTransform();
            device.removeLocInfo();
        }
    }


    private DeviceDTO toDto(Device device) {
        DeviceDTO deviceDTO = DeviceDTO.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .description(device.getDescription())
                .active(device.getActive())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();

        // 디바이스 3d asset 관련
        if (device.getDevice3dAsset() != null) {
            deviceDTO.setAssetId(device.getDevice3dAsset().getAssetId());
            deviceDTO.setAssetName(device.getDevice3dAsset().getAssetName());
        }

        // 디바이스 카테코리 관련
        if (device.getDeviceCategory() != null) {
            deviceDTO.setCategoryId(device.getDeviceCategory().getCategoryId());
            deviceDTO.setCategoryName(device.getDeviceCategory().getCategoryName());

            List<String> path = new ArrayList<>();
            DeviceCategory current = device.getDeviceCategory();

            while (current != null) {
                path.add(0, current.getCategoryName());
                current = current.getParent();
            }
            deviceDTO.setCategoryPath(String.join(">",path));
        }

        // 디바이스 transform 관련
        DeviceTransformDTO transform = DeviceTransformDTO.from(device.getDeviceTransform());
        deviceDTO.setTransform(transform);
        deviceDTO.setSet(transform != null);

        // 디바이스 로케이션 관련
        deviceDTO.setLocation(DeviceLocationDTO.from(device));



        return deviceDTO;
    }



}
