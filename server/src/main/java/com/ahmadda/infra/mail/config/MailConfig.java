package com.ahmadda.infra.mail.config;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.infra.mail.MockEmailNotifier;
import com.ahmadda.infra.mail.SmtpEmailNotifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "true")
    public EmailNotifier mockMailService() {
        return new MockEmailNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "mail.mock", havingValue = "false", matchIfMissing = true)
    public EmailNotifier mailServiceImpl(
            final JavaMailSender javaMailSender,
            final TemplateEngine templateEngine
    ) {
        return new SmtpEmailNotifier(javaMailSender, templateEngine);
    }
}
