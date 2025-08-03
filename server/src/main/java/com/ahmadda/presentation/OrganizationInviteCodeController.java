package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationInviteCodeService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.InviteCode;
import com.ahmadda.presentation.dto.InviteCodeCreateResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Organization InviteCode", description = "조직 초대코드 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationInviteCodeController {

    private final OrganizationInviteCodeService organizationInviteCodeService;

    @Operation(summary = "조직 초대코드 생성", description = "조직의 초대코드를 생성합니다.")
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
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/organizations/{organizationId}/invite_codes"
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
                                              "detail": "조직에 참여중인 조직원만 해당 조직의 초대코드를 만들 수 있습니다.",
                                              "instance": "/api/organizations/{organizationId}/invite_codes"
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
                                            name = "조직 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 조직 정보입니다.",
                                                      "instance": "/api/organizations/{organizationId}/invite_codes"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "조직원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 조직원 정보입니다.",
                                                      "instance": "/api/organizations/{organizationId}/invite_codes"
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @PostMapping("/{organizationId}/invite_codes")
    public ResponseEntity<InviteCodeCreateResponse> create(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        InviteCode inviteCode = organizationInviteCodeService.createInviteCode(organizationId, loginMember);

        return ResponseEntity.ok(new InviteCodeCreateResponse(inviteCode.getCode(), inviteCode.getExpiresAt()));
    }
}
