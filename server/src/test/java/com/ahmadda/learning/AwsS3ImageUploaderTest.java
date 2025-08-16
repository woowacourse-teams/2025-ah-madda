package com.ahmadda.learning;

import com.ahmadda.domain.ImageFile;
import com.ahmadda.infra.image.AwsS3ImageUploader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "aws.s3.mock=false",
        "aws.s3.region=${aws.dev.s3.region}",
        "aws.s3.bucket=${aws.dev.s3.bucket}",
        "aws.s3.folder=${aws.dev.s3.folder}"
})
@Transactional
//@Disabled
public class AwsS3ImageUploaderTest {

    @Autowired
    private AwsS3ImageUploader awsS3ImageUploader;

    @Test
    void 실제_AWS_S3로_이미지를_업로드한다() {
        // given
        byte[] fileContent = "dummy image data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContent);

        ImageFile imageFile = ImageFile.create(
                "test.jpg",
                "image/jpeg",
                fileContent.length,
                inputStream
        );

        // when
        String url = awsS3ImageUploader.upload(imageFile);

        //then
        System.out.println(url);
    }
}
