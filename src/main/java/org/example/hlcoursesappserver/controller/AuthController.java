package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.*;
import org.example.hlcoursesappserver.service.AuthenticationService;
import org.example.hlcoursesappserver.service.RegistrationService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RegistrationService registrationService;

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationService authenticationService, JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
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
        CustomAuthentication authentication = authenticationService.authenticateUser(request);
        if (authentication != null) {
            String token = jwtUtil.generateToken(authentication.getUserId().toString(), authentication.getRole());
            return ResponseEntity.ok(new LoginResponse(authentication.getUserId(), authentication.getRole(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неправильный email или пароль.");
        }
    }
}
