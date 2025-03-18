package org.example.hlcoursesappserver.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Конфигурация интеграции с Google Drive API.
 * Предоставляет бин {@link Drive} для работы с Google Drive.
 */
@Configuration
public class GoogleDriveConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveConfig.class);
    private static final String APPLICATION_NAME = "HLCoursesServer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String USER_IDENTIFIER = "user";

    @Value("${google.drive.credentials.path:/credentials.json}")
    private String credentialsFilePath;

    @Value("${google.drive.tokens.directory:tokens}")
    private String tokensDirectoryPath;

    @Value("${google.drive.auth.port:8080}")
    private int authPort;

    /**
     * Создаёт и возвращает настроенный экземпляр {@link Drive} для работы с Google Drive API.
     *
     * @return объект {@link Drive} для взаимодействия с Google Drive
     * @throws IOException если произошла ошибка ввода-вывода при загрузке учетных данных
     * @throws GeneralSecurityException если произошла ошибка безопасности при создании HTTP-транспорта
     */
    @Bean
    public Drive googleDriveService() throws IOException, GeneralSecurityException {
        LOGGER.info("Инициализация сервиса Google Drive");

        GoogleClientSecrets clientSecrets = loadClientSecrets();
        NetHttpTransport httpTransport = createHttpTransport();
        GoogleAuthorizationCodeFlow flow = buildAuthorizationFlow(clientSecrets, httpTransport);
        Credential credential = authorize(flow);

        Drive drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        LOGGER.info("Сервис Google Drive успешно инициализирован");
        return drive;
    }

    private GoogleClientSecrets loadClientSecrets() throws IOException {
        InputStream in = GoogleDriveConfig.class.getResourceAsStream(credentialsFilePath);
        if (in == null) {
            LOGGER.error("Файл учетных данных не найден по пути: {}", credentialsFilePath);
            throw new FileNotFoundException("Файл учетных данных не найден: " + credentialsFilePath);
        }
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    private NetHttpTransport createHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    private GoogleAuthorizationCodeFlow buildAuthorizationFlow(GoogleClientSecrets clientSecrets,
                                                               NetHttpTransport httpTransport) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDirectoryPath)))
                .setAccessType("offline")
                .build();
    }

    private Credential authorize(GoogleAuthorizationCodeFlow flow) throws IOException {
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(authPort)
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(USER_IDENTIFIER);
    }
}