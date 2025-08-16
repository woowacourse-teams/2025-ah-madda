package com.ahmadda.infra.image;

import com.ahmadda.domain.ImageFile;
import com.ahmadda.domain.ImageUploader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockImageUploader implements ImageUploader {

    @Override
    public String upload(final ImageFile file) {
        log.info("[Mock ImageUploader] uploading file {}", file.getFileName());
        
        return file.getFileName();
    }
}
