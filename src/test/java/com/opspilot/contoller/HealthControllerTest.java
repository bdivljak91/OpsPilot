package com.opspilot.contoller;

import com.opspilot.HealthController;
import com.opspilot.dto.response.HealthResponse;
import com.opspilot.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HealthService healthService;

    @Test
    void shouldReturnApplicationHealthStatus() throws Exception {
        Instant timestamp = Instant.parse("2026-07-18T08:00:00Z");

        HealthResponse response = new HealthResponse(
                "UP",
                timestamp
        );

        when(healthService.getHealth()).thenReturn(response);

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp")
                        .value("2026-07-18T08:00:00Z"));

        verify(healthService).getHealth();
    }
}