package com.healthcare.readmission.auth.controller;

import com.healthcare.readmission.auth.dto.LoginRequest;
import com.healthcare.readmission.auth.dto.LoginResponse;
import com.healthcare.readmission.auth.entity.User;
import com.healthcare.readmission.auth.service.UserService;
import com.healthcare.readmission.auth.jwt.JwtTokenProvider;
import com.healthcare.readmission.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication REST API Controller - DATABASE INTEGRATED
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserService userService; // ADDED DATABASE SERVICE

    /**
     * Login endpoint - authenticate user from DATABASE and return JWT token
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

            // AUTHENTICATE AGAINST DATABASE
            boolean authenticated = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            if (!authenticated) {
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

            // GET USER FROM DATABASE
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Generate JWT token WITH ROLE
            String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());

            // Update last login time in database
            userService.updateLastLogin(user.getUsername());

            // Create response WITH ALL USER INFO
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setRole(user.getRole());
            response.setFullName(user.getFullName());
            response.setUserId(user.getId());

            // Log successful login
            auditService.logAudit(
                    user.getUsername(),
                    "LOGIN_SUCCESS",
                    "/api/v1/auth/login",
                    "POST",
                    200,
                    "User logged in successfully - Role: " + user.getRole());

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
     * Register new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getRole() != null ? request.getRole() : "PATIENT",
                    request.getFullName());

            auditService.logAudit(
                    request.getUsername(),
                    "USER_REGISTERED",
                    "/api/v1/auth/register",
                    "POST",
                    201,
                    "New user registered - Role: " + user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully", "userId", user.getId()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validate JWT token
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
                String role = jwtTokenProvider.getRoleFromToken(token);

                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("role", role);
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
     * Logout endpoint
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

    // Inner class for register request
    static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String role;
        private String fullName;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
