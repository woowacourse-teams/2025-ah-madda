package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.domain.Organization;
import com.ahmadda.presentation.dto.OrganizationCreateResponse;
import com.ahmadda.presentation.dto.OrganizationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Organization", description = "조직 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationCreateResponse> createOrganization(
            @RequestBody @Valid final OrganizationCreateRequest organizationCreateRequest
    ) {
        Organization organization = organizationService.createOrganization(organizationCreateRequest);

        return ResponseEntity.created(URI.create("/api/organizations/" + organization.getId()))
                .body(new OrganizationCreateResponse(organization.getId()));
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> readOrganization(@PathVariable final Long organizationId) {
        Organization organization = organizationService.getOrganization(organizationId);
        OrganizationResponse organizationResponse = OrganizationResponse.from(organization);

        return ResponseEntity.ok(organizationResponse);
    }

    //TODO 07.25 이후 리팩터링 및 제거하기
    @Deprecated
    @GetMapping("/woowacourse")
    public ResponseEntity<OrganizationResponse> getOrganization() {
        Organization organization = organizationService.alwaysGetWoowacourse();
        OrganizationResponse organizationResponse = OrganizationResponse.from(organization);

        return ResponseEntity.ok(organizationResponse);
    }
}
