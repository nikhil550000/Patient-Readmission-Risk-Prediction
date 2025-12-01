package com.healthcare.readmission.controller;

import com.healthcare.readmission.auth.jwt.JwtTokenProvider;
import com.healthcare.readmission.dto.PatientData;
import com.healthcare.readmission.dto.PredictionResult;
import com.healthcare.readmission.model.entity.Prediction;
import com.healthcare.readmission.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/predictions")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Make a prediction (ADMIN and DOCTOR only)
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predict(
            @RequestBody PatientData patientData,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authorization required"));
            }

            String token = authHeader.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            // Only ADMIN and DOCTOR can make predictions
            if ("PATIENT".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Patients cannot make predictions. Please contact your doctor."));
            }

            Long userId = 1L; // TODO: Get actual userId from database

            PredictionResult result = predictionService.predictAndSave(patientData, userId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Prediction failed: " + e.getMessage()));
        }
    }

    /**
     * Get prediction history - role-based filtering
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Allow request without auth to get empty list (for testing)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authorization required"));
            }

            String token = authHeader.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            List<Prediction> predictions;

            if ("PATIENT".equals(role)) {
                // Patients only see their own predictions
                Long patientId = extractPatientIdFromUsername(username);
                predictions = predictionService.getPredictionsByPatientId(patientId);
            } else {
                // ADMIN and DOCTOR see all predictions
                predictions = predictionService.getRecentPredictions();
            }

            return ResponseEntity.ok(predictions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve history: " + e.getMessage()));
        }
    }

    /**
     * Get recent predictions (ADMIN and DOCTOR only)
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Allow request without auth (return empty list)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authorization required"));
            }

            String token = authHeader.substring(7);
            String role = jwtTokenProvider.getRoleFromToken(token);

            // Only ADMIN and DOCTOR can view all predictions
            if ("PATIENT".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied. Patients can only view their own predictions."));
            }

            List<Prediction> predictions = predictionService.getRecentPredictions();
            return ResponseEntity.ok(predictions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve predictions: " + e.getMessage()));
        }
    }

    /**
     * Get specific prediction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPredictionById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authorization required"));
            }

            String token = authHeader.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            return predictionService.getPredictionById(id)
                    .map(prediction -> {
                        // Check if patient is requesting their own prediction
                        if ("PATIENT".equals(role)) {
                            Long patientId = extractPatientIdFromUsername(username);
                            if (!prediction.getPatientId().equals(patientId)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body((Object) Map.of("error", "You can only view your own predictions"));
                            }
                        }
                        return ResponseEntity.ok((Object) prediction);
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve prediction: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint (no auth required)
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        boolean ready = predictionService.isReady();
        if (ready) {
            return ResponseEntity.ok("Prediction service is ready");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Prediction service is not ready");
        }
    }

    /**
     * Helper: Extract patient ID from username
     */
    private Long extractPatientIdFromUsername(String username) {
        if (username.startsWith("patient")) {
            try {
                String numStr = username.replace("patient", "");
                int num = Integer.parseInt(numStr);
                return 10000L + num;
            } catch (NumberFormatException e) {
                return 10001L;
            }
        }
        return 10001L;
    }

    /**
     * Error response DTO
     */
    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
