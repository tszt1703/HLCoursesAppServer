package org.example.hlcoursesappserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long lessonId;

    private Long moduleId;

    private String title;

    private String content;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer position;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LessonFile> files;

    @OneToMany(mappedBy = "lessonId", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Test> tests;

    // Getters and setters

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<LessonFile> getFiles() {
        return files;
    }

    public void setFiles(List<LessonFile> files) {
        this.files = files;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public Lesson() {
    }

    public Lesson(Long moduleId, String title, String content, Integer position) {
        this.moduleId = moduleId;
        this.title = title;
        this.content = content;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", position=" + position +
                '}';
    }
}