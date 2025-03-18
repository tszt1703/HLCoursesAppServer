package org.example.hlcoursesappserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Конфигурация валидации для приложения.
 * Предоставляет бин для валидации аннотаций JSR-303/JSR-380.
 */
@Configuration
public class ValidationConfig {

    /**
     * Создаёт бин валидатора для проверки данных на основе аннотаций валидации.
     *
     * @return {@link LocalValidatorFactoryBean} для валидации
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}