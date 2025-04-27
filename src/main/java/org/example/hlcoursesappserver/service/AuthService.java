package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.CustomAuthentication;
import org.example.hlcoursesappserver.dto.LoginRequest;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.PendingUser;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.PendingUserRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ListenerRepository listenerRepository;
    private final SpecialistRepository specialistRepository;
    private final PendingUserRepository pendingUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(ListenerRepository listenerRepository, SpecialistRepository specialistRepository,
                       PendingUserRepository pendingUserRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.listenerRepository = listenerRepository;
        this.specialistRepository = specialistRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public CustomAuthentication authenticateUser(LoginRequest request) {
        // Проверяем, есть ли пользователь в pending_users
        if (pendingUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email не подтверждён. Пожалуйста, проверьте вашу почту.");
        }

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

        // Если пользователь не найден или неверный пароль
        throw new IllegalArgumentException("Неверный email или пароль");
    }

    public String requestPasswordReset(String email) {
        // Проверяем, что email подтверждён (нет в pending_users)
        if (pendingUserRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email не подтверждён. Пожалуйста, подтвердите ваш email.");
        }

        // Проверяем, существует ли пользователь
        Optional<Listener> listenerOpt = listenerRepository.findByEmail(email);
        Optional<Specialist> specialistOpt = specialistRepository.findByEmail(email);

        if (listenerOpt.isEmpty() && specialistOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с таким email не найден");
        }

        // Определяем роль
        String role = listenerOpt.isPresent() ? "Listener" : "Specialist";

        // Генерируем JWT для сброса пароля
        return jwtUtil.generateResetPasswordToken(email, role);
    }

    public void resetPassword(String resetToken, String newPassword) {
        // Проверяем токен
        if (!jwtUtil.validateResetPasswordToken(resetToken)) {
            throw new IllegalArgumentException("Недействительный или истёкший токен сброса пароля");
        }

        // Извлекаем email и роль
        String email = jwtUtil.extractEmailFromResetToken(resetToken);
        String role = jwtUtil.extractRoleFromResetToken(resetToken);

        // Обновляем пароль
        if ("Listener".equalsIgnoreCase(role)) {
            Optional<Listener> listenerOpt = listenerRepository.findByEmail(email);
            if (listenerOpt.isPresent()) {
                Listener listener = listenerOpt.get();
                listener.setPassword(passwordEncoder.encode(newPassword));
                listenerRepository.save(listener);
            } else {
                throw new IllegalArgumentException("Пользователь не найден");
            }
        } else if ("Specialist".equalsIgnoreCase(role)) {
            Optional<Specialist> specialistOpt = specialistRepository.findByEmail(email);
            if (specialistOpt.isPresent()) {
                Specialist specialist = specialistOpt.get();
                specialist.setPassword(passwordEncoder.encode(newPassword));
                specialistRepository.save(specialist);
            } else {
                throw new IllegalArgumentException("Пользователь не найден");
            }
        } else {
            throw new IllegalArgumentException("Недопустимая роль в токене");
        }
    }
}