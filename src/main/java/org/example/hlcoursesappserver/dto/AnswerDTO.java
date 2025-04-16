package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Answer;

public class AnswerDTO {
    private Long answerId;
    private Long questionId;
    private String answerText;
    private Boolean isCorrect;

    public AnswerDTO(Answer answer) {
        this.answerId = answer.getAnswerId();
        this.questionId = answer.getQuestionId();
        this.answerText = answer.getAnswerText();
        this.isCorrect = answer.getCorrect();
    }

    // Getters and setters
    public Long getAnswerId() { return answerId; }
    public void setAnswerId(Long answerId) { this.answerId = answerId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}