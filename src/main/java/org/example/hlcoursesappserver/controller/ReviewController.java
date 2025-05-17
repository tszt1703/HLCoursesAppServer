package org.example.hlcoursesappserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.hlcoursesappserver.dto.ApiResponse;
import org.example.hlcoursesappserver.dto.ErrorResponse;
import org.example.hlcoursesappserver.dto.ReviewRequestDTO;
import org.example.hlcoursesappserver.dto.ReviewResponseDTO;
import org.example.hlcoursesappserver.exception.InvalidTokenException;
import org.example.hlcoursesappserver.service.ReviewService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Создает новый отзыв.
     *
     * @param token   JWT-токен пользователя
     * @param request данные отзыва
     * @return созданный отзыв
     */
    @Operation(summary = "Создание отзыва", description = "Создает новый отзыв для курса")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Отзыв успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(@RequestHeader("Authorization") String token,
                                                                       @Valid @RequestBody ReviewRequestDTO request) {
        try {
            Long listenerId = validateListenerToken(token);
            ReviewResponseDTO response = reviewService.createReview(listenerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(response));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param token     JWT-токен пользователя
     * @param reviewId  ID отзыва
     * @param request   данные отзыва
     * @return обновленный отзыв
     */
    @Operation(summary = "Обновление отзыва", description = "Обновляет существующий отзыв для курса")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Отзыв успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(@RequestHeader("Authorization") String token,
                                                                       @PathVariable Long reviewId,
                                                                       @Valid @RequestBody ReviewRequestDTO request) {
        try {
            Long listenerId = validateListenerToken(token);
            ReviewResponseDTO response = reviewService.updateReview(listenerId, reviewId, request);
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@RequestHeader("Authorization") String token,
                                                          @PathVariable Long reviewId) {
        try {
            Long listenerId = validateListenerToken(token);
            reviewService.deleteReview(listenerId, reviewId);
            return ResponseEntity.ok(new ApiResponse<>(null));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Получает отзыв по ID курса.
     *
     * @param token    JWT-токен пользователя
     * @param courseId ID курса
     * @return отзыв
     */
    @Operation(summary = "Получение отзыва по ID курса", description = "Получает отзыв для курса по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Отзыв успешно найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> getReviewByCourse(@RequestHeader("Authorization") String token,
                                                                            @PathVariable Long courseId) {
        try {
            Long listenerId = validateListenerToken(token);
            Optional<ReviewResponseDTO> review = reviewService.getReviewByListenerAndCourse(listenerId, courseId);
            return review.map(data -> ResponseEntity.ok(new ApiResponse<>(data)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(new ErrorResponse("Отзыв не найден", HttpStatus.NOT_FOUND.value()))));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Получает отзыв по ID.
     *
     * @param token    JWT-токен пользователя
     * @param reviewId ID отзыва
     * @return отзыв
     */
    @Operation(summary = "Получение отзыва по ID", description = "Получает отзыв по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Отзыв успешно найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> getReviewById(@RequestHeader("Authorization") String token,
                                                                        @PathVariable Long reviewId) {
        try {
            Long listenerId = validateListenerToken(token);
            Optional<ReviewResponseDTO> review = reviewService.getReviewByIdAndListener(listenerId, reviewId);
            return review.map(data -> ResponseEntity.ok(new ApiResponse<>(data)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(new ErrorResponse("Отзыв не найден", HttpStatus.NOT_FOUND.value()))));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Получает все отзывы по ID курса.
     *
     * @param token    JWT-токен пользователя
     * @param courseId ID курса
     * @return список отзывов
     */
    @Operation(summary = "Получение всех отзывов по ID курса", description = "Получает все отзывы для курса по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Отзывы успешно найдены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Отзывы не найдены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/course/{courseId}/all")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getAllReviewsByCourse(@RequestHeader("Authorization") String token,
                                                                                      @PathVariable Long courseId) {
        try {
            validateToken(token); // Проверяем токен, но допускаем роли Listener или Specialist
            List<ReviewResponseDTO> reviews = reviewService.getAllReviewsByCourse(courseId);
            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(new ErrorResponse("Отзывы для курса не найдены", HttpStatus.NOT_FOUND.value())));
            }
            return ResponseEntity.ok(new ApiResponse<>(reviews));
        } catch (IllegalArgumentException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(new ErrorResponse("Ошибка: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    /**
     * Получает все отзывы по ID слушателя.
     *
     * @param token        JWT-токен пользователя
     * @return список отзывов
     */
    @Operation(summary = "Получение всех отзывов по ID слушателя", description = "Получает все отзывы для слушателя по его ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Отзывы успешно найдены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Отзывы не найдены"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/listener")
    private Long validateListenerToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Токен отсутствует или некорректен");
        }
        String jwt = token.substring(7);
        try {
            if (!jwtUtil.validateToken(jwt)) {
                throw new IllegalArgumentException("Недействительный токен");
            }
            String role = jwtUtil.extractRole(jwt);
            if (!"Listener".equals(role)) {
                throw new IllegalArgumentException("Только слушатели могут выполнять это действие");
            }
            return jwtUtil.extractUserId(jwt);
        } catch (InvalidTokenException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Проверяет токен для получения всех отзывов.
     *
     * @param token JWT-токен пользователя
     */
    @Operation(summary = "Проверка токена для получения всех отзывов", description = "Проверяет токен для получения всех отзывов")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Токен валиден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректный токен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения действия")
    })
    @GetMapping("/listener/all")
    private void validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Токен отсутствует или некорректен");
        }
        String jwt = token.substring(7);
        try {
            if (!jwtUtil.validateToken(jwt)) {
                throw new IllegalArgumentException("Недействительный токен");
            }
            String role = jwtUtil.extractRole(jwt);
            if (!"Listener".equals(role) && !"Specialist".equals(role)) {
                throw new IllegalArgumentException("Только слушатели или специалисты могут выполнять это действие");
            }
            jwtUtil.extractUserId(jwt);
        } catch (InvalidTokenException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}