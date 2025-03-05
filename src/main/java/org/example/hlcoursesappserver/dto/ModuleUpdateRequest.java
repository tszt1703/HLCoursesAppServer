package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;

public class ModuleUpdateRequest {
    @NotBlank(message = "Название модуля обязательно")
    private String title;
    private String description;
    private Integer position;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}