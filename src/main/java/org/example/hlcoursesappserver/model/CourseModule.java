package org.example.hlcoursesappserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

// Таблица для модулей курсов
@Entity
@Table(name = "course_modules")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long moduleId;

    private Long courseId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer position;

    @OneToMany(mappedBy = "moduleId", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Lesson> lessons;

    // Getters and Setters

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }


    public CourseModule() {
    }

    public CourseModule(Long courseId, String title, String description, Integer position) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.position = position;
    }

    @Override
    public String toString() {
        return "CourseModule{" +
                "moduleId=" + moduleId +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                '}';
    }
}
