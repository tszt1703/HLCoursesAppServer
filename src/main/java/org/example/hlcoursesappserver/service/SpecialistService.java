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

    // Обновить специалиста
    public Specialist updateSpecialist(Long id, Specialist specialistDetails) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialist.setFirstName(specialistDetails.getFirstName());
        specialist.setLastName(specialistDetails.getLastName());
        specialist.setEmail(specialistDetails.getEmail());
        specialist.setBirthDate(specialistDetails.getBirthDate());
        specialist.setProfilePhotoUrl(specialistDetails.getProfilePhotoUrl());
        specialist.setDescription(specialistDetails.getDescription());
        specialist.setCertificationDocumentUrl(specialistDetails.getCertificationDocumentUrl());
        return specialistRepository.save(specialist);
    }

    // Удалить специалиста
    public void deleteSpecialist(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialistRepository.delete(specialist);
    }
}
