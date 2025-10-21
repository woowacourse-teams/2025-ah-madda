package com.ahmadda.presentation;

import com.ahmadda.application.OpenProfileService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OpenProfileUpdateRequest;
import com.ahmadda.domain.member.OpenProfile;
import com.ahmadda.presentation.dto.OpenProfileResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Open Profile", description = "오픈 프로필 관련 API")
@RestController
@RequestMapping("/api/open-profiles")
@RequiredArgsConstructor
public class OpenProfileController {

    private final OpenProfileService openProfileService;

    @Operation(summary = "자신의 오픈 프로필 조회", description = "로그인한 사용자의 오픈 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = OpenProfileResponse.class)
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
                                              "instance": "/api/open-profiles/profile"
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
                                            name = "회원이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 회원입니다.",
                                                      "instance": "/api/open-profiles/profile"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈 프로필이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 오픈 프로필입니다.",
                                                      "instance": "/api/open-profiles/profile"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<OpenProfileResponse> getOpenProfile(@AuthMember final LoginMember loginMember) {
        OpenProfile openProfile = openProfileService.getOpenProfile(loginMember);

        OpenProfileResponse response = OpenProfileResponse.from(openProfile);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "오픈 프로필 업데이트", description = "오픈 프로필의 닉네임과 그룹을 업데이트하고, 관련된 모든 조직 구성원 정보도 함께 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "오픈 프로필이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 프로필입니다.",
                                                      "instance": "/api/open-profiles/{openProfileId}"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "그룹이 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 그룹입니다.",
                                                      "instance": "/api/open-profiles/{openProfileId}"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @AuthMember final LoginMember loginMember,
            @Valid @RequestBody final OpenProfileUpdateRequest request
    ) {
        openProfileService.updateProfile(loginMember, request);

        return ResponseEntity.noContent()
                .build();
    }
}