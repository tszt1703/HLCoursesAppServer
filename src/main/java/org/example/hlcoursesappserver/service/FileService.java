package org.example.hlcoursesappserver.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.example.hlcoursesappserver.model.Course;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.repository.CourseRepository;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Сервис для работы с файлами, включая загрузку и получение URL файлов на Google Drive.
 */
@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
    private static final String FILE_NAME_REGEX = "[^a-zA-Z0-9]";

    private final Drive driveService;
    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;
    private final CourseRepository courseRepository;

    @Value("${google.drive.profile-photos-folder-id:1fTKb6ARwNhdNVN0fVyXcPsKH4rCdRgdl}")
    private String profilePhotosFolderId;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param driveService          сервис Google Drive
     * @param specialistRepository  репозиторий специалистов
     * @param listenerRepository    репозиторий слушателей
     * @param courseRepository      репозиторий курсов
     */
    @Autowired
    public FileService(Drive driveService,
                       SpecialistRepository specialistRepository,
                       ListenerRepository listenerRepository,
                       CourseRepository courseRepository) {
        this.driveService = driveService;
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Загружает фото профиля пользователя на Google Drive и обновляет URL в базе данных.
     *
     * @param file   файл фото профиля
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public String uploadProfilePhoto(MultipartFile file, Long userId, String role) throws IOException {
        LOGGER.info("Загрузка фото профиля для пользователя ID: {} с ролью: {}", userId, role);

        String email = getUserEmail(userId, role);
        String fileName = generateFileName(email, file.getOriginalFilename());
        String fileUrl = uploadFileToDrive(file, fileName);

        updateUserProfilePhoto(userId, role, fileUrl);
        LOGGER.info("Фото профиля успешно загружено: {}", fileUrl);
        return fileUrl;
    }

    /**
     * Загружает обложку курса на Google Drive и обновляет URL в базе данных.
     *
     * @param file     файл обложки курса
     * @param courseId идентификатор курса
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public String uploadCourseCover(MultipartFile file, Long courseId) throws IOException {
        LOGGER.info("Загрузка обложки для курса ID: {}", courseId);

        String fileName = generateFileName("course_" + courseId, file.getOriginalFilename());
        String fileUrl = uploadFileToDrive(file, fileName);

        updateCourseCover(courseId, fileUrl);
        LOGGER.info("Обложка курса успешно загружена: {}", fileUrl);
        return fileUrl;
    }

    /**
     * Получает URL обложки курса.
     *
     * @param courseId идентификатор курса
     * @return URL обложки курса
     * @throws IllegalArgumentException если курс не найден или обложка не задана
     */
    public String getCourseCoverUrl(Long courseId) {
        LOGGER.debug("Получение URL обложки для курса ID: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        String coverUrl = course.getPhotoUrl();
        if (coverUrl == null || coverUrl.isEmpty()) {
            throw new IllegalArgumentException("Обложка для курса с ID " + courseId + " не задана");
        }
        return coverUrl;
    }

    /**
     * Получает URL фото профиля пользователя.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return URL фото профиля
     * @throws IllegalArgumentException если пользователь или фото не найдены
     */
    public String getProfilePhotoUrl(Long userId, String role) {
        LOGGER.debug("Получение URL фото профиля для пользователя ID: {} с ролью: {}", userId, role);
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

    private String getUserEmail(Long userId, String role) {
        if ("Specialist".equalsIgnoreCase(role)) {
            return specialistRepository.findById(userId)
                    .map(Specialist::getEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Специалист с ID " + userId + " не найден"));
        } else if ("Listener".equalsIgnoreCase(role)) {
            return listenerRepository.findById(userId)
                    .map(Listener::getEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Слушатель с ID " + userId + " не найден"));
        } else {
            throw new IllegalArgumentException("Неизвестная роль: " + role);
        }
    }

    private String generateFileName(String prefix, String originalFileName) {
        return (prefix + "_" + originalFileName).replaceAll(FILE_NAME_REGEX, "_");
    }

    private String uploadFileToDrive(MultipartFile file, String fileName) throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            File fileMetadata = new File()
                    .setName(fileName)
                    .setParents(Collections.singletonList(profilePhotosFolderId));
            FileContent mediaContent = new FileContent(file.getContentType(), tempFile.toFile());

            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink")
                    .execute();

            return uploadedFile.getWebViewLink();
        } finally {
            if (tempFile != null && Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
        }
    }

    private void updateUserProfilePhoto(Long userId, String role, String fileUrl) {
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
    }

    private void updateCourseCover(Long courseId, String fileUrl) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        course.setPhotoUrl(fileUrl);
        courseRepository.save(course);
    }
}