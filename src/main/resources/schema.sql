-- Создание таблиц
CREATE TABLE Specialists (
                             specialist_id SERIAL PRIMARY KEY,
                             first_name VARCHAR(100) NOT NULL,
                             last_name VARCHAR(100) NOT NULL,
                             email VARCHAR(100) UNIQUE NOT NULL,
                             password VARCHAR(100) NOT NULL,
                             birth_date DATE,
                             profile_photo_url TEXT,  -- ссылка на фото профиля
                             description TEXT,
                             certification_document_url TEXT,  -- документы, подтверждающие образование
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Listeners (
                           listener_id SERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           password VARCHAR(100) NOT NULL,
                           birth_date DATE,
                           profile_photo_url TEXT,  -- ссылка на фото профиля
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Course_Categories (
                                   category_id SERIAL PRIMARY KEY,
                                   category_name VARCHAR(100) NOT NULL
);

CREATE TABLE Courses (
                         course_id SERIAL PRIMARY KEY,
                         specialist_id INT REFERENCES Specialists(specialist_id) ON DELETE CASCADE, -- связь со специалистом
                         category_id INT REFERENCES Course_Categories(category_id),
                         title VARCHAR(200) NOT NULL,
                         description TEXT,
                         difficulty_level VARCHAR(50),
                         age_group VARCHAR(50),
                         duration_days INT,
                         plan TEXT, -- план проведения курса
                         photo_url TEXT,  -- ссылка на изображение курса
                         video_url TEXT,  -- ссылка на видео-материал курса
                         certificate_available BOOLEAN DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Lessons (
                         lesson_id SERIAL PRIMARY KEY,
                         course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE, -- связь с курсом
                         title VARCHAR(200) NOT NULL,
                         content TEXT, -- теоретический материал
                         photo_url TEXT,  -- ссылка на изображение урока
                         video_url TEXT,  -- ссылка на видео-материал урока
                         order_num INT, -- порядок уроков в курсе
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tests (
                       test_id SERIAL PRIMARY KEY,
                       lesson_id INT REFERENCES Lessons(lesson_id) ON DELETE CASCADE, -- связь с уроком
                       title VARCHAR(200) NOT NULL,
                       question TEXT,
                       correct_answer TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Messages (
                          message_id SERIAL PRIMARY KEY,
                          sender_id INT, -- отправитель (может быть специалист или слушатель)
                          receiver_id INT, -- получатель (может быть специалист или слушатель)
                          sender_role VARCHAR(20) CHECK (sender_role IN ('specialist', 'listener')), -- роль отправителя
                          receiver_role VARCHAR(20) CHECK (receiver_role IN ('specialist', 'listener')), -- роль получателя
                          message_text TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Reviews (
                         review_id SERIAL PRIMARY KEY,
                         listener_id INT REFERENCES Listeners(listener_id) ON DELETE CASCADE, -- слушатель
                         course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE, -- курс
                         rating INT CHECK (rating >= 1 AND rating <= 5),
                         review_text TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Progress_Stats (
                                progress_id SERIAL PRIMARY KEY,
                                listener_id INT REFERENCES Listeners(listener_id) ON DELETE CASCADE, -- слушатель
                                course_id INT REFERENCES Courses(course_id) ON DELETE CASCADE, -- курс
                                lessons_completed INT DEFAULT 0,
                                tests_passed INT DEFAULT 0,
                                progress_percent DECIMAL(5,2) DEFAULT 0.00,
                                last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);