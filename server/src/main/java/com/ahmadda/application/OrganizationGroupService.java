package com.ahmadda.application;

import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationGroupService {

    private final OrganizationGroupRepository organizationGroupRepository;

    public List<OrganizationGroup> findAll() {
        return organizationGroupRepository.findAll();
    }
}
