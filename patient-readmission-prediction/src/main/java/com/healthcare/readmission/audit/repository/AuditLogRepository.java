package com.healthcare.readmission.audit.repository;

import com.healthcare.readmission.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Find logs by username
    List<AuditLog> findByUsername(String username);

    // Find logs by user ID
    List<AuditLog> findByUserId(Long userId);

    // Find logs by action type
    List<AuditLog> findByAction(String action);

    // Find logs by endpoint
    List<AuditLog> findByEndpoint(String endpoint);

    // Find logs within date range
    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find logs by user and date range
    List<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // Find failed requests (status >= 400)
    List<AuditLog> findByStatusCodeGreaterThanEqual(Integer statusCode);

    // Get recent logs (limit 50)
    List<AuditLog> findTop50ByOrderByCreatedAtDesc();

    // Count logs by action
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a GROUP BY a.action")
    List<Object[]> countByAction();

    // Count logs by user
    long countByUserId(Long userId);
}
