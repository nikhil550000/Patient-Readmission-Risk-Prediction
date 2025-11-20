package com.healthcare.readmission.controller;

import com.healthcare.readmission.auth.jwt.JwtTokenProvider; // ← Changed this
import com.healthcare.readmission.dto.PatientData;
import com.healthcare.readmission.dto.PredictionResult;
import com.healthcare.readmission.model.entity.Prediction;
import com.healthcare.readmission.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/predictions")
// @CrossOrigin(origins = "*")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // ← Changed this

    @PostMapping("/predict")
    public ResponseEntity<?> predict(
            @RequestBody PatientData patientData,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token); // ← Changed this

            Long userId = 1L; // TODO: Get actual userId from database

            PredictionResult result = predictionService.predictAndSave(patientData, userId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Prediction failed: " + e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = 1L;
            List<Prediction> predictions = predictionService.getUserPredictions(userId);
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve history: " + e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecent() {
        try {
            List<Prediction> predictions = predictionService.getRecentPredictions();
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve predictions: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPredictionById(@PathVariable Long id) {
        try {
            return predictionService.getPredictionById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve prediction: " + e.getMessage()));
        }
    }

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
