package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialistService {

    @Autowired
    private SpecialistRepository specialistRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Создание специалиста
    public Specialist createSpecialist(Specialist specialist) {
        specialist.setPassword(passwordEncoder.encode(specialist.getPassword()));
        return specialistRepository.save(specialist);
    }

    // Получить всех специалистов
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    // Получить специалиста по email
    public Optional<Specialist> getSpecialistByEmail(String email) {
        return specialistRepository.findByEmail(email);
    }

    public Specialist getSpecialistById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
    }

    // Обновить специалиста
    public void updateSpecialist(Long id, Specialist specialistDetails) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialist.setFirstName(specialistDetails.getFirstName());
        specialist.setLastName(specialistDetails.getLastName());
        specialist.setEmail(specialistDetails.getEmail());
        specialist.setBirthDate(specialistDetails.getBirthDate());
        specialist.setProfilePhotoUrl(specialistDetails.getProfilePhotoUrl());
        specialist.setDescription(specialistDetails.getDescription());
        specialist.setCertificationDocumentUrl(specialistDetails.getCertificationDocumentUrl());
        specialistRepository.save(specialist);
    }

    // В SpecialistService
    public boolean isUserAuthorizedToUpdate(Long id, String email) {
        // Логика проверки, имеет ли пользователь с таким email право обновить специалиста с данным id
        Specialist specialist = specialistRepository.findById(id).orElse(null);
        if (specialist != null && specialist.getEmail().equals(email)) {
            return true;
        }
        return false;
    }



    // Удалить специалиста
    public void deleteSpecialist(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialistRepository.delete(specialist);
    }

    // Проверка, может ли текущий пользователь удалить специалиста
    public boolean isUserAuthorizedToDelete(Long id, String emailFromToken) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        return specialist.getEmail().equals(emailFromToken);
    }
}
