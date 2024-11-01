package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long specialistId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String profilePhotoUrl;
    private String description;
    private String certificationDocumentUrl;

    public String getUsername() {
        return null;
    }

    // Getters and setters


}
