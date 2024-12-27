package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SpecialistService implements UserService<Specialist> {

    private final SpecialistRepository specialistRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, JwtUtil jwtUtil) {
        this.specialistRepository = specialistRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Specialist createUser(Specialist specialist) {
        specialist.setPassword(passwordEncoder.encode(specialist.getPassword()));
        return specialistRepository.save(specialist);
    }

    @Override
    public List<Specialist> getAllUsers() {
        return specialistRepository.findAll();
    }

    @Override
    public Optional<Specialist> getUserByEmail(String email) {
        return specialistRepository.findByEmail(email);
    }

    @Override
    public Specialist getUserById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
    }

    @Override
    public void updateUser(Long id, Specialist userDetails) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        if (userDetails.getFirstName() != null) {
            specialist.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            specialist.setLastName(userDetails.getLastName());
        }
        if (userDetails.getBirthDate() != null) {
            specialist.setBirthDate(userDetails.getBirthDate());
        } else {
            specialist.setBirthDate(null); // Явное обнуление даты
        }

        if (userDetails.getProfilePhotoUrl() != null) {
            specialist.setProfilePhotoUrl(userDetails.getProfilePhotoUrl());
        }
        if (userDetails.getDescription() != null) {
            specialist.setDescription(userDetails.getDescription());
        }
        if (userDetails.getSocialLinks() != null) {
            specialist.setSocialLinks(userDetails.getSocialLinks());
        }
        if (userDetails.getCertificationDocumentUrl() != null) {
            specialist.setCertificationDocumentUrl(userDetails.getCertificationDocumentUrl());
        }

        specialistRepository.save(specialist);
    }

    @Override
    public void deleteUser(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialistRepository.delete(specialist);
    }

    @Override
    public boolean isUserAuthorizedToUpdate(Long id, String email) {
        Specialist specialist = specialistRepository.findById(id).orElse(null);
        return specialist != null && specialist.getEmail().equals(email);
    }

    @Override
    public boolean isUserAuthorizedToDelete(Long id, String email) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        return specialist.getEmail().equals(email);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (!passwordEncoder.matches(oldPassword, specialist.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        specialist.setPassword(passwordEncoder.encode(newPassword));
        specialistRepository.save(specialist);
    }

    @Override
    public Map<String, String> updateEmail(Long id, String newEmail) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        if (specialistRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        specialist.setEmail(newEmail);
        specialistRepository.save(specialist);

        // Генерация новых токенов
        String accessToken = jwtUtil.generateAccessToken(specialist.getSpecialistId(), newEmail, "Specialist");
        String refreshToken = jwtUtil.generateRefreshToken(specialist.getSpecialistId(), newEmail, "Specialist");

        // Возвращаем токены для контроллера
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }
}
