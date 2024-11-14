package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lesson_id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String title;
    private String content;
    private String photo_url;
    private String video_url;
    private int order_num;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Test> tests;

    // Getters and setters
}

