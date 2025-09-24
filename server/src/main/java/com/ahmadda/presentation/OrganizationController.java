package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.dto.OrganizationUpdateRequest;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.presentation.dto.OrganizationCreateResponse;
import com.ahmadda.presentation.dto.OrganizationParticipateRequest;
import com.ahmadda.presentation.dto.OrganizationParticipateResponse;
import com.ahmadda.presentation.dto.OrganizationResponse;
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
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Tag(name = "Organization", description = "이벤트 스페이스 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "신규 이벤트 스페이스 생성", description = "새로운 이벤트 스페이스를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            schema = @Schema(implementation = OrganizationCreateResponse.class)
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
                                              "detail": "존재하지 않는 회원입니다",
                                              "instance": "/api/organizations"
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
                                            name = "이름의 길이가 맞지 않음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이름의 길이는 1자 이상 30자 이하이어야 합니다.",
                                                      "instance": "/api/organizations"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "설명의 길이가 맞지 않음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "설명의 길이는 1자 이상 30자 이하이어야 합니다.",
                                                      "instance": "/api/organizations"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrganizationCreateResponse> createOrganization(
            @RequestPart("organization") @Valid OrganizationCreateRequest organizationCreateRequest,
            @RequestPart("thumbnail") MultipartFile multipartFile,
            @AuthMember LoginMember loginMember
    ) throws IOException {
        OrganizationImageFile thumbnailOrganizationImageFile = OrganizationImageFile.create(
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                multipartFile.getSize(),
                multipartFile.getInputStream()
        );

        Organization organization = organizationService.createOrganization(
                organizationCreateRequest,
                thumbnailOrganizationImageFile,
                loginMember
        );

        return ResponseEntity.created(URI.create("/api/organizations/" + organization.getId()))
                .body(new OrganizationCreateResponse(organization.getId()));
    }

    @Operation(summary = "이벤트 스페이스 정보 조회", description = "이벤트 스페이스 ID로 특정 이벤트 스페이스의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OrganizationResponse.class)
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
                                              "instance": "/api/organizations/{organizationId}"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> readOrganization(@PathVariable final Long organizationId) {
        Organization organization = organizationService.getOrganizationById(organizationId);
        OrganizationResponse organizationResponse = OrganizationResponse.from(organization);

        return ResponseEntity.ok(organizationResponse);
    }

    @Operation(summary = "이벤트 스페이스 참여", description = "사용자가 특정 이벤트 스페이스에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OrganizationParticipateResponse.class)
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
                                              "instance": "/api/organizations/{organizationId}/participation"
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
                                                      "detail": "존재하지 않는 이벤트 스페이스입니다.",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "회원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 회원입니다",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이미 참여한 이벤트 스페이스",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이미 참여한 이벤트 스페이스입니다.",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 스페이스의 초대코드가 아니거나 없는 경우",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "잘못된 초대코드입니다.",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "만료된 초대코드인 경우",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "초대코드가 만료되었습니다.",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 스페이스가 정원이 가득 찬 경우",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이벤트 스페이스에 이미 정원이 가득차 참여할 수 없습니다.",
                                                      "instance": "/api/organizations/{organizationId}/participation"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{organizationId}/participation")
    public ResponseEntity<OrganizationParticipateResponse> participateOrganization(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember,
            @Valid @RequestBody final OrganizationParticipateRequest organizationParticipateRequest
    ) {
        OrganizationMember organizationMember =
                organizationService.participateOrganization(
                        organizationId, loginMember,
                        organizationParticipateRequest
                );

        return ResponseEntity.ok(
                new OrganizationParticipateResponse(
                        organizationMember.getId(),
                        organizationMember.getNickname()
                ));
    }

    @Operation(summary = "이벤트 스페이스 수정", description = "이벤트 스페이스를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트 스페이스",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트 스페이스입니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 구성원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    ),

                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "이름의 길이가 맞지 않음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이름의 길이는 1자 이상 30자 이하이어야 합니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "설명의 길이가 맞지 않음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "설명의 길이는 1자 이상 30자 이하이어야 합니다.",
                                                      "instance": "/api/organizations/{organizationId}"
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
                                            name = "이벤트 스페이스에 속한 구성원이 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "이벤트 스페이스에 속한 구성원만 수정이 가능합니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 스페이스의 관리자가 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "구성원의 관리자만 이벤트 스페이스 정보를 수정할 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping(value = "/{organizationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateOrganization(
            @RequestPart("organization") @Valid final OrganizationUpdateRequest organizationUpdateRequest,
            @Nullable @RequestPart(value = "thumbnail", required = false) final MultipartFile multipartFile,
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) throws IOException {
        OrganizationImageFile thumbnailOrganizationImageFile = null;
        if (multipartFile != null) {
            thumbnailOrganizationImageFile = OrganizationImageFile.create(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    multipartFile.getSize(),
                    multipartFile.getInputStream()
            );
        }

        organizationService.updateOrganization(
                organizationId,
                organizationUpdateRequest,
                thumbnailOrganizationImageFile,
                loginMember
        );

        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "내가 참여 중인 이벤트 스페이스 목록 조회", description = "로그인한 사용자가 참여 중인 이벤트 스페이스 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationResponse.class))
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
                                              "instance": "/api/organizations/participated"
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
                                              "detail": "존재하지 않는 회원입니다",
                                              "instance": "/api/organizations/participated"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/participated")
    public ResponseEntity<List<OrganizationResponse>> getParticipatedOrganizations(
            @AuthMember final LoginMember loginMember
    ) {
        List<Organization> participatingOrganizations = organizationService.getParticipatingOrganizations(loginMember);

        List<OrganizationResponse> organizationResponses = participatingOrganizations.stream()
                .map(OrganizationResponse::from)
                .toList();

        return ResponseEntity.ok(organizationResponses);
    }

    @Operation(summary = "이벤트 스페이스 삭제", description = "이벤트 스페이스를 삭제합니다.")
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
                                              "instance": "/api/organizations/{organizationId}"
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
                                              "detail": "이벤트 스페이스의 관리자만 삭제할 수 있습니다.",
                                              "instance": "/api/organizations/{organizationId}"
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
                                            name = "존재하지 않는 이벤트 스페이스",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트 스페이스입니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 구성원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 구성원입니다.",
                                                      "instance": "/api/organizations/{organizationId}"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<Void> deleteOrganization(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember
    ) {
        organizationService.deleteOrganization(organizationId, loginMember);

        return ResponseEntity.noContent()
                .build();
    }
}
