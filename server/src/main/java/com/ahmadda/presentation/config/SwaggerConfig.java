package com.ahmadda.presentation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    private Info apiInfo() {
        return new Info()
                .title("Ahmadda API")
                .description("Ahmadda API 명세서")
                .version("v0.0.0");
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
    }

    private Components securityComponents() {
        SecurityScheme scheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);

        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, scheme);
    }
}
