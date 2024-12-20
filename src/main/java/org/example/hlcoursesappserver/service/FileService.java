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

@Service
public class FileService {

    private final Drive driveService;
    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;

    @Autowired
    public FileService(SpecialistRepository specialistRepository,
                       ListenerRepository listenerRepository) throws Exception {
        this.driveService = GoogleDriveConfig.getDriveService();
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
    }

    public String uploadProfilePhoto(MultipartFile file, Long userId, String role) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName("HLCoursesAppFiles/ProfilePhotos/" + file.getOriginalFilename());

        java.io.File tempFile = java.io.File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        FileContent mediaContent = new FileContent(file.getContentType(), tempFile);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        String fileUrl = uploadedFile.getWebViewLink();

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
        } else {
            throw new IllegalArgumentException("Неизвестная роль: " + role);
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
