package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialistService implements UserService<Specialist> {

    private final SpecialistRepository specialistRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
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

        // Обновляем только те поля, которые не равны null
        if (userDetails.getFirstName() != null) {
            specialist.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            specialist.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            specialist.setEmail(userDetails.getEmail());
        }
        if (userDetails.getBirthDate() != null) {
            specialist.setBirthDate(userDetails.getBirthDate());
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

        // Сохраняем обновлённые данные
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
}
