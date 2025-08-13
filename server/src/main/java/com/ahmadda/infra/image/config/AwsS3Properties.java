package com.ahmadda.infra.image.config;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
@Getter
public class AwsS3Properties {

    private final String region;
    private final String bucket;
    private final String folder;

    public AwsS3Properties(final String region, final String bucket, final String folder) {
        validateProperties(region, bucket);

        this.region = region;
        this.bucket = bucket;
        this.folder = folder == null ? "" : folder;
    }

    private void validateProperties(final String region, final String bucket) {
        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("region 설정이 비어있습니다.");
        }

        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException("bucket 설정이 비어있습니다.");
        }
    }
}
