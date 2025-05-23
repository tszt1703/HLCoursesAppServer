-- Таблица для категорий курсов
CREATE TABLE course_categories (
                                   category_id SERIAL PRIMARY KEY,
                                   category_name VARCHAR NOT NULL
);

-- Таблица для курсов
CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         specialist_id INT NOT NULL,
                         title VARCHAR NOT NULL,
                         short_description TEXT NOT NULL,
                         full_description TEXT NOT NULL,
                         difficulty_level VARCHAR NOT NULL,
                         age_group VARCHAR,
                         duration_days INT,
                         photo_url VARCHAR,
                         status VARCHAR DEFAULT 'draft', -- Статус: draft, published, archived
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE course_category_mapping (
                                         course_id BIGINT NOT NULL,
                                         category_id BIGINT NOT NULL,
                                         PRIMARY KEY (course_id, category_id),
                                         FOREIGN KEY (course_id) REFERENCES courses(course_id),
                                         FOREIGN KEY (category_id) REFERENCES course_categories(category_id)
);

-- Таблица для модулей курсов
CREATE TABLE course_modules (
                                module_id SERIAL PRIMARY KEY,
                                course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
                                title VARCHAR NOT NULL,
                                description TEXT,
                                position INT DEFAULT 0, -- Порядок модулей
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для уроков
CREATE TABLE lessons (
                         lesson_id SERIAL PRIMARY KEY,
                         module_id INT REFERENCES course_modules(module_id) ON DELETE CASCADE,
                         title VARCHAR NOT NULL,
                         content TEXT,
                         position INT DEFAULT 0, -- Порядок уроков в модуле
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы lesson_files
CREATE TABLE lesson_files (
                              file_id SERIAL PRIMARY KEY,
                              lesson_id INT REFERENCES lessons(lesson_id) ON DELETE CASCADE,
                              file_type VARCHAR NOT NULL, -- Тип файла: 'photo', 'video', 'document' и т.д.
                              file_name VARCHAR NOT NULL, -- Название файла
                              file_url VARCHAR NOT NULL,
                              uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для тестов
CREATE TABLE tests (
                       test_id SERIAL PRIMARY KEY,
                       lesson_id INT REFERENCES lessons(lesson_id) ON DELETE CASCADE,
                       title VARCHAR NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для вопросов тестов
CREATE TABLE questions (
                           question_id SERIAL PRIMARY KEY,
                           test_id INT REFERENCES tests(test_id) ON DELETE CASCADE,
                           question_text TEXT NOT NULL
);

-- Таблица для ответов на вопросы
CREATE TABLE answers (
                         answer_id SERIAL PRIMARY KEY,
                         question_id INT REFERENCES questions(question_id) ON DELETE CASCADE,
                         answer_text TEXT NOT NULL,
                         is_correct BOOLEAN DEFAULT FALSE
);

-- Таблица для отслеживания прогресса пользователей
CREATE TABLE progress_stats (
                                progress_id SERIAL PRIMARY KEY,
                                listener_id INT NOT NULL,
                                course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
                                lessons_completed INT DEFAULT 0,
                                tests_passed INT DEFAULT 0,
                                progress_percent FLOAT DEFAULT 0,
                                last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для отзывов
CREATE TABLE reviews (
                         review_id BIGSERIAL PRIMARY KEY,
                         listener_id INT NOT NULL,
                         course_id INT NOT NULL,
                         rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                         review_text TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (listener_id) REFERENCES listeners(listener_id) ON DELETE CASCADE,
                         FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                         CONSTRAINT unique_listener_course UNIQUE (listener_id, course_id)
);

CREATE TABLE pending_users (
                               id BIGSERIAL PRIMARY KEY,
                               email VARCHAR(255) NOT NULL UNIQUE,
                               password VARCHAR(255) NOT NULL,
                               role VARCHAR(50) NOT NULL,
                               verification_token VARCHAR(255),
                               last_verification_request TIMESTAMP,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Связь с таблицей слушателей
CREATE TABLE listeners (
                           listener_id SERIAL PRIMARY KEY,
                           first_name VARCHAR,
                           last_name VARCHAR,
                           email VARCHAR UNIQUE NOT NULL,
                           password VARCHAR NOT NULL,
                           birth_date DATE,
                           profile_photo_url VARCHAR,
                           description TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Связь с таблицей специалистов
CREATE TABLE specialists (
                             specialist_id SERIAL PRIMARY KEY,
                             first_name VARCHAR,
                             last_name VARCHAR,
                             email VARCHAR UNIQUE NOT NULL,
                             password VARCHAR NOT NULL,
                             birth_date DATE,
                             profile_photo_url VARCHAR,
                             description TEXT,
                             certification_document_url VARCHAR,
                             social_links TEXT,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE course_applications (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     listener_id BIGINT NOT NULL,
                                     course_id BIGINT NOT NULL,
                                     status VARCHAR(255) NOT NULL,
                                     application_date TIMESTAMP NOT NULL,
                                     FOREIGN KEY (listener_id) REFERENCES listeners(listener_id),
                                     FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE listener_favorite_courses (
                                           listener_id BIGINT NOT NULL,
                                           course_id BIGINT NOT NULL,
                                           PRIMARY KEY (listener_id, course_id),
                                           FOREIGN KEY (listener_id) REFERENCES listeners(listener_id),
                                           FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
