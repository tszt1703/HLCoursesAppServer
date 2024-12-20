package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.service.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUploadProfilePhoto() throws Exception {
        Long userId = 2L;
        String role = "Specialist";
        String fileUrl = "https://drive.google.com/file/d/12345/view";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "profile-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test content".getBytes()
        );

        // Мокируем работу сервиса
        Mockito.when(fileService.uploadProfilePhoto(mockFile, userId, role)).thenReturn(fileUrl);

        // Выполняем тест
        mockMvc.perform(multipart("/api/files/upload-profile-photo")
                        .file(mockFile)
                        .with(csrf()) // Добавляем CSRF токен
                        .param("userId", userId.toString())
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(content().string("Фото профиля успешно загружено: " + fileUrl));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUploadProfilePhotoError() throws Exception {
        Long userId = 2L;
        String role = "Specialist";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "profile-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test content".getBytes()
        );

        // Мокируем ошибку в сервисе
        Mockito.when(fileService.uploadProfilePhoto(mockFile, userId, role))
                .thenThrow(new IOException("Ошибка при загрузке файла"));

        // Выполняем тест
        mockMvc.perform(multipart("/api/files/upload-profile-photo")
                        .file(mockFile)
                        .with(csrf()) // Добавляем CSRF токен
                        .param("userId", userId.toString())
                        .param("role", role))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Ошибка при загрузке фото профиля: Ошибка при загрузке файла"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetProfilePhotoUrl() throws Exception {
        Long userId = 2L;
        String role = "Specialist";
        String fileUrl = "https://drive.google.com/file/d/12345/view";

        Mockito.when(fileService.getProfilePhotoUrl(userId, role)).thenReturn(fileUrl);

        mockMvc.perform(get("/api/files/profile-photo")
                        .param("userId", userId.toString())
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(content().string(fileUrl));
    }

}
