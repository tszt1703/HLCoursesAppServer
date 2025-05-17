package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.service.ListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/listeners")
public class ListenerController {

    private static final Logger logger = LoggerFactory.getLogger(ListenerController.class);

    private final ListenerService listenerService;

    @Autowired
    public ListenerController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    /**
     * Добавляет курс в избранное для слушателя.
     *
     * @param listenerId идентификатор слушателя
     * @param courseId   идентификатор курса
     * @param authenticatedUserId идентификатор аутентифицированного пользователя
     * @return ответ с сообщением об успешном добавлении курса в избранное
     */
    @Operation(summary = "Добавить курс в избранное", description = "Добавляет курс в избранное для слушателя")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно добавлен в избранное"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка при добавлении курса в избранное"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Несанкционированная попытка добавить курс в избранное другого пользователя")
    })
    @PostMapping("/{listenerId}/favorites/{courseId}")
    public ResponseEntity<?> addCourseToFavorites(
            @PathVariable Long listenerId,
            @PathVariable Long courseId,
            @RequestHeader("userId") Long authenticatedUserId) {
        logger.info("Запрос на добавление курса ID: {} в избранное слушателя ID: {}", courseId, listenerId);

        if (!listenerId.equals(authenticatedUserId)) {
            logger.error("Несанкционированная попытка добавить курс в избранное для другого пользователя");
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized action"));
        }

        try {
            listenerService.addCourseToFavorites(listenerId, courseId);
            return ResponseEntity.ok(Map.of("message", "Курс добавлен в избранное"));
        } catch (RuntimeException e) {
            logger.error("Ошибка при добавлении курса в избранное: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Удаляет курс из избранного для слушателя.
     *
     * @param listenerId идентификатор слушателя
     * @param courseId   идентификатор курса
     * @param authenticatedUserId идентификатор аутентифицированного пользователя
     * @return ответ с сообщением об успешном удалении курса из избранного
     */
    @Operation(summary = "Удалить курс из избранного", description = "Удаляет курс из избранного для слушателя")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Курс успешно удалён из избранного"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка при удалении курса из избранного"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Несанкционированная попытка удалить курс из избранного другого пользователя")
    })
    @DeleteMapping("/{listenerId}/favorites/{courseId}")
    public ResponseEntity<?> removeCourseFromFavorites(
            @PathVariable Long listenerId,
            @PathVariable Long courseId,
            @RequestHeader("userId") Long authenticatedUserId) {
        logger.info("Запрос на удаление курса ID: {} из избранного слушателя ID: {}", courseId, listenerId);

        if (!listenerId.equals(authenticatedUserId)) {
            logger.error("Несанкционированная попытка удалить курс из избранного другого пользователя");
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized action"));
        }

        try {
            listenerService.removeCourseFromFavorites(listenerId, courseId);
            return ResponseEntity.ok(Map.of("message", "Курс удалён из избранного"));
        } catch (RuntimeException e) {
            logger.error("Ошибка при удалении курса из избранного: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Получает список избранных курсов для слушателя.
     *
     * @param listenerId идентификатор слушателя
     * @param authenticatedUserId идентификатор аутентифицированного пользователя
     * @return список избранных курсов
     */
    @Operation(summary = "Получить список избранных курсов", description = "Получает список избранных курсов для слушателя")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список избранных курсов успешно получен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Несанкционированная попытка получить избранное другого пользователя")
    })
    @GetMapping("/{listenerId}/favorites")
    public ResponseEntity<List<Course>> getFavoriteCourses(
            @PathVariable Long listenerId,
            @RequestHeader("userId") Long authenticatedUserId) {
        logger.info("Запрос списка избранных курсов для слушателя ID: {}", listenerId);

        if (!listenerId.equals(authenticatedUserId)) {
            logger.error("Несанкционированная попытка получить избранное другого пользователя");
            return ResponseEntity.status(403).body(null);
        }

        List<Course> favorites = listenerService.getFavoriteCourses(listenerId);
        return ResponseEntity.ok(favorites);
    }
}