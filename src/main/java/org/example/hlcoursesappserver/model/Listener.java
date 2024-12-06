package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "listeners")
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
    private String description;

    public Listener() {
    }

    public Listener(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters


    public Long getListenerId() {
        return listenerId;
    }

    public void setListenerId(Long listenerId) {
        this.listenerId = listenerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Listener{" +
                "listenerId=" + listenerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birthDate=" + birthDate +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", description='" + description + '\''+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Listener)) return false;
        Listener listener = (Listener) o;
        return listenerId.equals(listener.listenerId);
    }

    @Override
    public int hashCode() {
        return listenerId.hashCode();
    }
}
