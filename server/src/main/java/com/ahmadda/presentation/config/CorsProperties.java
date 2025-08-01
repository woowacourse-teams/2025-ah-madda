package com.ahmadda.presentation.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private final String[] allowedOrigins;

    public CorsProperties(final String[] allowedOrigins) {
        validateProperties(allowedOrigins);

        this.allowedOrigins = allowedOrigins;
    }

    private void validateProperties(final String[] allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.length == 0) {
            throw new IllegalArgumentException("허용된 CORS 오리진이 하나 이상 지정되어야 합니다.");
        }
        for (String origin : allowedOrigins) {
            if (origin == null || origin.isBlank()) {
                throw new IllegalArgumentException("CORS 오리진 값에 null 또는 빈 값이 포함되어 있습니다.");
            }
        }
    }
}
