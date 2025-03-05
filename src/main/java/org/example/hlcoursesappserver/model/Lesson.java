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
    private String photoUrl;
    private String videoUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer position;

    @OneToMany(mappedBy = "lessonId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Test> tests;

    // Getters and setters

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Long  getModuleId() {
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }


    public Lesson() {
    }

    public Lesson(Long moduleId, String title, String content, String photoUrl, String videoUrl, Integer position) {
        this.moduleId = moduleId;
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", moduleId=" + moduleId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", position=" + position +
                '}';
    }
}

