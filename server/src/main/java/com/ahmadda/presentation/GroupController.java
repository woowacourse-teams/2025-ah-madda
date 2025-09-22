package com.ahmadda.presentation;

import com.ahmadda.application.OrganizationGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group", description = "그룹 관련 API")
@RequestMapping("/api/organizations")
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final OrganizationGroupService organizationGroupService;

}
