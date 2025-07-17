package com.ahmadda.application;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getOrganizationAvailableEvents(final Long organizationId) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return eventRepository.findAllByOrganization_IdAndEventStartAfter(organizationId, currentDateTime);
    }

    public List<Event> getOwnersEvent(final Long memberId, final Long organizationId) {
        return eventRepository.findAllByOrganizer_Member_IdAndOrganization_Id(memberId, organizationId);
    }
}
