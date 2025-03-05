package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;

// Таблица для отслеживания прогресса пользователей
@Entity
@Table(name = "progress_stats")
public class ProgressStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    private Long listenerId;

    private Long courseId;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer lessonsCompleted;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer testsPassed;

    @Column(nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private Float progressPercent;

    // Getters and Setters

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Long getListenerId() {
        return listenerId;
    }

    public void setListenerId(Long listenerId) {
        this.listenerId = listenerId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getLessonsCompleted() {
        return lessonsCompleted;
    }

    public void setLessonsCompleted(Integer lessonsCompleted) {
        this.lessonsCompleted = lessonsCompleted;
    }

    public Integer getTestsPassed() {
        return testsPassed;
    }

    public void setTestsPassed(Integer testsPassed) {
        this.testsPassed = testsPassed;
    }

    public Float getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Float progressPercent) {
        this.progressPercent = progressPercent;
    }

    public ProgressStat() {
    }

    public ProgressStat(Long listenerId, Long courseId) {
        this.listenerId = listenerId;
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "ProgressStat{" +
                "progressId=" + progressId +
                ", listenerId=" + listenerId +
                ", courseId=" + courseId +
                ", lessonsCompleted=" + lessonsCompleted +
                ", testsPassed=" + testsPassed +
                ", progressPercent=" + progressPercent +
                '}';
    }
}

