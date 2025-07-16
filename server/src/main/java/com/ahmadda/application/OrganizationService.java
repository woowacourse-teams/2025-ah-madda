package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.presentation.OrganizationCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public void createOrganization(final OrganizationCreateRequest organizationCreateRequest) {
        Organization organization = Organization.create(
                organizationCreateRequest.name(),
                organizationCreateRequest.description(),
                organizationCreateRequest.imageUrl()
        );

        organizationRepository.save(organization);
    }

    public Organization getOrganization(final Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessFlowViolatedException("존재하지 않는 조직입니다."));
    }
}
