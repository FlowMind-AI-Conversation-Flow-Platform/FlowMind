package com.flowmind.health;

import com.flowmind.config.FlowMindRuntimeProperties;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final FlowMindRuntimeProperties runtimeProperties;

    public HealthController(FlowMindRuntimeProperties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> readHealth() {
        Map<String, Object> payload = Map.of(
                "status", "UP",
                "service", runtimeProperties.applicationName(),
                "timestamp", Instant.now().toString(),
                "storage", Map.of(
                        "databaseEnabled", runtimeProperties.storage().databaseEnabled(),
                        "flywayEnabled", runtimeProperties.storage().flywayEnabled()
                ),
                "integration", Map.of(
                        "redisEnabled", runtimeProperties.integration().redisEnabled(),
                        "openaiEnabled", runtimeProperties.integration().openaiEnabled()
                )
        );

        return ResponseEntity.ok(payload);
    }
}

