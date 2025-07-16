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

    public List<Event> getOrganizerAvailableEvents(Long organizerId) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return eventRepository.findAllByOrganization_IdAndEventStartAfter(organizerId, currentDateTime);
    }

    public List<Event> getOwnersEvent(Long memberId, Long organizerId) {
        return eventRepository.findAllByOrganizer_Member_IdAndOrganization_Id(memberId, organizerId);
    }
}
