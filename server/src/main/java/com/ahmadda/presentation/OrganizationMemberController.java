package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationMemberService;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.LoginMember;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    @GetMapping("/{organizationId}/profile")
    public ResponseEntity<OrganizationMemberResponse> getOrganizationProfile(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        OrganizationMember organizationMember =
                organizationMemberService.getOrganizationMember(organizationId, loginMember);

        OrganizationMemberResponse response = OrganizationMemberResponse.from(organizationMember);

        return ResponseEntity.ok(response);
    }
}
