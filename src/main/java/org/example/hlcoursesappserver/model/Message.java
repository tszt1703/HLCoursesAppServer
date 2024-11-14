package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long message_id;

    private Long sender_id;
    private Long receiver_id;
    private String sender_role;
    private String receiver_role;
    private String message_text;
    private LocalDateTime sent_at;

    // Getters and setters
}
