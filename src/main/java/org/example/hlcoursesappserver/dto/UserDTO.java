package org.example.hlcoursesappserver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String profilePhotoUrl;
    private String description;
    private String socialLinks; // Only for Specialists
    private String certificationDocumentUrl; // Only for Specialists
    private boolean isEmailVerified; // Добавлено для статуса подтверждения

    // Default constructor
    public UserDTO() {}

    // Constructor for registration (minimal data from pending_users)
    public UserDTO(Long id, String email, String role, boolean isEmailVerified) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.isEmailVerified = isEmailVerified;
    }

    // Constructor for Listeners
    public UserDTO(Long id, String firstName, String lastName, String email, String role,
                   LocalDate birthDate, String profilePhotoUrl, String description) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.birthDate = birthDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.description = description;
        this.isEmailVerified = true; // Слушатели всегда подтверждены
    }

    // Constructor for Specialists
    public UserDTO(Long id, String firstName, String lastName, String email, String role,
                   LocalDate birthDate, String profilePhotoUrl, String description,
                   String socialLinks, String certificationDocumentUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.birthDate = birthDate;
        this.profilePhotoUrl = profilePhotoUrl;
        this.description = description;
        this.socialLinks = socialLinks;
        this.certificationDocumentUrl = certificationDocumentUrl;
        this.isEmailVerified = true; // Специалисты всегда подтверждены
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }

    public String getCertificationDocumentUrl() {
        return certificationDocumentUrl;
    }

    public void setCertificationDocumentUrl(String certificationDocumentUrl) {
        this.certificationDocumentUrl = certificationDocumentUrl;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", description='" + description + '\'' +
                ", socialLinks='" + socialLinks + '\'' +
                ", certificationDocumentUrl='" + certificationDocumentUrl + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                '}';
    }
}