package com.ahmadda.presentation.config;

import com.ahmadda.presentation.resolver.AuthLoginMemberArgumentResolver;
import com.ahmadda.presentation.resolver.OptionalAuthLoginMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthLoginMemberArgumentResolver authLoginMemberArgumentResolver;
    private final OptionalAuthLoginMemberArgumentResolver optionalAuthLoginMemberArgumentResolver;
    private final CorsProperties corsProperties;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authLoginMemberArgumentResolver);
        resolvers.add(optionalAuthLoginMemberArgumentResolver);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins())
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
