package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionRequest {
    @NotNull(message = "Идентификатор теста обязателен")
    private Long testId;

    @NotBlank(message = "Текст вопроса обязателен")
    private String questionText;

    // Геттеры и сеттеры
    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
