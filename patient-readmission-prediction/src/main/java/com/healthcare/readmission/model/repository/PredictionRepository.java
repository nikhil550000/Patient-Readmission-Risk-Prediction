package com.healthcare.readmission.model.repository;

import com.healthcare.readmission.model.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    // Find predictions by patient
    List<Prediction> findByPatientId(Long patientId);

    // Find predictions by user (who made the prediction)
    List<Prediction> findByUserId(Long userId);

    // Find predictions by subject ID (MIMIC-III)
    List<Prediction> findBySubjectId(Integer subjectId);

    // Find predictions by risk level
    List<Prediction> findByRiskLevel(String riskLevel);

    // Find predictions that predict readmission
    List<Prediction> findByWillReadmit(Boolean willReadmit);

    // Find predictions within date range
    List<Prediction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find predictions by user within date range
    List<Prediction> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // Count predictions by risk level
    @Query("SELECT p.riskLevel, COUNT(p) FROM Prediction p GROUP BY p.riskLevel")
    List<Object[]> countByRiskLevel();

    // Count total predictions by user
    long countByUserId(Long userId);

    // Get latest predictions (limit 10)
    List<Prediction> findTop10ByOrderByCreatedAtDesc();

    // Get recent predictions for a patient
    List<Prediction> findTop5ByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<Prediction> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
