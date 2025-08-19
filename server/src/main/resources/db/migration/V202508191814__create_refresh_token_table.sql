CREATE TABLE refresh_token (
   refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
   token VARCHAR(255) NOT NULL,
   member_id BIGINT NOT NULL,
   device_id VARCHAR(255) NOT NULL,
   expires_at DATETIME(6) NOT NULL
);
