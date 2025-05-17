package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.exception.InvalidTokenException;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.PendingUser;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.PendingUserRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.example.hlcoursesappserver.service.AuthService;
import org.example.hlcoursesappserver.service.RegistrationService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ListenerRepository listenerRepository;
    private final SpecialistRepository specialistRepository;
    private final PendingUserRepository pendingUserRepository;
    private final JavaMailSender mailSender;

    /**
     * Конструктор контроллера аутентификации.
     *
     * @param authService            сервис аутентификации
     * @param registrationService    сервис регистрации
     * @param jwtUtil                утилита для работы с JWT-токенами
     * @param listenerRepository     репозиторий слушателей
     * @param specialistRepository   репозиторий специалистов
     * @param pendingUserRepository  репозиторий ожидающих пользователей
     * @param mailSender             почтовый отправитель
     */
    public AuthController(AuthService authService, RegistrationService registrationService, JwtUtil jwtUtil,
                          ListenerRepository listenerRepository, SpecialistRepository specialistRepository,
                          PendingUserRepository pendingUserRepository, JavaMailSender mailSender) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.listenerRepository = listenerRepository;
        this.specialistRepository = specialistRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.mailSender = mailSender;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param request запрос на регистрацию
     * @return ответ с данными пользователя
     */
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя в системе.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody RegistrationRequest request) {
        try {
            UserDTO user = registrationService.registerUser(request);
            return ResponseEntity.ok(new ApiResponse<>(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Произошла ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Авторизация пользователя.
     *
     * @param request запрос на авторизацию
     * @return ответ с токенами доступа и обновления
     */
    @Operation(summary = "Авторизация пользователя", description = "Авторизует пользователя и возвращает токены.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Авторизация успешна"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            CustomAuthentication authentication = authService.authenticateUser(request);

            String accessToken = jwtUtil.generateAccessToken(authentication.getUserId(), authentication.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(authentication.getUserId(), authentication.getRole());

            LoginResponse loginResponse = new LoginResponse(authentication.getUserId(), authentication.getRole(), accessToken, refreshToken);
            return ResponseEntity.ok(new ApiResponse<>(loginResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Произошла ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Обновление токенов доступа и обновления.
     *
     * @param request запрос на обновление токенов
     * @return ответ с новыми токенами
     */
    @Operation(summary = "Обновление токенов", description = "Обновляет access и refresh токены.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Неверный или истекший refresh токен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (jwtUtil.validateRefreshToken(refreshToken)) {
                Long userId = jwtUtil.extractUserId(refreshToken);
                String role = jwtUtil.extractRole(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(userId, role);
                String newRefreshToken = jwtUtil.generateRefreshToken(userId, role);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);

                return ResponseEntity.ok(new ApiResponse<>(tokens));
            } else {
                throw new InvalidTokenException("Refresh token is invalid or expired");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value())));
        }
    }

    /**
     * Подтверждение email пользователя.
     *
     * @param token токен подтверждения
     * @return ответ с сообщением о результате
     */
    @Operation(summary = "Подтверждение email", description = "Подтверждает email пользователя по токену.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email успешно подтверждён"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный токен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        try {
            registrationService.verifyUser(token);
            return ResponseEntity.ok(new ApiResponse<>("Email успешно подтверждён"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка подтверждения: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Повторная отправка письма для подтверждения email.
     *
     * @param request запрос с email
     * @return ответ с сообщением о результате
     */
    @Operation(summary = "Повторная отправка письма для подтверждения email", description = "Отправляет повторное письмо для подтверждения email.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Письмо отправлено"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный email или пользователь не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Слишком много запросов"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            Optional<PendingUser> pendingUserOpt = pendingUserRepository.findByEmail(email);
            if (pendingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(new ErrorResponse("Пользователь с таким email не найден", HttpStatus.BAD_REQUEST.value())));
            }

            PendingUser pendingUser = pendingUserOpt.get();
            if (pendingUser.getLastVerificationRequest() != null &&
                    pendingUser.getLastVerificationRequest().plusMinutes(3).isAfter(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new ApiResponse<>(new ErrorResponse("Повторная отправка возможна через 3 минуты", HttpStatus.TOO_MANY_REQUESTS.value())));
            }

            String newToken = UUID.randomUUID().toString();
            pendingUser.setVerificationToken(newToken);
            pendingUser.setLastVerificationRequest(LocalDateTime.now());
            pendingUserRepository.save(pendingUser);

            sendVerificationEmail(email, newToken);
            return ResponseEntity.ok(new ApiResponse<>("Письмо с подтверждением отправлено повторно"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка отправки письма: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Изменение email пользователя.
     *
     * @param request запрос с новыми данными
     * @return ответ с сообщением о результате
     */
    @Operation(summary = "Изменение email", description = "Изменяет email пользователя.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email успешно изменён"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный email или пользователь не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/change-email")
    public ResponseEntity<ApiResponse<String>> changeEmail(@RequestBody Map<String, String> request) {
        try {
            String oldEmail = request.get("oldEmail");
            String newEmail = request.get("newEmail");

            Optional<PendingUser> pendingUserOpt = pendingUserRepository.findByEmail(oldEmail);
            if (pendingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(new ErrorResponse("Пользователь с таким email не найден", HttpStatus.BAD_REQUEST.value())));
            }

            if (pendingUserRepository.findByEmail(newEmail).isPresent() ||
                    listenerRepository.findByEmail(newEmail).isPresent() ||
                    specialistRepository.findByEmail(newEmail).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(new ErrorResponse("Новый email уже зарегистрирован", HttpStatus.BAD_REQUEST.value())));
            }

            PendingUser pendingUser = pendingUserOpt.get();
            pendingUser.setEmail(newEmail);
            String newToken = UUID.randomUUID().toString();
            pendingUser.setVerificationToken(newToken);
            pendingUser.setLastVerificationRequest(LocalDateTime.now());
            pendingUserRepository.save(pendingUser);

            sendVerificationEmail(newEmail, newToken);
            return ResponseEntity.ok(new ApiResponse<>("Email изменён, новое письмо с подтверждением отправлено"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка изменения email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Запрос на сброс пароля.
     *
     * @param request запрос с email
     * @return ответ с сообщением о результате
     */
    @Operation(summary = "Запрос на сброс пароля", description = "Отправляет письмо для сброса пароля.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Письмо отправлено"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный email или пользователь не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String resetToken = authService.requestPasswordReset(email);
            sendPasswordResetEmail(email, resetToken);
            return ResponseEntity.ok(new ApiResponse<>("Письмо для сброса пароля отправлено"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка отправки письма: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Сброс пароля пользователя.
     *
     * @param request запрос с токеном и новым паролем
     * @return ответ с сообщением о результате
     */
    @Operation(summary = "Сброс пароля", description = "Сбрасывает пароль пользователя по токену.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Пароль успешно сброшен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный токен или пароль"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String resetToken = request.get("resetToken");
            String newPassword = request.get("newPassword");
            authService.resetPassword(resetToken, newPassword);
            return ResponseEntity.ok(new ApiResponse<>("Пароль успешно сброшен"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка сброса пароля: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Отправляет письмо с подтверждением регистрации на указанный email.
     *
     * @param email адрес электронной почты
     * @param token токен подтверждения
     */
    @Operation(summary = "Отправка письма с подтверждением регистрации", description = "Отправляет письмо с подтверждением регистрации на указанный email.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Письмо отправлено"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный email"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/send-verification-email")
    private void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Подтверждение регистрации");
        message.setText("Пожалуйста, подтвердите вашу электронную почту, перейдя по ссылке:\n" +
                "http://localhost:8080/auth/verify?token=" + token);
        mailSender.send(message);
    }

    /**
     * Отправляет письмо для сброса пароля на указанный email.
     *
     * @param email адрес электронной почты
     * @param token токен сброса пароля
     */
    @Operation(summary = "Отправка письма для сброса пароля", description = "Отправляет письмо для сброса пароля на указанный email.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Письмо отправлено"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный email"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/send-password-reset-email")
    private void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Сброс пароля");
        message.setText("Для сброса пароля перейдите по ссылке:\n" +
                "http://localhost:8080/auth/reset-password?token=" + token);
        mailSender.send(message);
    }
}