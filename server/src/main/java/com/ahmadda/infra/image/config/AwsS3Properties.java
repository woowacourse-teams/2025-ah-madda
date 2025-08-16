package com.ahmadda.infra.image.config;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
@Getter
public class AwsS3Properties {

    private final String bucket;
    private final String folder;

    public AwsS3Properties(final String bucket, final String folder) {
        validateProperties(bucket, folder);

        this.bucket = bucket;
        this.folder = folder;
    }

    private void validateProperties(final String bucket, final String folder) {
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("bucket 설정이 비어있습니다.");
        }

        if (folder == null || folder.isEmpty()) {
            throw new IllegalArgumentException("folder 설정이 비어있습니다.");
        }
    }
}
