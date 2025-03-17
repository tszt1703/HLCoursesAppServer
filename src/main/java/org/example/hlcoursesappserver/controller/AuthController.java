package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.exception.InvalidTokenException;
import org.example.hlcoursesappserver.service.AuthService;
import org.example.hlcoursesappserver.service.RegistrationService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, RegistrationService registrationService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            UserDTO user = registrationService.registerUser(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            CustomAuthentication authentication = authService.authenticateUser(request);

            String accessToken = jwtUtil.generateAccessToken(authentication.getUserId(), authentication.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(authentication.getUserId(), authentication.getRole());

            return ResponseEntity.ok(new LoginResponse(authentication.getUserId(), authentication.getRole(), accessToken, refreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (jwtUtil.validateRefreshToken(refreshToken)) {
                Long userId = jwtUtil.extractUserId(refreshToken);
                String role = jwtUtil.extractRole(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(userId, role);
                String newRefreshToken = jwtUtil.generateRefreshToken(userId, role);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);

                return ResponseEntity.ok(tokens);
            } else {
                throw new InvalidTokenException("Refresh token is invalid or expired");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}