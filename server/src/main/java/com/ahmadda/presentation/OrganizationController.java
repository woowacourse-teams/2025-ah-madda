package com.ahmadda.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmadda.application.OrganizationService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.domain.Organization;
import com.ahmadda.presentation.dto.OrganizationCreateResponse;
import com.ahmadda.presentation.dto.OrganizationResponse;
import com.ahmadda.presentation.dto.ParticipateRequestDto;
import com.ahmadda.presentation.resolver.AuthMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Organization", description = "조직 관련 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "신규 조직 생성", description = "새로운 조직을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Bad Request",
                                              "status": 400,
                                              "detail": "이름은 공백이면 안됩니다.",
                                              "instance": "/api/organizations"
                                            }
                                            """
                            )
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
                                              "detail": "이름의 길이는 2자 이상 20자 이하이어야 합니다.",
                                              "instance": "/api/organizations"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<OrganizationCreateResponse> createOrganization(
            @RequestBody @Valid final OrganizationCreateRequest organizationCreateRequest
    ) {
        Organization organization = organizationService.createOrganization(organizationCreateRequest);

        return ResponseEntity.created(URI.create("/api/organizations/" + organization.getId()))
                .body(new OrganizationCreateResponse(organization.getId()));
    }

    @Operation(summary = "조직 정보 조회", description = "조직 ID로 특정 조직의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 조직입니다.",
                                              "instance": "/api/organizations/{organizationId}"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> readOrganization(@PathVariable final Long organizationId) {
        Organization organization = organizationService.getOrganization(organizationId);
        OrganizationResponse organizationResponse = OrganizationResponse.from(organization);

        return ResponseEntity.ok(organizationResponse);
    }

    //TODO 07.25 이후 리팩터링 및 제거하기
    @Deprecated
    @Operation(summary = "우아코스 조직 정보 조회 (임시)", description = "항상 우아코스 조직 정보를 반환하는 임시 API입니다. 추후 제거될 예정입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping("/woowacourse")
    public ResponseEntity<OrganizationResponse> getOrganization() {
        Organization organization = organizationService.alwaysGetWoowacourse();
        OrganizationResponse organizationResponse = OrganizationResponse.from(organization);

        return ResponseEntity.ok(organizationResponse);
    }

    @Operation(summary = "조직 참여", description = "사용자가 특정 조직에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
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
                                              "instance": "/api/organizations/{organizationId}/participation"
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
                                              "detail": "존재하지 않는 조직입니다.",
                                              "instance": "/api/organizations/{organizationId}/participation"
                                            }
                                            """
                            )
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
                                              "detail": "이미 참여한 조직입니다.",
                                              "instance": "/api/organizations/{organizationId}/participation"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{organizationId}/participation")
    public ResponseEntity<OrganizationResponse> participateOrganization(
            @PathVariable final Long organizationId,
            @AuthMember final LoginMember loginMember,
            @RequestBody final ParticipateRequestDto participateRequestDto
    ) {
        organizationService.participateOrganization(organizationId, loginMember, participateRequestDto);

        return ResponseEntity.ok()
                .build();
    }
}
