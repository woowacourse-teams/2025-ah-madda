package com.ahmadda.infra.image;

import com.ahmadda.domain.ImageFile;
import com.ahmadda.domain.ImageUploader;

public class MockImageUploader implements ImageUploader {

    @Override
    public String upload(final ImageFile file) {
        return file.getFileName();
    }
}
