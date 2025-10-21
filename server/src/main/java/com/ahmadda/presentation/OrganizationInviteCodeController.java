package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationInviteCodeService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.organization.InviteCode;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.presentation.dto.InviteCodeCreateResponse;
import com.ahmadda.presentation.dto.OrganizationResponse;
import com.ahmadda.presentation.resolver.Auth;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Organization InviteCode", description = "이벤트 스페이스 초대코드 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationInviteCodeController {

    private final OrganizationInviteCodeService organizationInviteCodeService;

    @Operation(summary = "이벤트 스페이스 초대코드 생성", description = "이벤트 스페이스의 초대코드를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = InviteCodeCreateResponse.class
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
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/organizations/{organizationId}/invite-codes"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "이벤트 스페이스에 참여 중인 구성원만 해당 이벤트 스페이스의 초대코드를 만들 수 있습니다.",
                                              "instance": "/api/organizations/{organizationId}/invite-codes"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이벤트 스페이스 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트 스페이스 정보입니다.",
                                                      "instance": "/api/organizations/{organizationId}/invite-codes"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원 정보입니다.",
                                                      "instance": "/api/organizations/{organizationId}/invite-codes"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @PostMapping("/{organizationId}/invite-codes")
    public ResponseEntity<InviteCodeCreateResponse> create(
            @PathVariable final Long organizationId,
            @Auth final LoginMember loginMember
    ) {
        InviteCode inviteCode =
                organizationInviteCodeService.createInviteCode(organizationId, loginMember, LocalDateTime.now());

        return ResponseEntity.ok(new InviteCodeCreateResponse(inviteCode.getCode(), inviteCode.getExpiresAt()));
    }

    @Operation(summary = "초대코드를 통해 이벤트 스페이스 조회", description = "초대코드를 통해 이벤트 스페이스를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(
                                    implementation = OrganizationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 초대코드",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "유효하지 않은 초대코드입니다.",
                                                      "instance": "/api/organizations/preview?inviteCode={inviteCode}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 초대코드",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "만료된 초대코드입니다.",
                                                      "instance": "/api/organizations/preview?inviteCode={inviteCode}"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/preview")
    public ResponseEntity<OrganizationResponse> getOrganizationByCode(
            @RequestParam final String inviteCode
    ) {
        Organization organization = organizationInviteCodeService.getOrganizationByCode(inviteCode);

        return ResponseEntity.ok(OrganizationResponse.from(organization));
    }
}
