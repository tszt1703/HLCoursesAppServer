package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialistService {

//    private final SpecialistRepository specialistRepository;
//
//    @Autowired
//    public SpecialistService(SpecialistRepository specialistRepository) {
//        this.specialistRepository = specialistRepository;
//    }
//
//    public Specialist createSpecialist(Specialist specialist) {
//        return specialistRepository.save(specialist);
//    }
//
//    public Optional<Specialist> findById(Long id) {
//        return specialistRepository.findById(id);
//    }
//
//    public List<Specialist> findAll() {
//        return specialistRepository.findAll();
//    }
//
//    public void deleteSpecialist(Long id) {
//        specialistRepository.deleteById(id);
//    }
}
