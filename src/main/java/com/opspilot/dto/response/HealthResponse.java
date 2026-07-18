package com.opspilot.dto.response;

import java.time.Instant;

public record HealthResponse(
        String status,
        Instant timestamp
) {
}
