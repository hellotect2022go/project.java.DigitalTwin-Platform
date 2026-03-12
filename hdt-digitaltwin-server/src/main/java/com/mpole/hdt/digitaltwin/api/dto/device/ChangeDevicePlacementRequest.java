package com.mpole.hdt.digitaltwin.api.dto.device;

public record ChangeDevicePlacementRequest(
        Long deviceId,
        Float posX,
        Float posY,
        Float posZ,
        Float rotX,
        Float rotY,
        Float rotZ,
        Float scaleX,
        Float scaleY,
        Float scaleZ,
        Long locBuildingId,
        Long locFloorId,
        Long locZoneId,
        Long locZoneDetailId
) {
}
