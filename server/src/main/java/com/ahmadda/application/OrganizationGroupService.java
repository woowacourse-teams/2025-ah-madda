package com.ahmadda.application;

import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationGroupService {

    private final OrganizationGroupRepository organizationGroupRepository;

    @Transactional(readOnly = true)
    public List<OrganizationGroup> findAll() {
        return organizationGroupRepository.findAll();
    }
}
