package org.example.hlcoursesappserver.dto;

import java.time.LocalDate;

public class UserDTO {
    private final Long userId;
    private Long id;
    private String username;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePhotoUrl;
    private String description; // Только для Specialists
    private String certificationDocumentUrl; // Только для Specialists
    private LocalDate birthDate;

    // Конструктор для создания UserDTO с userId и role
    public UserDTO(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    // Конструктор для нового пользователя
    public UserDTO(Long userId, Long id, String username, String role) {
        this.userId = userId;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Конструктор для специалиста с дополнительными данными
    public UserDTO(Long userId, String firstName, String lastName, String role,
                   String profilePhotoUrl, String description,
                   String certificationDocumentUrl, LocalDate birthDate) {
        this.userId = userId;
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
    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
