package com.ahmadda.presentation;

import com.ahmadda.application.GroupService;
import com.ahmadda.application.dto.GroupCreateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.organization.Group;
import com.ahmadda.presentation.dto.GroupCreateResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group", description = "그룹 관련 API")
@RequestMapping("/api/organizations")
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "신규 그룹 생성", description = "새로운 그룹을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = GroupCreateResponse.class)
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
                                                      "detail": "존재하지 않는 이벤트 스페이스 정보입니다.",
                                                      "instance": "/api/organizations"
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
                                                      "instance": "/api/organizations"
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
                                            name = "이벤트 스페이스의 구성원이 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "이벤트 스페이스의 구성원만 그룹을 만들 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}/groups"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이벤트 스페이스의 어드민이 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Forbidden",
                                                      "status": 403,
                                                      "detail": "어드민만 그룹을 만들 수 있습니다.",
                                                      "instance": "/api/organizations/{organizationId}/groups"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{organizationId}/groups")
    public ResponseEntity<GroupCreateResponse> createGroup(
            @PathVariable final Long organizationId,
            @RequestBody final GroupCreateRequest groupCreateRequest,
            @AuthMember final LoginMember loginMember
    ) {
        Group group = groupService.createGroup(organizationId, groupCreateRequest, loginMember);

        return ResponseEntity.ok(new GroupCreateResponse(group.getId()));
    }

}
