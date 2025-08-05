package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.EventStatistic;
import com.ahmadda.domain.EventStatisticRepository;
import com.ahmadda.domain.EventViewMetric;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventStatisticService {

    private final EventStatisticRepository eventStatisticRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final EventRepository eventRepository;

    public List<EventViewMetric> getEventStatistic(final Long eventId, final LoginMember loginMember) {
        EventStatistic eventStatistic = getEventStatistic(eventId);
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember organizationMember = getOrganizationMember(loginMember, organization);

        return eventStatistic.findEventViewMetrics(organizationMember, LocalDate.now());
    }

    private OrganizationMember getOrganizationMember(final LoginMember loginMember, final Organization organization) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organization.getId(),
                        loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow((() -> new NotFoundException("존재하지 않는 이벤트입니다")));
    }

    private EventStatistic getEventStatistic(final Long eventId) {
        return eventStatisticRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트 조회수 정보를 가져오는데 실패하였습니다."));
    }
}
