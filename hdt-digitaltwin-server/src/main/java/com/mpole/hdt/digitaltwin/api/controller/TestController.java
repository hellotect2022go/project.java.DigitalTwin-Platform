package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.persistence.device.Device;
import com.mpole.hdt.digitaltwin.persistence.device.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final DeviceRepository deviceRepository;




    @GetMapping
    public ResponseEntity<ApiResponse> test() {

        List<Device> list = deviceRepository.fetchDeviceWithPoints();





        return ResponseEntity.ok(ApiResponse.success("성공",null));
    }
}
