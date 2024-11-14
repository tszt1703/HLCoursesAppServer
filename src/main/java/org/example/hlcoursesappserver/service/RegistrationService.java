package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.RegistrationRequest;
import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final ListenerRepository listenerRepository;

    private final SpecialistRepository specialistRepository;

    private final PasswordEncoder passwordEncoder;

    public RegistrationService(ListenerRepository listenerRepository, SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder) {
        this.listenerRepository = listenerRepository;
        this.specialistRepository = specialistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registerUser(RegistrationRequest request) throws Exception {
        // Проверка уникальности email
        if (listenerRepository.existsByEmail(request.getEmail()) || specialistRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email уже зарегистрирован.");
        }

        // Хеширование пароля
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Сохранение данных в зависимости от роли
        if ("Listener".equalsIgnoreCase(request.getRole())) {
            Listener listener = new Listener(request.getEmail(), encodedPassword);
            listenerRepository.save(listener);
            return new UserDTO(listener.getListenerId(), "Listener");
        } else if ("Specialist".equalsIgnoreCase(request.getRole())) {
            Specialist specialist = new Specialist(request.getEmail(), encodedPassword);
            specialistRepository.save(specialist);
            return new UserDTO(specialist.getSpecialistId(), "Specialist");
        } else {
            throw new Exception("Некорректная роль.");
        }
    }
}
