package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ListenerService implements UserService<Listener> {

    private final ListenerRepository listenerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Autowired
    public ListenerService(ListenerRepository listenerRepository, JwtUtil jwtUtil) {
        this.listenerRepository = listenerRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Listener createUser(Listener listener) {
        listener.setPassword(passwordEncoder.encode(listener.getPassword()));
        return listenerRepository.save(listener);
    }

    @Override
    public List<Listener> getAllUsers() {
        return listenerRepository.findAll();
    }

    @Override
    public Optional<Listener> getUserByEmail(String email) {
        return listenerRepository.findByEmail(email);
    }

    @Override
    public Listener getUserById(Long id) {
        return listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));
    }

    @Override
    public void updateUser(Long id, Listener userDetails) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));

        // Обновляем только те поля, которые не равны null
        if (userDetails.getFirstName() != null) {
            listener.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            listener.setLastName(userDetails.getLastName());
        }
        if (userDetails.getBirthDate() != null) {
            listener.setBirthDate(userDetails.getBirthDate());
        }
        if (userDetails.getProfilePhotoUrl() != null) {
            listener.setProfilePhotoUrl(userDetails.getProfilePhotoUrl());
        }
        if (userDetails.getDescription() != null) {
            listener.setDescription(userDetails.getDescription());
        }

        // Сохраняем обновлённые данные
        listenerRepository.save(listener);
    }


    @Override
    public void deleteUser(Long id) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));
        listenerRepository.delete(listener);
    }

    @Override
    public boolean isUserAuthorizedToUpdate(Long id, String email) {
        Listener listener = listenerRepository.findById(id).orElse(null);
        return listener != null && listener.getEmail().equals(email);
    }

    @Override
    public boolean isUserAuthorizedToDelete(Long id, String email) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));
        return listener.getEmail().equals(email);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (!passwordEncoder.matches(oldPassword, listener.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        listener.setPassword(passwordEncoder.encode(newPassword));
        listenerRepository.save(listener);
    }

    @Override
    public Map<String, String> updateEmail(Long id, String newEmail) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));

        if (listenerRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        listener.setEmail(newEmail);
        listenerRepository.save(listener);

        // Генерация новых токенов с правильной ролью
        String accessToken = jwtUtil.generateAccessToken(listener.getListenerId(), newEmail, "Listener");
        String refreshToken = jwtUtil.generateRefreshToken(listener.getListenerId(), newEmail);

        // Возвращаем токены для контроллера
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }



}
