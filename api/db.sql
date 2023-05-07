CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(128) COLLATE utf8mb4_unicode_ci,
    title VARCHAR(512) COLLATE utf8mb4_unicode_ci,
    text VARCHAR(512) COLLATE utf8mb4_unicode_ci,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    device VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    model VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    product VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    manufacturer VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    brand VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    android_version VARCHAR(64) COLLATE utf8mb4_unicode_ci,
    sdk_version VARCHAR(64) COLLATE utf8mb4_unicode_ci
);
CREATE INDEX idx_package_name ON notifications (package_name);
CREATE INDEX idx_created_at ON notifications (created_at);