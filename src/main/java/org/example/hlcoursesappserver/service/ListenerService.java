package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.Listener;
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
    private final CourseService courseService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Autowired
    public ListenerService(ListenerRepository listenerRepository, CourseService courseService, JwtUtil jwtUtil) {
        this.listenerRepository = listenerRepository;
        this.courseService = courseService;
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
    public void updateEmail(Long id, String newEmail) {
        Listener listener = listenerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listener not found"));

        if (listenerRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        listener.setEmail(newEmail);
        listenerRepository.save(listener);
        // Токены больше не генерируются
    }

    public void addCourseToFavorites(Long listenerId, Long courseId) {
        Listener listener = getUserById(listenerId);
        Course course = courseService.getCourseWithDetails(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (listener.getFavoriteCourses().contains(course)) {
            throw new RuntimeException("Course is already in favorites");
        }

        listener.getFavoriteCourses().add(course);
        listenerRepository.save(listener);
    }

    public void removeCourseFromFavorites(Long listenerId, Long courseId) {
        Listener listener = getUserById(listenerId);
        Course course = courseService.getCourseWithDetails(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!listener.getFavoriteCourses().contains(course)) {
            throw new RuntimeException("Course is not in favorites");
        }

        listener.getFavoriteCourses().remove(course);
        listenerRepository.save(listener);
    }

    public List<Course> getFavoriteCourses(Long listenerId) {
        Listener listener = getUserById(listenerId);
        return listener.getFavoriteCourses();
    }

}
