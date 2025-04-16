package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Question;

public class QuestionDTO {
    private Long questionId;
    private Long testId;
    private String questionText;

    public QuestionDTO(Question question) {
        this.questionId = question.getQuestionId();
        this.testId = question.getTestId();
        this.questionText = question.getQuestionText();
    }

    // Getters and setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}