package org.example.hlcoursesappserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация приложения Spring Boot.
 * Настраивает параметры веб-приложения, включая CORS.
 */
@Configuration
public class AppConfig {

    private static final String[] ALLOWED_METHODS = {"GET", "POST", "PUT", "DELETE"};
    private static final String ALL_PATHS = "/**";
    private static final String ALL_HEADERS = "*";

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    /**
     * Создаёт и настраивает бин для конфигурации CORS.
     *
     * @return объект {@link WebMvcConfigurer} с настройками CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(ALL_PATHS)
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods(ALLOWED_METHODS)
                        .allowedHeaders(ALL_HEADERS);
            }
        };
    }
}