package org.example.hlcoursesappserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настраивает обработку статических ресурсов.
     * Добавляет обработчик ресурсов для загрузок файлов.
     *
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/User/IdeaProjects/HLCoursesAppServer/src/main/resources/uploads/");
    }
}
