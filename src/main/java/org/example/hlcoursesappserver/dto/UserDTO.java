package org.example.hlcoursesappserver.dto;

import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePhotoUrl;
    private String description; // Только для Specialists
    private String certificationDocumentUrl; // Только для Specialists
    private LocalDate birthDate;

    // Конструктор по умолчанию
    // Публичный конструктор по умолчанию
    public UserDTO() {}

    // Конструктор для создания UserDTO с userId и role
    public UserDTO(Long id, String role) {
        this.id = id;
        this.role = role;
    }

    // Конструктор для нового пользователя
    public UserDTO(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Конструктор для специалиста с дополнительными данными
    public UserDTO(Long id, String firstName, String lastName, String role,
                   String profilePhotoUrl, String description,
                   String certificationDocumentUrl, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePhotoUrl = profilePhotoUrl;
        this.birthDate = birthDate;

        // Для Specialists, передаем description и certificationDocumentUrl
        if ("Specialist".equals(role)) {
            this.description = description;
            this.certificationDocumentUrl = certificationDocumentUrl;
        }
    }

    // Геттеры и сеттеры для всех полей

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
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
