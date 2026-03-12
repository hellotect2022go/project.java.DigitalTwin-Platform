package com.mpole.hdt.digitaltwin.api.dto.device;

import com.mpole.hdt.digitaltwin.persistence.device.Device;

public record DeviceLocationDTO(
        Long buildingId, String buildingName,
        Long floorId, String floorName,
        Long zoneId, String zoneName,
        Long zoneDetailId, String zoneDetailName
)
{
    public static DeviceLocationDTO from(Device device){
        return new DeviceLocationDTO(
                device.getLocBuilding() != null ? device.getLocBuilding().getBuildingId() : null,
                device.getLocBuilding() != null ? device.getLocBuilding().getName() : null,
                device.getLocFloor() != null ? device.getLocFloor().getFloorId() : null,
                device.getLocFloor() != null ? device.getLocFloor().getName() : null,
                device.getLocZone() != null ? device.getLocZone().getZoneId() : null,
                device.getLocZone() != null ? device.getLocZone().getName() : null,
                device.getLocZoneDetail() != null ? device.getLocZoneDetail().getZoneDetailId() : null,
                device.getLocZoneDetail() != null ? device.getLocZoneDetail().getName() : null
        );
    }
}
