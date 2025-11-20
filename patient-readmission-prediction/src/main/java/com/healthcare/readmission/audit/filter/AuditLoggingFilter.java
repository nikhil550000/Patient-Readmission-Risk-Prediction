package com.healthcare.readmission.audit.filter;

import com.healthcare.readmission.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to log all incoming HTTP requests and outgoing responses
 */
@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

    @Autowired
    private AuditService auditService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Capture request details
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String params = queryString != null ? queryString : "";

        // Get username from request (if authenticated)
        String username = extractUsername(request);

        // Log request
        long startTime = System.currentTimeMillis();
        auditService.logRequest(username, method, uri, params);

        // Continue with the request
        filterChain.doFilter(request, response);

        // Log response
        long duration = System.currentTimeMillis() - startTime;
        int statusCode = response.getStatus();
        auditService.logResponse(username, method, uri, statusCode, duration);
    }

    /**
     * Extract username from request (JWT token or session)
     */
    private String extractUsername(HttpServletRequest request) {
        // Try to get from JWT token in Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // TODO: Decode JWT and extract username
            // For now, return null (anonymous)
            return null;
        }

        // Could also check session or other auth mechanisms
        return null;
    }
}
