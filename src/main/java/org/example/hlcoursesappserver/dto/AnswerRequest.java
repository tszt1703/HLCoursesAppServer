package org.example.hlcoursesappserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnswerRequest {
    @NotNull(message = "Идентификатор вопроса обязателен")
    private Long questionId;

    @NotBlank(message = "Текст ответа обязателен")
    private String answerText;

    @NotNull(message = "Признак правильности обязателен")
    private Boolean isCorrect;

    // Геттеры и сеттеры
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}