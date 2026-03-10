package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.Location.LocFloorDTO;
import com.mpole.hdt.digitaltwin.api.dto.Location.LocZoneDTO;
import com.mpole.hdt.digitaltwin.api.dto.Location.LocationResponse;
import com.mpole.hdt.digitaltwin.api.dto.device.DeviceDTO;
import com.mpole.hdt.digitaltwin.api.dto.device.DeviceLocationDTO;
import com.mpole.hdt.digitaltwin.api.dto.device.DeviceTransformDTO;
import com.mpole.hdt.digitaltwin.application.repository.device.Device;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceCategory;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceRepository;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceTransform;
import com.mpole.hdt.digitaltwin.application.repository.location.LocBuilding;
import com.mpole.hdt.digitaltwin.application.repository.location.LocBuildingRepository;
import com.mpole.hdt.digitaltwin.application.repository.location.LocFloor;
import com.mpole.hdt.digitaltwin.application.repository.location.LocZone;
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

    @Transactional(readOnly = true)
    public Long getAllDevicesCnt() {
        return deviceRepository.count();
    }

    // 모든 장치 목록 조회
    @Transactional(readOnly = true)
    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.fetchAllDevices().stream().map(this::toDto).toList();
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
        if (device.getDeviceTransform() != null) {
            DeviceTransform deviceTransform = device.getDeviceTransform();
            DeviceTransformDTO transform = DeviceTransformDTO.builder()
                    .locationId(deviceTransform.getTransformId())
                    .positionX(deviceTransform.getPosX())
                    .positionY(deviceTransform.getPosY())
                    .positionZ(deviceTransform.getPosZ())
                    .rotationX(deviceTransform.getRotX())
                    .rotationY(deviceTransform.getRotY())
                    .rotationZ(deviceTransform.getRotZ())
                    .scaleX(deviceTransform.getScaleX())
                    .scaleY(deviceTransform.getScaleY())
                    .scaleZ(deviceTransform.getScaleZ())
                    .build();

            deviceDTO.setSet(true);
            deviceDTO.setTransform(transform);
        }

        DeviceLocationDTO deviceLocationDTO = DeviceLocationDTO.builder().build();
        if (device.getLocBuilding() !=null) {
            deviceLocationDTO.setBuildingId(device.getLocBuilding().getBuildingId());
            deviceLocationDTO.setBuildingName(device.getLocBuilding().getName());
        }
        if (device.getLocFloor() !=null) {
            deviceLocationDTO.setFloorId(device.getLocFloor().getFloorId());
            deviceLocationDTO.setFloorName(device.getLocFloor().getName());
        }
        if (device.getLocZone() !=null) {
            deviceLocationDTO.setZoneId(device.getLocZone().getZoneId());
            deviceLocationDTO.setZoneName(device.getLocZone().getName());
        }
        if (device.getLocZoneDetail() !=null) {
            deviceLocationDTO.setZoneDetailId(device.getLocZoneDetail().getZoneDetailId());
            deviceLocationDTO.setZoneDetailName(device.getLocZoneDetail().getName());
        }
        deviceDTO.setLocation(deviceLocationDTO);



        return deviceDTO;
    }



}
