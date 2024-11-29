package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListenerService {

    @Autowired
    private ListenerRepository listenerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Создание слушателя
    public Listener createListener(Listener listener) {
        listener.setPassword(passwordEncoder.encode(listener.getPassword()));
        return listenerRepository.save(listener);
    }

    // Получить всех слушателей
    public List<Listener> getAllListeners() {
        return listenerRepository.findAll();
    }

    // Получить слушателя по email
    public Optional<Listener> getListenerByEmail(String email) {
        return listenerRepository.findByEmail(email);
    }

    // Обновить слушателя
    public Listener updateListener(Long id, Listener listenerDetails) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));
        listener.setFirstName(listenerDetails.getFirstName());
        listener.setLastName(listenerDetails.getLastName());
        listener.setEmail(listenerDetails.getEmail());
        listener.setBirthDate(listenerDetails.getBirthDate());
        listener.setProfilePhotoUrl(listenerDetails.getProfilePhotoUrl());
        return listenerRepository.save(listener);
    }

    // Удалить слушателя
    public void deleteListener(Long id) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));
        listenerRepository.delete(listener);
    }
}
