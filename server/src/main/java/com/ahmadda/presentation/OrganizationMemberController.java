package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationMemberService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationMemberRoleUpdateRequest;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.presentation.dto.OrganizationMemberRenameRequest;
import com.ahmadda.presentation.dto.OrganizationMemberResponse;
import com.ahmadda.presentation.dto.OrganizationMembershipResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Organization Member", description = "구성원 관련 API")
@RestController
@RequestMapping("/api/organizations/{organizationId}")
@RequiredArgsConstructor
public class OrganizationMemberController {

    private final OrganizationMemberService organizationMemberService;

    @Operation(summary = "자신의 구성원 프로필 조회", description = "로그인한 사용자가 속한 이벤트 스페이스에서의 자신의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OrganizationMemberResponse.class)
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
                                              "detail": "존재하지 않는 구성원입니다.",
                                              "instance": "/api/organizations/{organizationId}/profile"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<OrganizationMemberResponse> getOrganizationMemberProfile(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        OrganizationMember organizationMember =
                organizationMemberService.getOrganizationMember(organizationId, loginMember);

        OrganizationMemberResponse response = OrganizationMemberResponse.from(organizationMember);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "구성원 역할 일괄 변경",
            description = "관리자가 같은 이벤트 스페이스에 속한 여러 구성원의 역할을 한 번에 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
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
                                              "instance": "/api/organizations/{organizationId}/organization-members/roles"
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
                                            name = "일부 구성원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 선택된 구성원입니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/roles"
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
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/roles"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "같은 이벤트 스페이스 소속이 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "같은 이벤트 스페이스에 속한 구성원만 권한을 변경할 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/roles"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "관리자 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "관리자만 구성원의 권한을 변경할 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/roles"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unprocessable Entity",
                                              "status": 422,
                                              "detail": "서로 다른 이벤트 스페이스에 속한 구성원이 포함되어 있습니다.",
                                              "instance": "/api/organizations/{organizationId}/organization-members/roles"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/organization-members/roles")
    public ResponseEntity<Void> updateRoles(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember,
            @Valid @RequestBody final OrganizationMemberRoleUpdateRequest request
    ) {
        organizationMemberService.updateRoles(organizationId, loginMember, request);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "이벤트 스페이스의 모든 구성원 목록 조회", description = "이벤트 스페이스에 속한 모든 구성원의 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationMemberResponse.class)
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
                                              "instance": "/api/organizations/{organizationId}/organization-members"
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
                                              "detail": "이벤트 스페이스에 속한 구성원만 구성원의 목록을 조회할 수 있습니다.",
                                              "instance": "/api/organizations/{organizationId}/organization-members"
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
                                              "detail": "존재하지 않는 이벤트 스페이스입니다.",
                                              "instance": "/api/organizations/{organizationId}/organization-members"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/organization-members")
    public ResponseEntity<List<OrganizationMemberResponse>> getAllOrganizationMembers(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        List<OrganizationMember> organizationMembers =
                organizationMemberService.getAllOrganizationMembers(organizationId, loginMember);

        List<OrganizationMemberResponse> response = organizationMembers.stream()
                .map(OrganizationMemberResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 스페이스의 가입 확인", description = "이벤트 스페이스에 속해 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OrganizationMembershipResponse.class)
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
                                              "instance": "/api/organizations/{organizationId}/organization-members/rename"
                                            }
                                            """
                            )
                    )
            ),
    })
    @GetMapping("/membership")
    public ResponseEntity<OrganizationMembershipResponse> getMembership(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        boolean isMember = organizationMemberService.isMember(organizationId, loginMember);

        OrganizationMembershipResponse response = new OrganizationMembershipResponse(isMember);

        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
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
                                              "instance": "/api/organizations/{organizationId}/organization-members/rename"
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
                                              "detail": "존재하지 않는 구성원입니다.",
                                              "instance": "/api/organizations/{organizationId}/organization-members/rename"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "중복 이름",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이미 사용 중인 닉네임입니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/rename"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "현재 같은 닉네임",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "현재 닉네임과 동일하여 변경할 수 없습니다.",
                                                      "instance": "/api/organizations/{organizationId}/organization-members/rename"
                                                    }
                                                    """)
                            }
                    )
            )
    })
    @PatchMapping("/organization-members/rename")
    public ResponseEntity<Void> organizationMemberRenameNickname(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember,
            @Valid @RequestBody final OrganizationMemberRenameRequest request
    ) {
        organizationMemberService.renameOrganizationMemberNickname(organizationId, loginMember, request.nickname());

        return ResponseEntity.noContent()
                .build();
    }
}
