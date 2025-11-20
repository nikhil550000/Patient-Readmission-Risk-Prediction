package com.healthcare.readmission.auth.controller;

import com.healthcare.readmission.auth.dto.LoginRequest;
import com.healthcare.readmission.auth.dto.LoginResponse;
import com.healthcare.readmission.auth.jwt.JwtTokenProvider;
import com.healthcare.readmission.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication REST API Controller
 */
@RestController
@RequestMapping("/api/v1/auth")
// @CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuditService auditService;

    /**
     * Login endpoint - authenticate user and return JWT token
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is required"));
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password is required"));
            }

            // TODO: In production, validate against database
            // For demo purposes, accept specific test credentials
            boolean isValidUser = authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            if (!isValidUser) {
                auditService.logAudit(
                        loginRequest.getUsername(),
                        "LOGIN_FAILED",
                        "/api/v1/auth/login",
                        "POST",
                        401,
                        "Invalid credentials");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(loginRequest.getUsername());

            // Create response
            LoginResponse response = new LoginResponse(token, loginRequest.getUsername());

            // Log successful login
            auditService.logAudit(
                    loginRequest.getUsername(),
                    "LOGIN_SUCCESS",
                    "/api/v1/auth/login",
                    "POST",
                    200,
                    "User logged in successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            auditService.logAudit(
                    loginRequest.getUsername(),
                    "LOGIN_ERROR",
                    "/api/v1/auth/login",
                    "POST",
                    500,
                    "Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    /**
     * Validate JWT token
     * GET /api/v1/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid authorization header"));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Invalid or expired token"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Token validation failed: " + e.getMessage()));
        }
    }

    /**
     * Logout endpoint (optional - JWT is stateless)
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = "unknown";

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                username = jwtTokenProvider.getUsernameFromToken(token);
            } catch (Exception e) {
                // Ignore token parsing errors on logout
            }
        }

        auditService.logAudit(
                username,
                "LOGOUT",
                "/api/v1/auth/logout",
                "POST",
                200,
                "User logged out");

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * Demo user authentication
     * In production, validate against database with hashed passwords
     */
    private boolean authenticateUser(String username, String password) {
        // Demo credentials for testing
        // TODO: Replace with database lookup and password hashing (BCrypt)

        Map<String, String> demoUsers = new HashMap<>();
        demoUsers.put("admin", "admin123");
        demoUsers.put("doctor", "doctor123");
        demoUsers.put("user", "user123");

        return demoUsers.containsKey(username) &&
                demoUsers.get(username).equals(password);
    }
}
