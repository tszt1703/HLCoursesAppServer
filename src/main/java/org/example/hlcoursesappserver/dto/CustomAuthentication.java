package org.example.hlcoursesappserver.dto;

public class CustomAuthentication {
    private final Long userId;
    private String role;

    public CustomAuthentication(Long userId) {
        this.userId = userId;
    }

    public CustomAuthentication(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
