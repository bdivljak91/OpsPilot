package com.opspilot.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllValidationErrors() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "description": ""
                }
                """;

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "name: Name must not be blank",
                        "description: Description must not be blank"
                )))
                .andExpect(jsonPath("$.path").value("/test/validation"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnNotFoundForResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Project not found"))
                .andExpect(jsonPath("$.path").value("/test/not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Invalid argument"))
                .andExpect(jsonPath("$.path").value("/test/illegal-argument"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedException() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0]")
                        .value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/test/unexpected"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/validation")
        void validateRequest(
                @Valid @RequestBody TestRequest request
        ) {}

        @GetMapping("/not-found")
        void throwResourceNotFoundException() {
            throw new ResourceNotFoundException("Project not found");
        }

        @GetMapping("/illegal-argument")
        void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @GetMapping("/unexpected")
        void throwUnexpectedException() {
            throw new RuntimeException("Database unavailable");
        }
    }

    record TestRequest(

            @NotBlank(message = "Name must not be blank")
            String name,

            @NotBlank(message = "Description must not be blank")
            String description

    ) {
    }
}