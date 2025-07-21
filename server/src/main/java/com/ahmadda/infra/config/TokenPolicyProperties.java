package com.ahmadda.infra.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "token")
@RequiredArgsConstructor
@Getter
public class TokenPolicyProperties {

    private final Duration accessExpiration;
}
