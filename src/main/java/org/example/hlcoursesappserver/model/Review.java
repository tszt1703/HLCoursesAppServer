package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne
    @JoinColumn(name = "listener_id", nullable = false)
    private Listener listener;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private int rating;
    private String review_text;
    private LocalDateTime created_at;

    // Getters and setters
}

