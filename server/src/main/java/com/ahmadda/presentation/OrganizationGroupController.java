package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationGroupService;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.presentation.dto.OrganizationGroupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Group", description = "그룹 관련 API")
@RequestMapping("/api/organization-groups")
@RestController
@RequiredArgsConstructor
public class OrganizationGroupController {

    private final OrganizationGroupService organizationGroupService;

    @Operation(summary = "그룹 전체 조회", description = "기본적으로 이벤트 스페이스가 가지는 그룹을 모두 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조직 그룹 리스트 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationGroupResponse.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<OrganizationGroupResponse>> getOrganizationGroups() {
        List<OrganizationGroup> organizationGroups = organizationGroupService.findAll();

        List<OrganizationGroupResponse> groupResponses = organizationGroups.stream()
                .map(organizationGroup -> new OrganizationGroupResponse(
                        organizationGroup.getId(),
                        organizationGroup.getName()
                ))
                .toList();

        return ResponseEntity.ok(groupResponses);
    }
}
