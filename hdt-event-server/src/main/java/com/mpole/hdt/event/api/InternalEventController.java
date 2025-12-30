package com.mpole.hdt.event.api;

import com.mpole.hdt.event.api.dto.StandardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalEventController {
    private static final Logger log = LoggerFactory.getLogger(InternalEventController.class);

    @PostMapping("/internal/events")
    public void receive(@RequestBody StandardEvent event) {
        log.info("EVENT RECEIVED - source={}, type={}, deviceId={}, ts={}",
                event.getSource(),
                event.getType(),
                event.getDeviceId(),
                event.getTimestampMs()
        );
    }
}
