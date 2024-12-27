package org.example.hlcoursesappserver.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.example.hlcoursesappserver.config.GoogleDriveConfig;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@Service
public class FileService {

    private final Drive driveService;
    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;
    private static final String PROFILE_PHOTOS_FOLDER_ID = "1fTKb6ARwNhdNVN0fVyXcPsKH4rCdRgdl";

    @Autowired
    public FileService(SpecialistRepository specialistRepository,
                       ListenerRepository listenerRepository) throws Exception {
        this.driveService = GoogleDriveConfig.getDriveService();
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
    }

    public String uploadProfilePhoto(MultipartFile file, Long userId, String role) throws IOException {
        // Получаем email пользователя
        String email;
        if ("Specialist".equalsIgnoreCase(role)) {
            Specialist specialist = specialistRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Специалист с ID " + userId + " не найден"));
            email = specialist.getEmail();
        } else if ("Listener".equalsIgnoreCase(role)) {
            Listener listener = listenerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Слушатель с ID " + userId + " не найден"));
            email = listener.getEmail();
        } else {
            throw new IllegalArgumentException("Неизвестная роль: " + role);
        }

        // Создаем уникальное имя файла с email пользователя
        String fileName = email.replaceAll("[^a-zA-Z0-9]", "_") + "_" + file.getOriginalFilename();

        // Метаданные файла
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(PROFILE_PHOTOS_FOLDER_ID)); // Указываем ID папки

        // Создаём временный файл для загрузки
        java.io.File tempFile = java.io.File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        FileContent mediaContent = new FileContent(file.getContentType(), tempFile);

        // Загружаем файл на Google Drive
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        String fileUrl = uploadedFile.getWebViewLink();

        // Сохраняем URL файла в БД
        if ("Specialist".equalsIgnoreCase(role)) {
            Specialist specialist = specialistRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Специалист с ID " + userId + " не найден"));
            specialist.setProfilePhotoUrl(fileUrl);
            specialistRepository.save(specialist);
        } else if ("Listener".equalsIgnoreCase(role)) {
            Listener listener = listenerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Слушатель с ID " + userId + " не найден"));
            listener.setProfilePhotoUrl(fileUrl);
            listenerRepository.save(listener);
        }

        // Удаляем временный файл
        if (tempFile.exists()) {
            tempFile.delete();
        }

        return fileUrl;
    }


    public String getProfilePhotoUrl(Long userId, String role) {
        if ("Specialist".equalsIgnoreCase(role)) {
            return specialistRepository.findById(userId)
                    .map(Specialist::getProfilePhotoUrl)
                    .orElseThrow(() -> new IllegalArgumentException("Фото профиля специалиста не найдено"));
        } else if ("Listener".equalsIgnoreCase(role)) {
            return listenerRepository.findById(userId)
                    .map(Listener::getProfilePhotoUrl)
                    .orElseThrow(() -> new IllegalArgumentException("Фото профиля слушателя не найдено"));
        } else {
            throw new IllegalArgumentException("Неизвестная роль: " + role);
        }
    }
}


