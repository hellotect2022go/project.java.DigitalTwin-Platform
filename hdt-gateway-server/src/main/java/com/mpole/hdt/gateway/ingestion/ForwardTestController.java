package com.mpole.hdt.gateway.ingestion;

import com.mpole.hdt.gateway.infrastructure.external.dto.StandardEvent;
import com.mpole.hdt.gateway.ingestion.service.EventForwarder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForwardTestController {
    private final EventForwarder forwarder;

    public ForwardTestController(EventForwarder forwarder) {
        this.forwarder = forwarder;
    }

    @GetMapping("/forward/test")
    public String testForward() {
        StandardEvent e = new StandardEvent(
                "vendor-x",
                "device.data",
                "D-001",
                System.currentTimeMillis(),
                "{\"sample\":true}"
        );

        forwarder.forward(e);
        return "forwarded";
    }
}
