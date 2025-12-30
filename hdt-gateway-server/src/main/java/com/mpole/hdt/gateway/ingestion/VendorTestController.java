package com.mpole.hdt.gateway.ingestion;

import com.mpole.hdt.gateway.ingestion.service.VendorPollingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VendorTestController {
    private final VendorPollingService pollingService;

    public VendorTestController(VendorPollingService pollingService) {
        this.pollingService = pollingService;
    }

    @GetMapping("/vendor/test")
    public String test() {
        return pollingService.pollOnce();
    }
}
