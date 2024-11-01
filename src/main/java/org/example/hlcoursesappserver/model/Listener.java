package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Listener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listenerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String profilePhotoUrl;

//    public Long getId() {
//    }
//
//    public String getUsername() {
//    }



    // Getters and setters

}
