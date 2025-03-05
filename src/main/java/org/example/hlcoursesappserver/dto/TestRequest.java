package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TestRequest {
    @NotNull(message = "Идентификатор урока обязателен")
    private Long lessonId;

    @NotBlank(message = "Название теста обязательно")
    private String title;

    // Геттеры и сеттеры
    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
