package com.ahmadda.infra.notification.mail.config;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.infra.notification.config.NotificationProperties;
import com.ahmadda.infra.notification.mail.MockEmailNotifier;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

import java.util.Map;
import java.util.Properties;

@EnableConfigurationProperties({NotificationProperties.class, SmtpProperties.class})
@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnProperty(name = "mail.provider", havingValue = "mock")
    public EmailNotifier mockEmailNotifier() {
        return new MockEmailNotifier();
    }

    @Bean
    @ConditionalOnProperty(name = "mail.provider", havingValue = "gmail")
    public EmailNotifier gmailEmailNotifier(
            final JavaMailSender smtpMailSender,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final EntityManager em
    ) {
        return new SmtpEmailNotifier(smtpMailSender, templateEngine, notificationProperties, em);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.provider", havingValue = "aws")
    public EmailNotifier awsEmailNotifier(
            final JavaMailSender smtpMailSender,
            final TemplateEngine templateEngine,
            final NotificationProperties notificationProperties,
            final EntityManager em
    ) {
        return new SmtpEmailNotifier(smtpMailSender, templateEngine, notificationProperties, em);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.provider", havingValue = "gmail")
    public JavaMailSender gmailSmtpMailSender(final SmtpProperties props) {
        SmtpProperties.Account acc = props.getGoogle();

        return getJavaMailSender(acc);
    }

    @Bean
    @ConditionalOnProperty(name = "mail.provider", havingValue = "aws")
    public JavaMailSender awsSmtpMailSender(final SmtpProperties props) {
        SmtpProperties.Account acc = props.getAws();
        
        return getJavaMailSender(acc);
    }

    private JavaMailSender getJavaMailSender(final SmtpProperties.Account acc) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(acc.getHost());
        sender.setPort(acc.getPort());
        sender.setUsername(acc.getUsername());
        sender.setPassword(acc.getPassword());
        sender.setDefaultEncoding("UTF-8");
        apply(sender, acc.getProperties());

        return sender;
    }

    private void apply(final JavaMailSenderImpl sender, final Map<String, String> props) {
        if (props == null) {
            return;
        }

        Properties javaMailProps = sender.getJavaMailProperties();
        javaMailProps.putAll(props);
    }
}
