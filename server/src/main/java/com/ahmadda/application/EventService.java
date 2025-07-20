package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public Event createEvent(
            final Long organizationId,
            final Long organizerId,
            final EventCreateRequest eventCreateRequest
    ) {
        Organization organization = getOrganization(organizationId);
        OrganizationMember organizer = getOrganizationMember(organizerId);

        EventOperationPeriod eventOperationPeriod = EventOperationPeriod.create(
                new Period(eventCreateRequest.registrationStart(), eventCreateRequest.registrationEnd()),
                new Period(eventCreateRequest.eventStart(), eventCreateRequest.eventEnd()),
                LocalDateTime.now()
        );
        Event event = Event.create(
                eventCreateRequest.title(),
                eventCreateRequest.description(),
                eventCreateRequest.place(),
                organizer,
                organization,
                eventOperationPeriod,
                eventCreateRequest.maxCapacity()
        );

        return eventRepository.save(event);
    }

    private Organization getOrganization(final Long organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(organizationId + "에 해당하는 조직을 찾을 수 없습니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException(organizationMemberId + "에 해당하는 조직원을 찾을 수 없습니다."));
    }
}
