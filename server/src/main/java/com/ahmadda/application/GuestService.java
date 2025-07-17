package com.ahmadda.application;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final OrganizationService organizationService;

    public List<Event> getJoinedEvents(final Long memberId, final Long organizationId) {
        Organization organization = organizationService.getOrganization(organizationId);
        List<Guest> guests = organization.getGuests();

        return guests.stream()
                .filter((guest) -> guest.getParticipant().isSameMember(memberId))
                .map(Guest::getEvent)
                .toList();
    }
}
