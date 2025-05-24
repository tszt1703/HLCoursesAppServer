package org.example.hlcoursesappserver.service;

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
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
    private static final String FILE_NAME_REGEX = "[^a-zA-Z0-9.]";

    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonFileRepository lessonFileRepository;

    @Value("${file.storage.path:/uploads}")
    private String storagePath;

    @Value("${file.access.url:/files}")
    private String fileAccessUrl;

    @Autowired
    public FileService(SpecialistRepository specialistRepository,
                       ListenerRepository listenerRepository,
                       CourseRepository courseRepository,
                       LessonRepository lessonRepository,
                       LessonFileRepository lessonFileRepository) {
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.lessonFileRepository = lessonFileRepository;
    }

    public String uploadProfilePhoto(MultipartFile file, Long userId, String role) throws IOException {
        LOGGER.info("Загрузка фото профиля для пользователя ID: {} с ролью: {}", userId, role);

        validateImageFile(file); // Перенесена валидация
        String email = getUserEmail(userId, role);
        String fileName = generateFileName(email, file.getOriginalFilename());
        String fileUrl = saveFileToServer(file, fileName);

        updateUserProfilePhoto(userId, role, fileUrl);
        LOGGER.info("Фото профиля успешно загружено: {}", fileUrl);
        return fileUrl;
    }

    public String uploadCourseCover(MultipartFile file, Long courseId) throws IOException {
        LOGGER.info("Загрузка обложки для курса ID: {}", courseId);

        validateImageFile(file); // Перенесена валидация
        String fileName = generateFileName("course_" + courseId, file.getOriginalFilename());
        String fileUrl = saveFileToServer(file, fileName);

        updateCourseCover(courseId, fileUrl);
        LOGGER.info("Обложка курса успешно загружена: {}", fileUrl);
        return fileUrl;
    }

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
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return (prefix + "_" + UUID.randomUUID() + extension).replaceAll(FILE_NAME_REGEX, "_");
    }

    private String saveFileToServer(MultipartFile file, String fileName) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        Path storageDir = Paths.get(storagePath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }

        Path filePath = storageDir.resolve(fileName);
        file.transferTo(filePath.toFile());

        return fileAccessUrl + "/" + fileName;
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

    public LessonFile uploadLessonFile(MultipartFile file, Long lessonId, String fileType) throws IOException {
        LOGGER.info("Загрузка файла для урока ID: {}", lessonId);

        validateLessonFile(file, fileType); // Перенесена валидация
        String fileName = generateFileName("lesson_" + lessonId, file.getOriginalFilename());
        String fileUrl = saveFileToServer(file, fileName);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Урок с ID " + lessonId + " не найден"));

        LessonFile lessonFile = new LessonFile(lesson, fileType, fileName, fileUrl);
        return lessonFileRepository.save(lessonFile);
    }

    public List<LessonFile> getLessonFiles(Long lessonId) {
        LOGGER.debug("Получение файлов для урока ID: {}", lessonId);
        return lessonFileRepository.findByLesson_LessonId(lessonId);
    }

    private void deleteFileFromServer(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(storagePath, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            LOGGER.error("Ошибка при удалении файла с сервера: {}", e.getMessage());
        }
    }

    public void deleteLessonFiles(Long lessonId) {
        List<LessonFile> files = lessonFileRepository.findByLesson_LessonId(lessonId);
        for (LessonFile file : files) {
            deleteFileFromServer(file.getFileUrl());
            lessonFileRepository.delete(file);
        }
    }

    public void deleteCourseCover(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        if (course.getPhotoUrl() != null) {
            deleteFileFromServer(course.getPhotoUrl());
            course.setPhotoUrl(null);
            courseRepository.save(course);
        }
    }

    public void deleteProfilePhoto(Long userId, String role) {
        if ("Specialist".equalsIgnoreCase(role)) {
            Specialist specialist = specialistRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Специалист с ID " + userId + " не найден"));
            if (specialist.getProfilePhotoUrl() != null) {
                deleteFileFromServer(specialist.getProfilePhotoUrl());
                specialist.setProfilePhotoUrl(null);
                specialistRepository.save(specialist);
            }
        } else if ("Listener".equalsIgnoreCase(role)) {
            Listener listener = listenerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Слушатель с ID " + userId + " не найден"));
            if (listener.getProfilePhotoUrl() != null) {
                deleteFileFromServer(listener.getProfilePhotoUrl());
                listener.setProfilePhotoUrl(null);
                listenerRepository.save(listener);
            }
        }
    }

    /**
     * Валидация файлов изображений (для профилей и обложек).
     *
     * @param file файл для проверки
     * @throws IllegalArgumentException если файл имеет недопустимый тип
     */
    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Файл должен быть изображением (jpg, png, etc.)");
        }
        if (file.getSize() > 2 * 1024 * 1024) { // Лимит 2 МБ для изображений
            throw new IllegalArgumentException("Размер изображения не должен превышать 2 МБ");
        }
    }

    /**
     * Валидация файлов урока.
     *
     * @param file     файл для проверки
     * @param fileType тип файла
     * @throws IllegalArgumentException если файл или тип недопустимы
     */
    private void validateLessonFile(MultipartFile file, String fileType) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Тип файла не определён");
        }
        if (file.getSize() > 50 * 1024 * 1024) { // Лимит 50 МБ для файлов уроков
            throw new IllegalArgumentException("Размер файла не должен превышать 50 МБ");
        }
        switch (fileType.toLowerCase()) {
            case "photo":
                if (!contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Файл для типа 'photo' должен быть изображением");
                }
                break;
            case "video":
                if (!contentType.startsWith("video/")) {
                    throw new IllegalArgumentException("Файл для типа 'video' должен быть видео");
                }
                break;
            case "document":
                if (!contentType.startsWith("application/") && !contentType.equals("text/plain")) {
                    throw new IllegalArgumentException("Файл для типа 'document' должен быть документом");
                }
                break;
            default:
                throw new IllegalArgumentException("Недопустимый тип файла: " + fileType);
        }
    }
}