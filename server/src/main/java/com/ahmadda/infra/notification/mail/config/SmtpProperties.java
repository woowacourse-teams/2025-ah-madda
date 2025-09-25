package com.ahmadda.infra.notification.mail.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "smtp")
@Getter
public class SmtpProperties {

    private final Account google;
    private final Account aws;

    public SmtpProperties(final Account google, final Account aws) {
        validateProperties(google, aws);

        this.google = google;
        this.aws = aws;
    }

    private void validateProperties(final Account google, final Account aws) {
        if (google == null) {
            throw new IllegalArgumentException("SMTP google 계정 설정이 존재하지 않습니다.");
        }
        if (aws == null) {
            throw new IllegalArgumentException("SMTP aws 계정 설정이 존재하지 않습니다.");
        }
    }

    @Getter
    public static class Account {

        private final String host;
        private final int port;
        private final String username;
        private final String password;
        private final Map<String, String> properties;

        public Account(
                final String host,
                final int port,
                final String username,
                final String password,
                final Map<String, String> properties
        ) {
            validateProperties(host, port, username, password);

            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.properties = properties;
        }

        private static void validateProperties(
                final String host,
                final int port,
                final String username,
                final String password
        ) {
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("SMTP host 설정이 비어있습니다.");
            }
            if (port <= 0) {
                throw new IllegalArgumentException("SMTP port 설정이 올바르지 않습니다.");
            }
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("SMTP username 설정이 비어있습니다.");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("SMTP password 설정이 비어있습니다.");
            }
        }
    }
}
