package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.domain.Organization;
import com.ahmadda.presentation.dto.OrganizationReadResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<Void> createOrganization(
            @RequestBody @Valid final OrganizationCreateRequest organizationCreateRequest) {
        Organization organization = organizationService.createOrganization(organizationCreateRequest);

        return ResponseEntity.created(URI.create("/api/organizations/" + organization.getId()))
                .build();
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationReadResponse> readOrganization(@PathVariable final Long organizationId) {
        Organization organization = organizationService.getOrganization(organizationId);
        OrganizationReadResponse organizationReadResponse = OrganizationReadResponse.from(organization);

        return ResponseEntity.ok(organizationReadResponse);
    }
}
