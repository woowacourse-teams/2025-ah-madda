package com.ahmadda.infra.image;

import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.domain.organization.OrganizationImageUploader;
import com.ahmadda.infra.image.config.AwsS3Properties;
import com.ahmadda.infra.image.exception.AwsImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
public class AwsS3OrganizationImageUploader implements OrganizationImageUploader {

    private final S3Client amazonS3Client;
    private final AwsS3Properties awsS3Properties;

    @Override
    public String upload(final OrganizationImageFile organizationImageFile) {
        String uploadFileName = awsS3Properties.getFolder() + generateUniqueName(organizationImageFile);

        try (InputStream inputStream = organizationImageFile.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3Properties.getBucket())
                    .key(uploadFileName)
                    .contentType(organizationImageFile.getContentType())
                    .build();

            amazonS3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, organizationImageFile.getSize())
            );
        } catch (S3Exception | IOException e) {
            throw new AwsImageUploadException("AWS S3로 이미지 업로드가 실패하였습니다.", e);
        }

        return getUploadImageUrl(uploadFileName);
    }

    private String generateUniqueName(final OrganizationImageFile organizationImageFile) {
        String fileName = organizationImageFile.getFileName();
        String extension = StringUtils.getFilenameExtension(fileName);

        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    private String getUploadImageUrl(final String uploadFileName) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                awsS3Properties.getBucket(),
                amazonS3Client.serviceClientConfiguration()
                        .region(),
                uploadFileName
        );
    }
}
