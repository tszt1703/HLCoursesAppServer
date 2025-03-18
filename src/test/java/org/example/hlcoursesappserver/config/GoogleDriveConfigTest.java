package org.example.hlcoursesappserver.config;

import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
class GoogleDriveConfigTest {

    @Autowired
    private Drive driveService;

    // Мокаем зависимости Google Drive, чтобы избежать реальных вызовов
    @MockBean
    private Drive.Files files;

    @Test
    void testDriveServiceBeanCreation() {
        assertNotNull(driveService, "Drive сервис должен быть создан");
        assertNotNull(driveService.files(), "Drive.Files должен быть доступен");
    }
}