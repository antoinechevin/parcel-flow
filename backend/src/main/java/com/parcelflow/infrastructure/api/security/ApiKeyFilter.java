package com.parcelflow.infrastructure.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-KEY";
    
    // For testing purposes only
    public static String TEST_KEY_OVERRIDE = null;

    @Value("${SECURITY_API_KEY:}")
    private String apiKey;

    @Value("${SECURITY_REQUIRE_HTTPS:false}")
    private boolean requireHttps;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // HTTPS check (X-Forwarded-Proto is standard for cloud proxies like Railway)
        if (requireHttps && !"https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                {
                  "type": "https://parcelflow.com/probs/insecure",
                  "title": "Insecure Connection",
                  "status": 403,
                  "detail": "HTTPS is required for API access"
                }
                """);
            return;
        }

        // Skip security for healthcheck or non-API paths if any
        if (path.equals("/api/parcels/health") || path.equals("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only protect /api/**
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader(API_KEY_HEADER);
        String effectiveKey = (TEST_KEY_OVERRIDE != null) ? TEST_KEY_OVERRIDE : apiKey;

        if (effectiveKey != null && !effectiveKey.isEmpty() && effectiveKey.equals(requestApiKey)) {
            filterChain.doFilter(request, response);
        } else {
            // Add CORS headers manually for error responses to avoid browser blocking
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                {
                  "type": "https://parcelflow.com/probs/unauthorized",
                  "title": "Unauthorized",
                  "status": 401,
                  "detail": "Invalid or missing API Key"
                }
                """);
        }
    }
}
