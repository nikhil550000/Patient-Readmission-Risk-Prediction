package com.healthcare.readmission.auth.dto;

/**
 * Login response with JWT token
 */
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String message;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.message = "Login successful";
    }

    public LoginResponse(String token, String username, String message) {
        this.token = token;
        this.username = username;
        this.message = message;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
