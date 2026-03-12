package com.mpole.hdt.digitaltwin.infrastructure.initializer;

import com.mpole.hdt.digitaltwin.application.repository.device.Device;
import com.mpole.hdt.digitaltwin.application.repository.device.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceInitializer implements CommandLineRunner {
    private final DeviceRepository deviceRepository; // JPA나 MyBatis
    private final DeviceCache deviceCache;

    @Override
    public void run(String... args) throws Exception {
        log.info("DeviceInitializer");

        List<Device> allDevices = deviceRepository.fetchDeviceWithPoints();
        deviceCache.initCache(allDevices);

        System.out.println("총 " + allDevices.size() + "개의 장비 정보 로드 완료.");
    }
}
