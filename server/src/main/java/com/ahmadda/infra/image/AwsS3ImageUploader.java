package com.ahmadda.infra.image;

import com.ahmadda.domain.ImageFile;
import com.ahmadda.domain.ImageUploader;
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
public class AwsS3ImageUploader implements ImageUploader {

    private final S3Client amazonS3Client;
    private final AwsS3Properties awsS3Properties;

    @Override
    public String upload(final ImageFile imageFile) {
        String uploadFileName = awsS3Properties.getFolder() + generateUniqueName(imageFile);

        try (InputStream inputStream = imageFile.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3Properties.getBucket())
                    .key(uploadFileName)
                    .contentType(imageFile.getContentType())
                    .build();

            amazonS3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, imageFile.getSize())
            );
        } catch (S3Exception | IOException e) {
            throw new AwsImageUploadException("AWS S3로 이미지 업로드가 실패하였습니다.", e);
        }

        return getUploadImageUrl(uploadFileName);
    }

    private String generateUniqueName(final ImageFile imageFile) {
        String fileName = imageFile.getFileName();
        String extension = StringUtils.getFilenameExtension(fileName);

        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    private String getUploadImageUrl(final String uploadFileName) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                awsS3Properties.getBucket(),
                awsS3Properties.getRegion(),
                uploadFileName
        );
    }
}
