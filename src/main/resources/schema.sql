-- Таблица для категорий курсов
CREATE TABLE course_categories (
                                   category_id SERIAL PRIMARY KEY,
                                   category_name VARCHAR NOT NULL
);

-- Таблица для курсов
CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         specialist_id INT NOT NULL,
                         category_id INT REFERENCES course_categories(category_id) ON DELETE SET NULL,
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
                         photo_url VARCHAR,
                         video_url VARCHAR,
                         position INT DEFAULT 0, -- Порядок уроков в модуле
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для тестов
CREATE TABLE tests (
                       test_id SERIAL PRIMARY KEY,
                       lesson_id INT REFERENCES lessons(lesson_id) ON DELETE CASCADE,
                       title VARCHAR NOT NULL,
                       status VARCHAR DEFAULT 'pending', -- Статус: pending, approved, rejected
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
                         review_id SERIAL PRIMARY KEY,
                         listener_id INT NOT NULL,
                         course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
                         rating INT CHECK (rating >= 1 AND rating <= 5),
                         review_text TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для сообщений (чат между специалистами и слушателями)
CREATE TABLE messages (
                          message_id SERIAL PRIMARY KEY,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          sender_role VARCHAR NOT NULL,
                          receiver_role VARCHAR NOT NULL,
                          message_text TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
