package org.example.hlcoursesappserver.util;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Отправка ошибки 401 при ошибке аутентификации
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication token is either missing or invalid.");
    }
}

