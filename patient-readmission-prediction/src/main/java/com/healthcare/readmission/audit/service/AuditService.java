package com.healthcare.readmission.audit.service;

import com.healthcare.readmission.audit.entity.AuditLog;
import com.healthcare.readmission.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for audit logging - SAVES TO DATABASE
 */
@Service
public class AuditService {

        @Autowired
        private AuditLogRepository auditLogRepository;

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * Log an audit event - SAVES TO DATABASE
         */
        public void logAudit(String username, String action, String endpoint,
                        String method, int statusCode, String details) {

                String timestamp = LocalDateTime.now().format(formatter);

                // Format log message for console
                String logMessage = String.format(
                                "[AUDIT] %s | User: %s | Action: %s | Endpoint: %s %s | Status: %d | Details: %s",
                                timestamp, username, action, method, endpoint, statusCode, details);

                // Log to console
                System.out.println(logMessage);

                // SAVE TO DATABASE
                try {
                        AuditLog auditLog = new AuditLog();
                        auditLog.setUsername(username);
                        auditLog.setAction(action);
                        auditLog.setEndpoint(endpoint);
                        auditLog.setHttpMethod(method);
                        auditLog.setStatusCode(statusCode);
                        auditLog.setDetails(details);
                        auditLog.setCreatedAt(LocalDateTime.now());

                        auditLogRepository.save(auditLog);
                } catch (Exception e) {
                        System.err.println("[AUDIT ERROR] Failed to save audit log: " + e.getMessage());
                        e.printStackTrace();
                }
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

                // Save to database
                try {
                        AuditLog auditLog = new AuditLog();
                        auditLog.setUsername(username != null ? username : "anonymous");
                        auditLog.setAction("API_REQUEST");
                        auditLog.setEndpoint(uri);
                        auditLog.setHttpMethod(method);
                        auditLog.setStatusCode(0); // Request doesn't have status yet
                        auditLog.setDetails("Params: " + params);
                        auditLog.setCreatedAt(LocalDateTime.now());

                        auditLogRepository.save(auditLog);
                } catch (Exception e) {
                        System.err.println("[AUDIT ERROR] Failed to save request log: " + e.getMessage());
                }
        }

        /**
         * Log API response
         */
        public void logResponse(String username, String method, String uri, int statusCode, long durationMs) {
                String timestamp = LocalDateTime.now().format(formatter);
                String logMessage = String.format(
                                "[RESPONSE] %s | User: %s | %s %s | Status: %d | Duration: %dms",
                                timestamp, username != null ? username : "anonymous", method, uri, statusCode,
                                durationMs);
                System.out.println(logMessage);

                // Save to database
                try {
                        AuditLog auditLog = new AuditLog();
                        auditLog.setUsername(username != null ? username : "anonymous");
                        auditLog.setAction("API_RESPONSE");
                        auditLog.setEndpoint(uri);
                        auditLog.setHttpMethod(method);
                        auditLog.setStatusCode(statusCode);
                        auditLog.setDurationMs(durationMs);
                        auditLog.setDetails("Duration: " + durationMs + "ms");
                        auditLog.setCreatedAt(LocalDateTime.now());

                        auditLogRepository.save(auditLog);
                } catch (Exception e) {
                        System.err.println("[AUDIT ERROR] Failed to save response log: " + e.getMessage());
                }
        }

        /**
         * Log prediction request
         */
        public void logPrediction(String username, Integer patientId, boolean prediction,
                        double probability) {
                String details = String.format("PatientID: %d, Predicted: %s, Probability: %.4f",
                                patientId, prediction ? "READMIT" : "NO_READMIT", probability);

                logAudit(
                                username != null ? username : "anonymous",
                                "PREDICTION",
                                "/api/v1/predictions/predict",
                                "POST",
                                200,
                                details);
        }

        /**
         * Get all audit logs (for admin viewing)
         */
        public List<AuditLog> getAllAuditLogs() {
                return auditLogRepository.findAll();
        }

        /**
         * Get recent audit logs (top 50)
         */
        public List<AuditLog> getRecentAuditLogs() {
                return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
        }

        /**
         * Get audit logs by username
         */
        public List<AuditLog> getAuditLogsByUsername(String username) {
                return auditLogRepository.findByUsername(username);
        }

        /**
         * Get audit logs by action
         */
        public List<AuditLog> getAuditLogsByAction(String action) {
                return auditLogRepository.findByAction(action);
        }

        /**
         * Get failed requests (status >= 400)
         */
        public List<AuditLog> getFailedRequests() {
                return auditLogRepository.findByStatusCodeGreaterThanEqual(400);
        }
}
