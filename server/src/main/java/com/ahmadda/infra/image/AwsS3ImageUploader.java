package com.ahmadda.infra.image;

import com.ahmadda.domain.ImageFile;
import com.ahmadda.domain.ImageUploader;
import com.ahmadda.infra.image.config.AwsS3Properties;
import com.ahmadda.infra.image.exception.AwsImageUploadException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
public class AwsS3ImageUploader implements ImageUploader {

    private final AmazonS3 amazonS3Client;
    private final AwsS3Properties awsS3Properties;

    @Override
    public String upload(final ImageFile imageFile) {
        String uploadFileName = awsS3Properties.getFolder() + generateUniqueName(imageFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());

        try (InputStream inputStream = imageFile.getInputStream()) {
            amazonS3Client.putObject(awsS3Properties.getBucket(), uploadFileName, inputStream, metadata);
        } catch (AmazonServiceException | IOException e) {
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
