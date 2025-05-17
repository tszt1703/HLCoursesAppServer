package org.example.hlcoursesappserver.service;

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
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Сервис для работы с файлами, использующий локальное хранилище.
 * Код для Google Drive закомментирован для возможного использования в будущем.
 */
@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
    private static final String FILE_NAME_REGEX = "[^a-zA-Z0-9.]";
    private static final String[] ALLOWED_FILE_TYPES = {
            "image/jpeg", "image/jpg","image/png", "image/gif",
            "video/mp4", "video/mpeg", "application/pdf"
    };
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private final SpecialistRepository specialistRepository;
    private final ListenerRepository listenerRepository;
    private final CourseRepository courseRepository;

    // Закомментированная зависимость для Google Drive
    /*
    private final Drive driveService;
    @Value("${google.drive.profile-photos-folder-id:1fTKb6ARwNhdNVN0fVyXcPsKH4rCdRgdl}")
    private String profilePhotosFolderId;
    */

    @Value("${file.upload-dir:/uploads}")
    private String uploadDir;

    @Value("${server.base-url:http://localhost:8080}")
    private String serverBaseUrl;

    @Autowired
    public FileService(SpecialistRepository specialistRepository,
                       ListenerRepository listenerRepository,
                       CourseRepository courseRepository) {
        this.specialistRepository = specialistRepository;
        this.listenerRepository = listenerRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Загружает фото профиля пользователя в локальное хранилище и обновляет URL в базе данных.
     *
     * @param file   файл фото профиля
     * @param userId идентификатор пользователя
     * @param role   роль пользователя ("Specialist" или "Listener")
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public String uploadProfilePhoto(MultipartFile file, Long userId, String role) throws IOException {
        LOGGER.info("Загрузка фото профиля для пользователя ID: {} с ролью: {}", userId, role);

        validateFile(file);
        String email = getUserEmail(userId, role);
        String fileName = generateFileName(email, file.getOriginalFilename());
        String fileUrl = saveFile(file, fileName);

        updateUserProfilePhoto(userId, role, fileUrl);
        LOGGER.info("Фото профиля успешно загружено: {}", fileUrl);
        return fileUrl;
    }

    /**
     * Загружает обложку курса в локальное хранилище и обновляет URL в базе данных.
     *
     * @param file     файл обложки курса
     * @param courseId идентификатор курса
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public String uploadCourseCover(MultipartFile file, Long courseId) throws IOException {
        LOGGER.info("Загрузка обложки для курса ID: {}", courseId);

        validateFile(file);
        String fileName = generateFileName("course_" + courseId, file.getOriginalFilename());
        String fileUrl = saveFile(file, fileName);

        updateCourseCover(courseId, fileUrl);
        LOGGER.info("Обложка курса успешно загружена: {}", fileUrl);
        return fileUrl;
    }

    /**
     * Загружает видео для курса в локальное хранилище.
     *
     * @param file     файл видео
     * @param courseId идентификатор курса
     * @return URL загруженного файла
     * @throws IOException если произошла ошибка при загрузке файла
     */
    public String uploadCourseVideo(MultipartFile file, Long courseId) throws IOException {
        LOGGER.info("Загрузка видео для курса ID: {}", courseId);

        validateFile(file);
        String fileName = generateFileName("video_course_" + courseId, file.getOriginalFilename());
        String fileUrl = saveFile(file, fileName);

        // Если нужно сохранить URL видео в базе, раскомментируйте:
        // updateCourseVideo(courseId, fileUrl);
        LOGGER.info("Видео курса успешно загружено: {}", fileUrl);
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
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : ".jpg";
        String uniqueId = UUID.randomUUID().toString();
        return (prefix + "_" + uniqueId + extension).replaceAll(FILE_NAME_REGEX, "_");
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Файл пустой или отсутствует");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("Файл слишком большой. Максимальный размер: " + (MAX_FILE_SIZE / (1024 * 1024)) + " МБ");
        }
        String contentType = file.getContentType();
        LOGGER.info("MIME-тип файла: {}, имя файла: {}", contentType, file.getOriginalFilename());

        boolean isValidType = contentType != null && isAllowedFileType(contentType);
        if (!isValidType && contentType != null && contentType.equals("image/*")) {
            // Дополнительная проверка расширения файла
            String fileName = file.getOriginalFilename();
            if (fileName != null && (fileName.toLowerCase().endsWith(".jpg") ||
                    fileName.toLowerCase().endsWith(".jpeg") ||
                    fileName.toLowerCase().endsWith(".png") ||
                    fileName.toLowerCase().endsWith(".gif"))) {
                LOGGER.warn("MIME-тип image/* заменен на основе расширения: {}", fileName);
                isValidType = true;
            }
        }

        if (!isValidType) {
            LOGGER.error("Недопустимый тип файла: {}. Разрешены: {}", contentType, String.join(", ", ALLOWED_FILE_TYPES));
            throw new IOException("Недопустимый тип файла. Разрешены: JPEG, PNG, GIF, MP4, MPEG, PDF");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        for (String allowedType : ALLOWED_FILE_TYPES) {
            LOGGER.debug(
                    contentType, allowedType);
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        LOGGER.info("Попытка сохранить файл в директорию: {}", uploadPath);

        // Проверяем и создаем директорию
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                LOGGER.info("Директория создана: {}", uploadPath);
            } catch (IOException e) {
                LOGGER.error("Не удалось создать директорию {}: {}", uploadPath, e.getMessage());
                throw new IOException("Не удалось создать директорию для загрузки файлов: " + e.getMessage(), e);
            }
        }

        // Проверяем права на запись
        if (!Files.isWritable(uploadPath)) {
            LOGGER.error("Нет прав на запись в директорию: {}", uploadPath);
            throw new IOException("Нет прав на запись в директорию: " + uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        LOGGER.debug("Сохранение файла по пути: {}", filePath);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            LOGGER.error("Ошибка при сохранении файла {}: {}", filePath, e.getMessage());
            throw new IOException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }

        String fileUrl = serverBaseUrl + "/uploads/" + fileName;
        LOGGER.info("Файл успешно сохранен, URL: {}", fileUrl);
        return fileUrl;
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

    // Если нужно сохранять URL видео в базе данных, раскомментируйте и добавьте поле videoUrl в модель Course
    /*
    private void updateCourseVideo(Long courseId, String fileUrl) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Курс с ID " + courseId + " не найден"));
        course.setVideoUrl(fileUrl);
        courseRepository.save(course);
    }
    */

    // Закомментированный код для Google Drive
    /*
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
    */
}