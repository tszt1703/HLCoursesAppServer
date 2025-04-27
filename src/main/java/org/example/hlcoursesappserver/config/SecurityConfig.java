package org.example.hlcoursesappserver.config;

import org.example.hlcoursesappserver.service.CustomUserDetailsService;
import org.example.hlcoursesappserver.util.JwtAuthenticationEntryPoint;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурация безопасности приложения.
 * Настраивает Spring Security для использования JWT-аутентификации и stateless-сессий.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/auth/verify",
            "/auth/resend-verification",
            "/auth/change-email",
            "/auth/forgot-password", // Добавлен эндпоинт для запроса восстановления пароля
            "/auth/reset-password"   // Добавлен эндпоинт для сброса пароля
    };
    private static final String PUBLIC_GET_PATTERN = "/public/**";

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${security.public-endpoints:}")
    private String[] additionalPublicEndpoints;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param jwtUtil                  утилита для работы с JWT
     * @param userDetailsService       сервис загрузки данных пользователей
     * @param jwtAuthenticationEntryPoint обработчик ошибок аутентификации
     */
    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * Создаёт бин для шифрования паролей.
     *
     * @return {@link PasswordEncoder} с использованием BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт бин менеджера аутентификации.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return {@link AuthenticationManager}
     * @throws Exception если конфигурация некорректна
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Настраивает цепочку фильтров безопасности.
     *
     * @param http объект для настройки HTTP-безопасности
     * @return настроенная цепочка фильтров {@link SecurityFilterChain}
     * @throws Exception если настройка не удалась
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Разрешаем все OPTIONS-запросы
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(additionalPublicEndpoints).permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_GET_PATTERN).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new JwtRequestFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}