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
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String jwtError = (String) request.getAttribute("jwtError");
        String errorMessage = jwtError != null ? "Invalid token: " + jwtError : "Unauthorized: Token is missing or invalid.";
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }


}

