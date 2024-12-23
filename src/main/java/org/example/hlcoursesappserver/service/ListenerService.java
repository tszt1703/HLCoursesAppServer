package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListenerService implements UserService<Listener> {

    private final ListenerRepository listenerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public ListenerService(ListenerRepository listenerRepository) {
        this.listenerRepository = listenerRepository;
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
        if (userDetails.getEmail() != null) {
            listener.setEmail(userDetails.getEmail());
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


}
