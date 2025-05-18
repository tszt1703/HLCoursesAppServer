package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;

public class LessonUpdateRequest {
    @NotBlank(message = "Название урока обязательно")
    private String title;
    private String content;
    private Integer position;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}