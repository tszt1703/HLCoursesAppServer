package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.CustomAuthentication;
import org.example.hlcoursesappserver.dto.LoginRequest;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ListenerRepository listenerRepository;
    private final SpecialistRepository specialistRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ListenerRepository listenerRepository, SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder) {
        this.listenerRepository = listenerRepository;
        this.specialistRepository = specialistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomAuthentication authenticateUser(LoginRequest request) {
        // Проверяем среди слушателей
        Optional<Listener> listenerOpt = listenerRepository.findByEmail(request.getEmail());
        if (listenerOpt.isPresent() && passwordEncoder.matches(request.getPassword(), listenerOpt.get().getPassword())) {
            Listener listener = listenerOpt.get();
            return new CustomAuthentication(listener.getListenerId(), listener.getEmail(), "Listener");
        }

        // Проверяем среди специалистов
        Optional<Specialist> specialistOpt = specialistRepository.findByEmail(request.getEmail());
        if (specialistOpt.isPresent() && passwordEncoder.matches(request.getPassword(), specialistOpt.get().getPassword())) {
            Specialist specialist = specialistOpt.get();
            return new CustomAuthentication(specialist.getSpecialistId(), specialist.getEmail(), "Specialist");
        }

        // Если пользователь не найден
        return null;
    }
}

