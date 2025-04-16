package org.example.hlcoursesappserver.dto;

import org.example.hlcoursesappserver.model.Test;

public class TestDTO {
    private Long testId;
    private Long lessonId;
    private String title;

    public TestDTO(Test test) {
        this.testId = test.getTestId();
        this.lessonId = test.getLessonId();
        this.title = test.getTitle();
    }

    // Getters and setters
    public Long getTestId() { return testId; }
    public void setTestId(Long testId) { this.testId = testId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
