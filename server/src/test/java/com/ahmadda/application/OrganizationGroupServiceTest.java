package com.ahmadda.application;

import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationGroupServiceTest extends IntegrationTest {

    @Autowired
    private OrganizationGroupService sut;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 모든_그룹을_조회할_수_있다() {
        //given
        var group1 = createGroup("코치");
        var group2 = createGroup("백엔드");
        var group3 = createGroup("프론트");

        //when
        var organizationGroups = sut.findAll();

        //then
        assertThat(organizationGroups).isEqualTo(List.of(group1, group2, group3));
    }

    private OrganizationGroup createGroup(String name) {
        return organizationGroupRepository.save(OrganizationGroup.create(name));
    }
}
