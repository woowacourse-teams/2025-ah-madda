package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final OrganizationService organizationService;
    private final MemberRepository memberRepository;

    public List<Event> getOrganizationAvailableEvents(final Long organizationId) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        Organization organization = organizationService.getOrganization(organizationId);

        return organization.getEvents()
                .stream()
                .filter((event) -> event.getEventStart().isAfter(currentDateTime))
                .toList();
    }

    public List<Event> getOwnersEvent(final Long memberId, final Long organizationId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessFlowViolatedException("존재하지 않는 멤버입니다.");
        }

        Organization organization = organizationService.getOrganization(organizationId);

        return organization.getEvents()
                .stream()
                .filter((event -> event.isOwner(memberId)))
                .toList();
    }
}
