package com.ahmadda.infra.image.config;

import com.ahmadda.domain.ImageUploader;
import com.ahmadda.infra.image.AwsS3ImageUploader;
import com.ahmadda.infra.image.MockImageUploader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
public class ImageUploaderConfig {

    @Bean
    @ConditionalOnProperty(name = "aws.s3.mock", havingValue = "false", matchIfMissing = true)
    public ImageUploader awsS3ImageUploader(final AwsS3Properties awsS3Properties) {
        return new AwsS3ImageUploader(amazonS3Client(awsS3Properties), awsS3Properties);
    }

    @Bean
    @ConditionalOnProperty(name = "aws.s3.mock", havingValue = "true")
    public ImageUploader mockImageUploader() {
        return new MockImageUploader();
    }

    private S3Client amazonS3Client(final AwsS3Properties awsS3Properties) {
        return S3Client.builder()
                .region(Region.of(awsS3Properties.getRegion()))
                .build();
    }
}
