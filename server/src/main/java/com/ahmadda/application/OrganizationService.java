package com.ahmadda.application;

import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public Organization createOrganization(final OrganizationCreateRequest organizationCreateRequest) {
        Organization organization = Organization.create(
                organizationCreateRequest.name(),
                organizationCreateRequest.description(),
                organizationCreateRequest.imageUrl()
        );

        return organizationRepository.save(organization);
    }

    public Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직입니다."));
    }

    public List<Event> getOrganizationEvents(final Long organizationId) {
        Organization organization = getOrganization(organizationId);

        return organization.getEvents();
    }
}
