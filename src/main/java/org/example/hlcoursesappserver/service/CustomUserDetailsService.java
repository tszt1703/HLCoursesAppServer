package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;

    public CustomUserDetailsService(SpecialistRepository specialistRepository, ListenerRepository listenerRepository) {
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Попробуем найти пользователя среди специалистов
        Specialist specialist = specialistRepository.findByEmail(username)
                .orElse(null);

        if (specialist != null) {
            return new CustomUserDetails(specialist.getEmail(), specialist.getPassword(), "Specialist");
        }

        // Попробуем найти пользователя среди слушателей
        Listener listener = listenerRepository.findByEmail(username)
                .orElse(null);

        if (listener != null) {
            return new CustomUserDetails(listener.getEmail(), listener.getPassword(), "Listener");
        }

        // Если пользователь не найден
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
