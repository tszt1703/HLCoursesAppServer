package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Конструктор контроллера пользователей.
     *
     * @param specialistService сервис для работы со специалистами
     * @param listenerService   сервис для работы со слушателями
     * @param userMapper        маппер для преобразования между сущностями и DTO
     */
    @Autowired
    public UserController(SpecialistService specialistService, ListenerService listenerService, UserMapper userMapper) {
        this.specialistService = specialistService;
        this.listenerService = listenerService;
        this.userMapper = userMapper;
    }

//    @PostMapping("/specialist")
//    public ResponseEntity<UserDTO> createSpecialist(@RequestBody UserDTO userDTO) {
//        Specialist specialist = userMapper.toSpecialist(userDTO);
//        Specialist createdSpecialist = specialistService.createUser(specialist);
//        return ResponseEntity.ok(userMapper.toUserDTO(createdSpecialist, "Specialist"));
//    }
//
//    @PostMapping("/listener")
//    public ResponseEntity<UserDTO> createListener(@RequestBody UserDTO userDTO) {
//        Listener listener = userMapper.toListener(userDTO);
//        Listener createdListener = listenerService.createUser(listener);
//        return ResponseEntity.ok(userMapper.toUserDTO(createdListener, "Listener"));
//    }


    /**
     * Получает всех пользователей системы.
     *
     * @return список всех пользователей
     */
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей системы")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<Specialist> specialists = specialistService.getAllUsers();
        List<Listener> listeners = listenerService.getAllUsers();
        List<UserDTO> users = userMapper.toUserDTOList(specialists, listeners);
        return ResponseEntity.ok(users);
    }

    /**
     * Получает пользователя по его email.
     *
     * @param email email пользователя
     * @return пользователь с указанным email
     */
    @Operation(summary = "Получить пользователя по email", description = "Возвращает пользователя по его email")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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

    /**
     * Получает пользователя по его ID.
     *
     * @param id   ID пользователя
     * @param role Роль пользователя (Specialist или Listener)
     * @return пользователь с указанным ID
     */
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable Long id,
            @RequestParam String role) {
        logger.info("Получен запрос на получение пользователя с ID: {} и ролью: {}", id, role);

        if (role.equals("Specialist")) {
            try {
                Specialist specialist = specialistService.getUserById(id);
                return ResponseEntity.ok(userMapper.toUserDTO(specialist, "Specialist"));
            } catch (RuntimeException e) {
                logger.warn("Специалист с ID: {} не найден", id);
                return ResponseEntity.notFound().build();
            }
        } else if (role.equals("Listener")) {
            try {
                Listener listener = listenerService.getUserById(id);
                return ResponseEntity.ok(userMapper.toUserDTO(listener, "Listener"));
            } catch (RuntimeException e) {
                logger.warn("Слушатель с ID: {} не найден", id);
                return ResponseEntity.notFound().build();
            }
        }

        logger.warn("Некорректная роль: {} для ID: {}", role, id);
        return ResponseEntity.badRequest().body(null);
    }

    // Метод для обновления пользователя
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Нет прав на обновление"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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
    @Operation(summary = "Обновить email", description = "Обновляет email пользователя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Нет прав на обновление"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/{id}/email")
    public ResponseEntity<?> updateEmail(
            @PathVariable Long id,
            @RequestBody String newEmail,
            @RequestHeader("email") String currentEmail,
            @RequestParam String role) {
        logger.info("Получен новый email: {}", newEmail); // Логируем входные данные

        // Очищаем кавычки, если они есть
        String cleanedEmail = newEmail.replace("\"", "").trim();

        if (role.equals("Specialist")) {
            if (!specialistService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }
            specialistService.updateEmail(id, cleanedEmail);
            return ResponseEntity.ok(Map.of("message", "Email успешно обновлен."));
        } else if (role.equals("Listener")) {
            if (!listenerService.isUserAuthorizedToUpdate(id, currentEmail)) {
                return ResponseEntity.status(403).build();
            }
            listenerService.updateEmail(id, cleanedEmail);
            return ResponseEntity.ok(Map.of("message", "Email успешно обновлен."));
        }
        return ResponseEntity.badRequest().build();
    }

    // Метод для обновления пароля
    @Operation(summary = "Обновить пароль", description = "Обновляет пароль пользователя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Нет прав на обновление"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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

    // Метод для удаления пользователя
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Нет прав на удаление"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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
