package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_applications")
public class CourseApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listener_id", nullable = false)
    private Long listenerId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    // Конструкторы
    public CourseApplication() {
    }

    public CourseApplication(Long listenerId, Long courseId) {
        this.listenerId = listenerId;
        this.courseId = courseId;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getListenerId() { return listenerId; }
    public void setListenerId(Long listenerId) { this.listenerId = listenerId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
}
