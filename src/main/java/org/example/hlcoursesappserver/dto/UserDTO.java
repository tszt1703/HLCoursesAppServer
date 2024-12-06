package org.example.hlcoursesappserver.dto;


import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePhotoUrl;
    private String description; // Only for Specialists
    private String certificationDocumentUrl; // Only for Specialists
    private LocalDate birthDate;

    // Default constructor
    public UserDTO() {}

    // Constructor for creating UserDTO with userId, role, and email
    public UserDTO(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UserDTO(Long id, String role) {
        this.id = id;
        this.role = role;
    }


    // Constructor for new user with more details
    public UserDTO(Long id, String firstName, String lastName, String role,
                   String profilePhotoUrl, String description,
                   String certificationDocumentUrl, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePhotoUrl = profilePhotoUrl;
        this.birthDate = birthDate;
        if ("Specialist".equals(role)) {
            this.description = description;
            this.certificationDocumentUrl = certificationDocumentUrl;
        }
    }

    // Getters and setters for all fields
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
