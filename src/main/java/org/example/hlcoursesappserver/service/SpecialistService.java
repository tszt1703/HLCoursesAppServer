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
        specialist.setFirstName(userDetails.getFirstName());
        specialist.setLastName(userDetails.getLastName());
        specialist.setEmail(userDetails.getEmail());
        specialist.setBirthDate(userDetails.getBirthDate());
        specialist.setProfilePhotoUrl(userDetails.getProfilePhotoUrl());
        specialist.setDescription(userDetails.getDescription());
        specialist.setSocialLinks(userDetails.getSocialLinks());
        specialist.setCertificationDocumentUrl(userDetails.getCertificationDocumentUrl());
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
