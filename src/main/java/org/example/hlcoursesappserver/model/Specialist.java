package org.example.hlcoursesappserver.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "specialists")
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

    public Specialist() {
    }

    public Specialist(String firstName, String lastName, String email, String password, LocalDate birthDate, String profilePhotoUrl, String description, String certificationDocumentUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.description = description;
        this.certificationDocumentUrl = certificationDocumentUrl;
    }

    public Specialist(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters


    public Long getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(Long specialistId) {
        this.specialistId = specialistId;
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

    public String getCertificationDocumentUrl() {
        return certificationDocumentUrl;
    }

    public void setCertificationDocumentUrl(String certificationDocumentUrl) {
        this.certificationDocumentUrl = certificationDocumentUrl;
    }

    @Override
    public String toString() {
        return "Specialist{" +
                "specialistId=" + specialistId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birthDate=" + birthDate +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", description='" + description + '\'' +
                ", certificationDocumentUrl='" + certificationDocumentUrl + '\'' +
                '}';
    }
}
