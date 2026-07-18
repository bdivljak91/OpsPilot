package com.opspilot.service;

import com.opspilot.dto.response.HealthResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HealthServiceTest {

    private final HealthService healthService = new HealthService();

    @Test
    void shouldReturnUpStatusAndTimestamp() {
        HealthResponse response = healthService.getHealth();

        assertEquals("UP", response.status());
        assertNotNull(response.timestamp());
    }
}