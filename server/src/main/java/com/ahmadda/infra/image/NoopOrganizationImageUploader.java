package com.ahmadda.infra.image;

import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.domain.organization.OrganizationImageUploader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoopOrganizationImageUploader implements OrganizationImageUploader {

    @Override
    public String upload(final OrganizationImageFile file) {
        log.info("[Noop ImageUploader] uploading file {}", file.getFileName());

        return file.getFileName();
    }
}
