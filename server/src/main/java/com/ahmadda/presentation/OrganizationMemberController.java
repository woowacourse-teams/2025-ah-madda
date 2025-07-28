package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationMemberService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OrganizationMember", description = "조직원 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    @Operation(summary = "자신의 조직원 프로필 조회", description = "로그인한 사용자가 속한 조직에서의 자신의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = OrganizationMemberResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/profile"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 조직원입니다.",
                                              "instance": "/api/organizations/{organizationId}/profile"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}/profile")
    public ResponseEntity<OrganizationMemberResponse> getOrganizationMemberProfile(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        OrganizationMember organizationMember =
                organizationMemberService.getOrganizationMember(organizationId, loginMember);

        OrganizationMemberResponse response = OrganizationMemberResponse.from(organizationMember);

        return ResponseEntity.ok(response);
    }
}
