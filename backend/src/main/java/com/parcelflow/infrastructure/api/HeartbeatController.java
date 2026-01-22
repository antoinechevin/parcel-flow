package com.parcelflow.infrastructure.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HeartbeatController {

    @GetMapping("/heartbeat")
    public Map<String, String> heartbeat() {
        return Map.of("status", "UP", "message", "Parcel-Flow Backend is running");
    }
}
