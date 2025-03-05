package org.example.hlcoursesappserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long testId;

    private Long lessonId;

    private String title;

    // Getters and Setters

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lesson) {
        this.lessonId = lesson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Test() {
    }

    public Test(Long lessonId, String title) {
        this.lessonId = lessonId;
        this.title = title;
    }
}
