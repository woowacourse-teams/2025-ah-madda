package com.ahmadda.infra.image.config;

import com.ahmadda.domain.organization.OrganizationImageUploader;
import com.ahmadda.infra.image.AwsS3OrganizationImageUploader;
import com.ahmadda.infra.image.NoopOrganizationImageUploader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
public class ImageUploaderConfig {

    @Bean
    @ConditionalOnProperty(name = "aws.s3.noob", havingValue = "false", matchIfMissing = true)
    public OrganizationImageUploader awsS3ImageUploader(final S3Client s3Client,
                                                        final AwsS3Properties awsS3Properties) {
        return new AwsS3OrganizationImageUploader(s3Client, awsS3Properties);
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.noob", havingValue = "true")
    public OrganizationImageUploader noobImageUploader() {
        return new NoopOrganizationImageUploader();
    }
}
