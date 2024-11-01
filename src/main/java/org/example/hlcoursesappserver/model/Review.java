package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "listener_id", nullable = false)
    private Listener listener;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public void setListener(Listener listener) {
    }

    public void setCourse(Course course) {
    }

    public void setContent(String content) {
    }

    public void setRating(int rating) {
    }

    public void setTimestamp(LocalDateTime timestamp) {
    }



    // Constructors, Getters, and Setters
}
