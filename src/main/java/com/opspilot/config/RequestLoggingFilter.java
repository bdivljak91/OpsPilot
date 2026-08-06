package com.opspilot.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Logs the outcome and execution time of each HTTP API request.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final Set<String> STATIC_RESOURCE_PATHS = Set.of(
            "/css/",
            "/js/",
            "/images/",
            "/webjars/"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            LOGGER.info(
                    "HTTP request completed: method={}, uri={}, status={}, executionTimeMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime
            );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.equals("/favicon.ico")
                || STATIC_RESOURCE_PATHS.stream().anyMatch(requestUri::startsWith);
    }
}
