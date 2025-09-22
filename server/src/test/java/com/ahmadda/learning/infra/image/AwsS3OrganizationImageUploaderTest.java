package com.ahmadda.learning.infra.image;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.infra.image.AwsS3OrganizationImageUploader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Disabled
@LearningTest
@TestPropertySource(properties = {
        "aws.s3.noob=false",
        "aws.s3.region=${aws.dev.s3.region}",
        "aws.s3.bucket=${aws.dev.s3.bucket}",
        "aws.s3.folder=${aws.dev.s3.folder}"
})
public class AwsS3OrganizationImageUploaderTest {

    @Autowired
    private AwsS3OrganizationImageUploader sut;

    @Test
    void 실제_AWS_S3로_이미지를_업로드한다() {
        // given
        byte[] fileContent = "dummy image data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContent);

        OrganizationImageFile organizationImageFile = OrganizationImageFile.create(
                "test.jpg",
                "image/jpeg",
                fileContent.length,
                inputStream
        );

        // when
        String url = sut.upload(organizationImageFile);

        //then
        System.out.println(url);
    }
}
