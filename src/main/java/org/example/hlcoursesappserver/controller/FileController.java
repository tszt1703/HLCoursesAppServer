package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            String fileUrl = fileService.uploadProfilePhoto(file, userId, role);
            return ResponseEntity.ok().body("Фото профиля успешно загружено: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при загрузке фото профиля: " + e.getMessage());
        }
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<?> getProfilePhotoUrl(
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        try {
            String fileUrl = fileService.getProfilePhotoUrl(userId, role);
            Map<String, String> response = Map.of("fileUrl", fileUrl);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", "Фото профиля не найдено: " + e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

}
