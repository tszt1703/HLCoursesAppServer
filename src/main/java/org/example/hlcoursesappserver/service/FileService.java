package org.example.hlcoursesappserver.service;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.example.hlcoursesappserver.model.*;
import org.example.hlcoursesappserver.repository.*;
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
import java.util.List;

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
    private final LessonRepository lessonRepository;
    private final LessonFileRepository lessonFileRepository;

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
                       CourseRepository courseRepository, LessonRepository lessonRepository, LessonFileRepository lessonFileRepository) {
        this.driveService = driveService;
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.lessonFileRepository = lessonFileRepository;
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

    /**
     * Получает email пользователя по его ID и роли.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return email пользователя
     * @throws IllegalArgumentException если пользователь не найден
     */
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

    /**
     * Генерирует имя файла, заменяя недопустимые символы на "_".
     *
     * @param prefix           префикс для имени файла
     * @param originalFileName оригинальное имя файла
     * @return сгенерированное имя файла
     */
    private String generateFileName(String prefix, String originalFileName) {
        return (prefix + "_" + originalFileName).replaceAll(FILE_NAME_REGEX, "_");
    }

    /**
     * Загружает файл на Google Drive и возвращает его URL.
     *
     * @param file      файл для загрузки
     * @param fileName  имя файла
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
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

    /**
     * Обновляет URL фото профиля пользователя в базе данных.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @param fileUrl URL загруженного файла
     */
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

    /**
     * Обновляет URL обложки курса в базе данных.
     *
     * @param courseId идентификатор курса
     * @param fileUrl  URL загруженного файла
     */
    private void updateCourseCover(Long courseId, String fileUrl) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        course.setPhotoUrl(fileUrl);
        courseRepository.save(course);
    }

    /**
     * Загружает файл для урока на Google Drive и сохраняет информацию в базе данных.
     *
     * @param file     файл для загрузки
     * @param lessonId идентификатор урока
     * @param fileType тип файла (например, "photo", "video", "document")
     * @return объект LessonFile с информацией о загруженном файле
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public LessonFile uploadLessonFile(MultipartFile file, Long lessonId, String fileType) throws IOException {
        LOGGER.info("Загрузка файла для урока ID: {}", lessonId);

        String fileName = generateFileName("lesson_" + lessonId, file.getOriginalFilename());
        String fileUrl = uploadFileToDrive(file, fileName);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Урок с ID " + lessonId + " не найден"));

        LessonFile lessonFile = new LessonFile(lesson, fileType, fileName, fileUrl);
        return lessonFileRepository.save(lessonFile);
    }

    /**
     * Получает список файлов для урока.
     *
     * @param lessonId идентификатор урока
     * @return список файлов урока
     */
    public List<LessonFile> getLessonFiles(Long lessonId) {
        LOGGER.debug("Получение файлов для урока ID: {}", lessonId);
        return lessonFileRepository.findByLessonId(lessonId);
    }

    // Метод для удаления файла с Google Drive
    private void deleteFileFromDrive(String fileUrl) {
        try {
            String fileId = extractFileIdFromUrl(fileUrl);
            driveService.files().delete(fileId).execute();
        } catch (Exception e) {
            LOGGER.error("Ошибка при удалении файла с Google Drive: {}", e.getMessage());
        }
    }

    /**
     * Удаляет файлы урока с Google Drive и обновляет информацию в базе данных.
     *
     * @param lessonId идентификатор урока
     */
    public void deleteLessonFiles(Long lessonId) {
        List<LessonFile> files = lessonFileRepository.findByLessonId(lessonId);
        for (LessonFile file : files) {
            deleteFileFromDrive(file.getFileUrl());
            lessonFileRepository.delete(file);
        }
    }

    /**
     * Удаляет обложку курса с Google Drive и обновляет информацию в базе данных.
     *
     * @param courseId идентификатор курса
     */
    public void deleteCourseCover(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        if (course.getPhotoUrl() != null) {
            deleteFileFromDrive(course.getPhotoUrl());
            course.setPhotoUrl(null);
            courseRepository.save(course);
        }
    }

    /**
     * Удаляет фото профиля пользователя.
     *
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     */
    public void deleteProfilePhoto(Long userId, String role) {
        if ("Specialist".equalsIgnoreCase(role)) {
            Specialist specialist = specialistRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Специалист с ID " + userId + " не найден"));
            if (specialist.getProfilePhotoUrl() != null) {
                deleteFileFromDrive(specialist.getProfilePhotoUrl());
                specialist.setProfilePhotoUrl(null);
                specialistRepository.save(specialist);
            }
        } else if ("Listener".equalsIgnoreCase(role)) {
            Listener listener = listenerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Слушатель с ID " + userId + " не найден"));
            if (listener.getProfilePhotoUrl() != null) {
                deleteFileFromDrive(listener.getProfilePhotoUrl());
                listener.setProfilePhotoUrl(null);
                listenerRepository.save(listener);
            }
        }
    }

    /**
     * Извлекает ID файла из URL.
     *
     * @param fileUrl URL файла
     * @return ID файла
     */
    private String extractFileIdFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains("/")) {
            throw new IllegalArgumentException("Некорректный URL файла: " + fileUrl);
        }
        int startIndex = fileUrl.lastIndexOf("/") + 1;
        int endIndex = fileUrl.contains("?") ? fileUrl.indexOf("?") : fileUrl.length();
        return fileUrl.substring(startIndex, endIndex);
    }
}