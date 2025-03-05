package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;

public class TestUpdateRequest {
    @NotBlank(message = "Название теста обязательно")
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}