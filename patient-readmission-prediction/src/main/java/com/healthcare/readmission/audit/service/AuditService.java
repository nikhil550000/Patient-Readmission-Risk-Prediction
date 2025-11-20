package com.healthcare.readmission.audit.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for audit logging
 */
@Service
public class AuditService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log an audit event
     */
    public void logAudit(String username, String action, String endpoint,
            String method, int statusCode, String details) {

        String timestamp = LocalDateTime.now().format(formatter);

        // Format log message
        String logMessage = String.format(
                "[AUDIT] %s | User: %s | Action: %s | Endpoint: %s %s | Status: %d | Details: %s",
                timestamp, username, action, method, endpoint, statusCode, details);

        // Log to console (in production, save to database or file)
        System.out.println(logMessage);

        // TODO: In production, save to database
        // auditLogRepository.save(new AuditLog(...));
    }

    /**
     * Log API request
     */
    public void logRequest(String username, String method, String uri, String params) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format(
                "[REQUEST] %s | User: %s | %s %s | Params: %s",
                timestamp, username != null ? username : "anonymous", method, uri, params);
        System.out.println(logMessage);
    }

    /**
     * Log API response
     */
    public void logResponse(String username, String method, String uri, int statusCode, long durationMs) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format(
                "[RESPONSE] %s | User: %s | %s %s | Status: %d | Duration: %dms",
                timestamp, username != null ? username : "anonymous", method, uri, statusCode, durationMs);
        System.out.println(logMessage);
    }

    /**
     * Log prediction request
     */
    public void logPrediction(String username, Integer patientId, boolean prediction,
            double probability) {
        logAudit(
                username != null ? username : "anonymous",
                "PREDICTION",
                "/api/v1/predictions/predict",
                "POST",
                200,
                String.format("PatientID: %d, Predicted: %s, Probability: %.4f",
                        patientId, prediction ? "READMIT" : "NO_READMIT", probability));
    }
}
