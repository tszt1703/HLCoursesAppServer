package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionUpdateRequest {
    @NotBlank(message = "Текст вопроса обязателен")
    private String questionText;

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}