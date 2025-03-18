package org.example.hlcoursesappserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hlcoursesappserver.service.CustomUserDetailsService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для проверки и обработки JWT-токенов в запросах.
 * Извлекает токен из заголовка Authorization, валидирует его и устанавливает аутентификацию в контексте Spring Security.
 */
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Конструктор фильтра с внедрением зависимостей.
     *
     * @param jwtUtil            утилита для работы с JWT-токенами
     * @param userDetailsService сервис для загрузки данных пользователя
     */
    public JwtRequestFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Обрабатывает входящий запрос, извлекает и валидирует JWT-токен, устанавливает аутентификацию.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @param chain    цепочка фильтров
     * @throws ServletException если произошла ошибка сервлета
     * @throws IOException      если произошла ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTH_HEADER);
        String jwt = null;
        Long userId = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                jwt = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
                userId = jwtUtil.extractUserId(jwt);
                LOGGER.debug("Извлечён JWT для userId: {}", userId);
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUserId(userId, jwtUtil.extractRole(jwt));

                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    LOGGER.debug("Аутентификация установлена для userId: {}", userId);

                    request.setAttribute("userId", userId);
                    request.setAttribute("role", jwtUtil.extractRole(jwt));
                } else {
                    LOGGER.warn("Невалидный JWT-токен для userId: {}", userId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка обработки JWT-токена: {}", e.getMessage());
            request.setAttribute("jwtError", e.getMessage());
        }

        chain.doFilter(request, response);
    }
}