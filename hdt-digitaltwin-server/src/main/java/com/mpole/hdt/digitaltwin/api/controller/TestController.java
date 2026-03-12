package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginRequest;
import com.mpole.hdt.digitaltwin.api.dto.auth.LoginResponse;
import com.mpole.hdt.digitaltwin.application.repository.device.Device;
import com.mpole.hdt.digitaltwin.application.repository.device.DevicePoint;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
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
