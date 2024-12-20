package org.example.hlcoursesappserver.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.example.hlcoursesappserver.config.GoogleDriveConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest {

    @Mock
    private GoogleDriveConfig googleDriveConfig; // Мокируем GoogleDriveConfig

    @Mock
    private Drive driveService; // Мокируем сервис Google Drive

    @InjectMocks
    private FileService fileService; // Тестируемый сервис

    @Mock
    private MultipartFile multipartFile; // Мокируем MultipartFile

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Мокаем метод в GoogleDriveConfig, который возвращает Drive
        when(googleDriveConfig.getDriveService()).thenReturn(driveService);
    }

    @Test
    public void testUploadFile() throws Exception {
        // Мокаем поведение Google Drive API
        File fileMetadata = new File();
        fileMetadata.setName("testFile.txt");

        // Мокаем объект ответа от Google Drive
        File uploadedFile = new File();
        uploadedFile.setId("fileId");
        uploadedFile.setWebViewLink("https://drive.google.com/file/d/fileId/view");

        // Мокаем загрузку файла
        when(multipartFile.getOriginalFilename()).thenReturn("testFile.txt");
        when(multipartFile.getInputStream()).thenReturn(new FileInputStream("path/to/test/file"));

        // Мокаем создание файла в Google Drive
        when(driveService.files().create(any(File.class), any(FileContent.class)))
                .thenReturn(mock(Drive.Files.Create.class));
        when(driveService.files().create(any(File.class), any(FileContent.class)).execute())
                .thenReturn(uploadedFile);

        // Выполняем тест
        String fileLink = fileService.uploadFile(multipartFile);

        // Проверяем, что ссылка на файл была возвращена
        assertEquals("https://drive.google.com/file/d/fileId/view", fileLink);

        // Проверяем, что метод uploadFile был вызван
        verify(driveService.files().create(any(File.class), any(FileContent.class)), times(1)).execute();
    }
}
