package com.ahmadda.application;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    public List<Event> getJoinedEvents(Long memberId, Long organizationId) {
        List<Guest> guests =
                guestRepository.findAllByParticipant_Member_IdAndParticipant_Organization_Id(memberId, organizationId);

        return guests.stream()
                .map(Guest::getEvent)
                .toList();
    }
}
