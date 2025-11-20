package com.healthcare.readmission.audit.service;

import com.healthcare.readmission.audit.entity.AuditLog;
import com.healthcare.readmission.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Create audit log entry
     */
    public AuditLog createLog(String username, String action, String endpoint,
            String httpMethod, Integer statusCode, Long durationMs, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setEndpoint(endpoint);
        log.setHttpMethod(httpMethod);
        log.setStatusCode(statusCode);
        log.setDurationMs(durationMs);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());

        return auditLogRepository.save(log);
    }

    /**
     * Get all audit logs
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * Get recent logs (last 50)
     */
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    /**
     * Get logs by username
     */
    public List<AuditLog> getLogsByUsername(String username) {
        return auditLogRepository.findByUsername(username);
    }

    /**
     * Get logs by action type
     */
    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    /**
     * Get logs within date range
     */
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Get failed requests (status >= 400)
     */
    public List<AuditLog> getFailedRequests() {
        return auditLogRepository.findByStatusCodeGreaterThanEqual(400);
    }
}
