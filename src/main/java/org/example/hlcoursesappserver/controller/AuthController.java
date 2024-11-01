package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.AuthRequest;
import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    private final AuthService authService;
//
//    @Autowired
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
//        return authService.authenticate(request);
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
//        return ResponseEntity.ok(authService.register(userDTO));
//    }
}
