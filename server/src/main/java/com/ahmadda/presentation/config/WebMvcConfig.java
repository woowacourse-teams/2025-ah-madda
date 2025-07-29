package com.ahmadda.presentation.config;

import com.ahmadda.presentation.resolver.MemberArgumentResolver;
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

    private final MemberArgumentResolver memberArgumentResolver;
    private final CorsProperties corsProperties;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberArgumentResolver);
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
