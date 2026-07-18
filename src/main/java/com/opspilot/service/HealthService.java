package com.opspilot.service;

import com.opspilot.dto.response.HealthResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthService {

    public HealthResponse getHealth() {
        return new HealthResponse(
                "UP",
                Instant.now()
        );
    }
}
