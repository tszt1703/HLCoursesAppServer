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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class GoogleDriveConfig {

    private static final String APPLICATION_NAME = "HLCoursesServer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveConfig.class);

    public static Drive getDriveService() throws Exception {
        logger.info("Initializing Google Drive Service...");

        // Проверка существования credentials.json
        InputStream in = GoogleDriveConfig.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            logger.error("File credentials.json not found in resources.");
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        // Загружаем данные клиента из credentials.json
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Строим поток авторизации
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // Авторизация пользователя
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8080) // Укажите тот же порт, что в Google Cloud Console
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");


        // Возвращаем объект Drive
        Drive drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        logger.info("Google Drive Service initialized successfully.");
        return drive;
    }
}
