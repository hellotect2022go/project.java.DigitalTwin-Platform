package com.mpole.hdt.digitaltwin.api.dto.device;

import com.mpole.hdt.digitaltwin.application.repository.device.DeviceTransform;

public record DeviceTransformDTO(
        Long transformId,
        // Position
        Float posX,
        Float posY,
        Float posZ,
        // Rotation
         Float rotX,
         Float rotY,
         Float rotZ,
        // Scale
         Float scaleX,
         Float scaleY,
         Float scaleZ
)
{
    public static DeviceTransformDTO from(DeviceTransform transform){
        if (transform == null) return null;
        return new DeviceTransformDTO(
                transform.getTransformId(),
                transform.getPosX(),transform.getPosY(),transform.getPosZ(),
                transform.getRotX(),transform.getRotY(),transform.getRotZ(),
                transform.getScaleX(),transform.getScaleY(),transform.getScaleZ()
        );
    }
}
