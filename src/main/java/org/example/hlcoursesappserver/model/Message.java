package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Listener sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Specialist receiver;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public void setSender(Listener sender) {
    }

    public void setReceiver(Specialist receiver) {

    }

    // Constructors, Getters, and Setters
}
