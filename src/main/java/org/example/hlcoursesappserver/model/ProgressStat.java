package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_stats")
public class ProgressStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progress_id;

    @ManyToOne
    @JoinColumn(name = "listener_id", nullable = false)
    private Listener listener;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private int lessons_completed;
    private int tests_passed;
    private double progress_percent;
    private LocalDateTime last_accessed;

    // Getters and setters
}

