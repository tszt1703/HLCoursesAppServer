package org.example.hlcoursesappserver.dto;

// LoginResponse.java
public class LoginResponse {
    private Long userId;
    private String role;
    private String accessToken;
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(Long userId, String role, String accessToken, String refreshToken) {
        this.userId = userId;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    // Setters

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "userId=" + userId +
                ", role='" + role + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}