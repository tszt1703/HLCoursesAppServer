package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.mapper.UserMapper;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final SpecialistService specialistService;
    private final ListenerService listenerService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(SpecialistService specialistService, ListenerService listenerService, UserMapper userMapper) {
        this.specialistService = specialistService;
        this.listenerService = listenerService;
        this.userMapper = userMapper;
    }

    @PostMapping("/specialist")
    public ResponseEntity<UserDTO> createSpecialist(@RequestBody UserDTO userDTO) {
        Specialist specialist = userMapper.toSpecialist(userDTO);
        Specialist createdSpecialist = specialistService.createUser(specialist);
        return ResponseEntity.ok(userMapper.toUserDTO(createdSpecialist, "Specialist"));
    }

    @PostMapping("/listener")
    public ResponseEntity<UserDTO> createListener(@RequestBody UserDTO userDTO) {
        Listener listener = userMapper.toListener(userDTO);
        Listener createdListener = listenerService.createUser(listener);
        return ResponseEntity.ok(userMapper.toUserDTO(createdListener, "Listener"));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<Specialist> specialists = specialistService.getAllUsers();
        List<Listener> listeners = listenerService.getAllUsers();
        List<UserDTO> users = userMapper.toUserDTOList(specialists, listeners);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<Specialist> specialistOpt = specialistService.getUserByEmail(email);
        if (specialistOpt.isPresent()) {
            Specialist specialist = specialistOpt.get();
            return ResponseEntity.ok(userMapper.toUserDTO(specialist, "Specialist"));
        }

        Optional<Listener> listenerOpt = listenerService.getUserByEmail(email);
        if (listenerOpt.isPresent()) {
            Listener listener = listenerOpt.get();
            return ResponseEntity.ok(userMapper.toUserDTO(listener, "Listener"));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO,
            @RequestHeader("email") String email) {
        // Логирование входящих данных
        logger.info("Получен запрос на обновление пользователя с ID: {}. Данные: {}", id, userDTO);

        // Проверка наличия роли
        if (userDTO.getRole() == null || userDTO.getRole().isEmpty()) {
            logger.warn("Роль пользователя не указана для ID: {}", id);
            return ResponseEntity.badRequest().body("Ошибка: Роль пользователя не указана.");
        }

        // Обработка обновления специалиста
        if (userDTO.getRole().equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToUpdate(id, email)) {
                logger.warn("Попытка обновления специалиста без авторизации. ID: {}, email: {}", id, email);
                return ResponseEntity.status(403).body("У вас нет прав на обновление этого пользователя.");
            }
            try {
                Specialist updatedSpecialist = userMapper.toSpecialist(userDTO);
                specialistService.updateUser(id, updatedSpecialist);
                logger.info("Специалист с ID: {} успешно обновлён.", id);
                return ResponseEntity.ok("Специалист успешно обновлён.");
            } catch (Exception e) {
                logger.error("Ошибка при обновлении специалиста с ID: {}. Ошибка: {}", id, e.getMessage());
                return ResponseEntity.status(500).body("Ошибка при обновлении специалиста.");
            }
        }

        // Обработка обновления слушателя
        else if (userDTO.getRole().equals("Listener")) {
            if (!listenerService.isUserAuthorizedToUpdate(id, email)) {
                logger.warn("Попытка обновления слушателя без авторизации. ID: {}, email: {}", id, email);
                return ResponseEntity.status(403).body("У вас нет прав на обновление этого пользователя.");
            }
            try {
                Listener updatedListener = userMapper.toListener(userDTO);
                listenerService.updateUser(id, updatedListener);
                logger.info("Слушатель с ID: {} успешно обновлён.", id);
                return ResponseEntity.ok("Слушатель успешно обновлён.");
            } catch (Exception e) {
                logger.error("Ошибка при обновлении слушателя с ID: {}. Ошибка: {}", id, e.getMessage());
                return ResponseEntity.status(500).body("Ошибка при обновлении слушателя.");
            }
        }

        // Если роль не распознана
        logger.warn("Попытка обновления пользователя с некорректной ролью. ID: {}, роль: {}", id, userDTO.getRole());
        return ResponseEntity.badRequest().body("Ошибка: Неверная роль пользователя.");
    }


    // Метод для обновления email
    @PutMapping("/{id}/email")
    public ResponseEntity<?> updateEmail(
            @PathVariable Long id,
            @RequestBody String newEmail,
            @RequestHeader("email") String currentEmail,
            @RequestParam String role) {

        if (role.equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }

            Map<String, String> tokens = specialistService.updateEmail(id, newEmail);

            return ResponseEntity.ok(Map.of(
                    "message", "Email успешно обновлен.",
                    "accessToken", tokens.get("accessToken"),
                    "refreshToken", tokens.get("refreshToken")
            ));
        } else if (role.equals("Listener")) {
            if (!listenerService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }

            Map<String, String> tokens = listenerService.updateEmail(id, newEmail);

            return ResponseEntity.ok(Map.of(
                    "message", "Email успешно обновлен.",
                    "accessToken", tokens.get("accessToken"),
                    "refreshToken", tokens.get("refreshToken")
            ));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords, // Передаём оба пароля через JSON
            @RequestHeader("email") String currentEmail,
            @RequestParam String role) {

        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");

        if (role.equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }
            specialistService.updatePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Пароль успешно обновлен."));
        } else if (role.equals("Listener")) {
            if (!listenerService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }
            listenerService.updatePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Пароль успешно обновлен."));
        }
        return ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader("email") String email,
            @RequestParam String role) {
        if (role.equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToDelete(id, email)) {
                return ResponseEntity.status(403).build();
            }
            specialistService.deleteUser(id);
            return ResponseEntity.ok().build();
        } else if (role.equals("Listener")) {
            if (!listenerService.isUserAuthorizedToDelete(id, email)) {
                return ResponseEntity.status(403).build();
            }
            listenerService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
