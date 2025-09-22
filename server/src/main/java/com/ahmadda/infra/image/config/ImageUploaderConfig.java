package com.ahmadda.infra.image.config;

import com.ahmadda.domain.organization.OrganizationImageUploader;
import com.ahmadda.infra.image.AwsS3OrganizationImageUploader;
import com.ahmadda.infra.image.NoopOrganizationImageUploader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
public class ImageUploaderConfig {

    @Bean
    public OrganizationImageUploader awsS3ImageUploader(final S3Client s3Client, final AwsS3Properties awsS3Properties) {
        return new AwsS3OrganizationImageUploader(s3Client, awsS3Properties);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "aws.s3.noop", havingValue = "true")
    public OrganizationImageUploader noopImageUploader() {
        return new NoopOrganizationImageUploader();
    }
}
