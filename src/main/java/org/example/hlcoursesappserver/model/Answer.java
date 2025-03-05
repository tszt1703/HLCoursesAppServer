package org.example.hlcoursesappserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

// Таблица для ответов на вопросы
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long answerId;

    private Long questionId;

    @Column(nullable = false)
    private String answerText;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isCorrect;

    // Getters and Setters

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

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

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Answer() {
    }

    public Answer(Long questionId, String answerText, Boolean isCorrect) {
        this.questionId = questionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }


}
