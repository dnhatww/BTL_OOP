-- -----------------------------------------------------
-- Thiết lập Môi trường
-- -----------------------------------------------------
SET NAMES utf8mb4;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- -----------------------------------------------------
-- Xóa bảng nếu tồn tại (theo thứ tự ngược lại)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `document_tags`;
DROP TABLE IF EXISTS `ratings`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `documents`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `tags`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;

-- -----------------------------------------------------
-- Bảng: roles
-- -----------------------------------------------------
CREATE TABLE `roles` (
                         `role_id` INT NOT NULL AUTO_INCREMENT,
                         `name` VARCHAR(50) NOT NULL,
                         PRIMARY KEY (`role_id`),
                         UNIQUE INDEX `name_UNIQUE` (`name` ASC)
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: users
-- -----------------------------------------------------
CREATE TABLE `users` (
                         `user_id` BIGINT NOT NULL AUTO_INCREMENT,
                         `username` VARCHAR(50) NOT NULL,
                         `password` VARCHAR(255) NOT NULL,
                         `email` VARCHAR(100) NOT NULL,
                         `full_name` VARCHAR(100) NULL,
                         `is_enabled` TINYINT(1) NOT NULL DEFAULT 1,
                         `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`user_id`),
                         UNIQUE INDEX `username_UNIQUE` (`username` ASC),
                         UNIQUE INDEX `email_UNIQUE` (`email` ASC)
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: categories
-- -----------------------------------------------------
CREATE TABLE `categories` (
                              `category_id` BIGINT NOT NULL AUTO_INCREMENT,
                              `name` VARCHAR(100) NOT NULL,
                              `slug` VARCHAR(100) NOT NULL,
                              PRIMARY KEY (`category_id`),
                              UNIQUE INDEX `name_UNIQUE` (`name` ASC),
                              UNIQUE INDEX `slug_UNIQUE` (`slug` ASC)
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: tags
-- -----------------------------------------------------
CREATE TABLE `tags` (
                        `tag_id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(50) NOT NULL,
                        `slug` VARCHAR(50) NOT NULL,
                        PRIMARY KEY (`tag_id`),
                        UNIQUE INDEX `name_UNIQUE` (`name` ASC),
                        UNIQUE INDEX `slug_UNIQUE` (`slug` ASC)
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: user_roles (Bảng nối User và Role)
-- -----------------------------------------------------
CREATE TABLE `user_roles` (
                              `user_id` BIGINT NOT NULL,
                              `role_id` INT NOT NULL,
                              PRIMARY KEY (`user_id`, `role_id`),
                              INDEX `fk_user_roles_role_idx` (`role_id` ASC),
                              CONSTRAINT `fk_user_roles_user`
                                  FOREIGN KEY (`user_id`)
                                      REFERENCES `users` (`user_id`)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE,
                              CONSTRAINT `fk_user_roles_role`
                                  FOREIGN KEY (`role_id`)
                                      REFERENCES `roles` (`role_id`)
                                      ON DELETE CASCADE
                                      ON UPDATE CASCADE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: documents (Đã tối ưu)
-- -----------------------------------------------------
CREATE TABLE `documents` (
                             `doc_id` BIGINT NOT NULL AUTO_INCREMENT,
                             `title` VARCHAR(255) NOT NULL,
                             `description` TEXT NULL,
                             `file_path` VARCHAR(255) NOT NULL COMMENT 'Đường dẫn/URL tới file trên S3/GCS/v.v.',
                             `mime_type` VARCHAR(100) NULL,
                             `user_id` BIGINT NOT NULL,
                             `category_id` BIGINT NULL,
                             `status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
                             `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Các cột tối ưu hóa (Denormalized)
                             `views_count` INT NOT NULL DEFAULT 0,
                             `download_count` INT NOT NULL DEFAULT 0,
                             `comments_count` INT NOT NULL DEFAULT 0,
                             `average_rating` DECIMAL(3, 2) NOT NULL DEFAULT 0.00,

                             PRIMARY KEY (`doc_id`),
                             INDEX `fk_documents_user_idx` (`user_id` ASC),
                             INDEX `fk_documents_category_idx` (`category_id` ASC),
                             INDEX `idx_status` (`status` ASC),
                             FULLTEXT INDEX `idx_fulltext_search` (`title`, `description`),

                             CONSTRAINT `fk_documents_user`
                                 FOREIGN KEY (`user_id`)
                                     REFERENCES `users` (`user_id`)
                                     ON DELETE RESTRICT -- Giữ lại tài liệu nếu user bị xóa (hoặc SET NULL)
                                     ON UPDATE CASCADE,
                             CONSTRAINT `fk_documents_category`
                                 FOREIGN KEY (`category_id`)
                                     REFERENCES `categories` (`category_id`)
                                     ON DELETE SET NULL -- Không xóa tài liệu nếu category bị xóa
                                     ON UPDATE CASCADE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: document_tags (Bảng nối Tài liệu và Tag)
-- -----------------------------------------------------
CREATE TABLE `document_tags` (
                                 `doc_id` BIGINT NOT NULL,
                                 `tag_id` BIGINT NOT NULL,
                                 PRIMARY KEY (`doc_id`, `tag_id`),
                                 INDEX `fk_document_tags_tag_idx` (`tag_id` ASC),
                                 CONSTRAINT `fk_document_tags_document`
                                     FOREIGN KEY (`doc_id`)
                                         REFERENCES `documents` (`doc_id`)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,
                                 CONSTRAINT `fk_document_tags_tag`
                                     FOREIGN KEY (`tag_id`)
                                         REFERENCES `tags` (`tag_id`)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: comments
-- -----------------------------------------------------
CREATE TABLE `comments` (
                            `comment_id` BIGINT NOT NULL AUTO_INCREMENT,
                            `doc_id` BIGINT NOT NULL,
                            `user_id` BIGINT NOT NULL,
                            `parent_id` BIGINT NULL COMMENT 'FK tự tham chiếu đến comment_id để reply',
                            `content` TEXT NOT NULL,
                            `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`comment_id`),
                            INDEX `fk_comments_document_idx` (`doc_id` ASC),
                            INDEX `fk_comments_user_idx` (`user_id` ASC),
                            INDEX `fk_comments_parent_idx` (`parent_id` ASC),
                            CONSTRAINT `fk_comments_document`
                                FOREIGN KEY (`doc_id`)
                                    REFERENCES `documents` (`doc_id`)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                            CONSTRAINT `fk_comments_user`
                                FOREIGN KEY (`user_id`)
                                    REFERENCES `users` (`user_id`)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,
                            CONSTRAINT `fk_comments_parent`
                                FOREIGN KEY (`parent_id`)
                                    REFERENCES `comments` (`comment_id`)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Bảng: ratings
-- -----------------------------------------------------
CREATE TABLE `ratings` (
                           `rating_id` BIGINT NOT NULL AUTO_INCREMENT,
                           `doc_id` BIGINT NOT NULL,
                           `user_id` BIGINT NOT NULL,
                           `score` TINYINT NOT NULL COMMENT 'Điểm đánh giá từ 1 đến 5',
                           PRIMARY KEY (`rating_id`),
    -- Ràng buộc: 1 user chỉ được đánh giá 1 tài liệu 1 lần
                           UNIQUE INDEX `uq_user_doc_rating` (`user_id` ASC, `doc_id` ASC),
                           INDEX `fk_ratings_document_idx` (`doc_id` ASC),
                           CONSTRAINT `fk_ratings_document`
                               FOREIGN KEY (`doc_id`)
                                   REFERENCES `documents` (`doc_id`)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,
                           CONSTRAINT `fk_ratings_user`
                               FOREIGN KEY (`user_id`)
                                   REFERENCES `users` (`user_id`)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE = InnoDB CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Chèn dữ liệu bảng roles cơ bản
-- -----------------------------------------------------
INSERT INTO `roles` (`name`) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- -----------------------------------------------------
-- Chèn dữ liệu bảng categories cơ bản
-- -----------------------------------------------------
INSERT INTO `categories` (`name`, `slug`)
VALUES
    ('Toán', 'toan'),
    ('Văn', 'van'),
    ('Anh', 'anh'),
    ('Công Nghệ', 'cong-nghe');