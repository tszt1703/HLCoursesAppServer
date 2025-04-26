package org.example.hlcoursesappserver.service;

import org.example.hlcoursesappserver.dto.RegistrationRequest;
import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.PendingUser;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.PendingUserRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    private final PendingUserRepository pendingUserRepository;
    private final ListenerRepository listenerRepository;
    private final SpecialistRepository specialistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public RegistrationService(PendingUserRepository pendingUserRepository, ListenerRepository listenerRepository,
                               SpecialistRepository specialistRepository, PasswordEncoder passwordEncoder,
                               JavaMailSender mailSender) {
        this.pendingUserRepository = pendingUserRepository;
        this.listenerRepository = listenerRepository;
        this.specialistRepository = specialistRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void verifyUser(String token) {
        Optional<PendingUser> pendingUserOpt = pendingUserRepository.findByVerificationToken(token);
        if (!pendingUserOpt.isPresent()) {
            throw new IllegalArgumentException("Недействительный токен подтверждения");
        }

        PendingUser pendingUser = pendingUserOpt.get();
        if (listenerRepository.findByEmail(pendingUser.getEmail()).isPresent() ||
                specialistRepository.findByEmail(pendingUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже зарегистрирован в listeners или specialists");
        }

        if ("Listener".equalsIgnoreCase(pendingUser.getRole())) {
            Listener listener = new Listener();
            listener.setEmail(pendingUser.getEmail());
            listener.setPassword(pendingUser.getPassword());
            listenerRepository.save(listener);
        } else if ("Specialist".equalsIgnoreCase(pendingUser.getRole())) {
            Specialist specialist = new Specialist();
            specialist.setEmail(pendingUser.getEmail());
            specialist.setPassword(pendingUser.getPassword());
            specialistRepository.save(specialist);
        } else {
            throw new IllegalArgumentException("Неверная роль пользователя");
        }

        pendingUserRepository.delete(pendingUser);
    }

    @Transactional
    public UserDTO registerUser(RegistrationRequest request) {
        // Проверка, что email не занят в pending_users, listeners или specialists
        if (pendingUserRepository.findByEmail(request.getEmail()).isPresent() ||
                listenerRepository.findByEmail(request.getEmail()).isPresent() ||
                specialistRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }

        // Создание неподтверждённого пользователя
        PendingUser pendingUser = new PendingUser();
        pendingUser.setEmail(request.getEmail());
        pendingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        pendingUser.setRole(request.getRole());
        pendingUser.setVerificationToken(UUID.randomUUID().toString());
        pendingUser.setLastVerificationRequest(LocalDateTime.now());

        pendingUser = pendingUserRepository.save(pendingUser);

        // Отправка письма
        sendVerificationEmail(pendingUser.getEmail(), pendingUser.getVerificationToken());

        return mapToUserDTO(pendingUser);
    }

    private void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Подтверждение регистрации");
        message.setText("Пожалуйста, подтвердите вашу электронную почту, перейдя по ссылке:\n" +
                "http://localhost:8080/auth/verify?token=" + token);
        mailSender.send(message);
    }

    private UserDTO mapToUserDTO(PendingUser pendingUser) {
        return new UserDTO(
                pendingUser.getId(),
                pendingUser.getEmail(),
                pendingUser.getRole(),
                false // Пользователи в pending_users всегда неподтверждённые
        );
    }
}