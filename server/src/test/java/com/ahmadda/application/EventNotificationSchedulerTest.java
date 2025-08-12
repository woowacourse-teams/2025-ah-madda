package com.ahmadda.application;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Reminder;
import com.ahmadda.domain.ReminderHistoryRepository;
import com.ahmadda.domain.ReminderRecipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventNotificationSchedulerTest {

    @Autowired
    private EventNotificationScheduler sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @MockitoSpyBean
    private Reminder reminder;

    @Autowired
    private ReminderHistoryRepository reminderHistoryRepository;

    @ParameterizedTest
    @MethodSource("registrationEndOffsets")
    void 등록_마감_임박_이벤트에_대해_비게스트에게_알람을_전송한다(
            int minutesUntilRegistrationEnds,
            boolean expectToSend
    ) {
        // given
        var organization = organizationRepository.save(Organization.create("조직", "설명", "img.png"));
        var host = saveOrganizationMember("주최자", "host@email.com", organization);
        var ng1 = saveOrganizationMember("비게스트1", "ng1@email.com", organization);
        var ng2 = saveOrganizationMember("비게스트2", "ng2@email.com", organization);

        var now = LocalDateTime.now();

        var registrationEnd = now.plusMinutes(minutesUntilRegistrationEnds);

        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, organization,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        registrationEnd,
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        // when
        sut.notifyRegistrationClosingEvents();

        // then
        if (expectToSend) {
            verify(reminder).remind(List.of(ng1, ng2), event, "이벤트 신청 마감이 임박했습니다.");
        } else {
            verify(reminder, Mockito.never()).remind(any(), any(), any());
        }
    }

    @Test
    void 등록_마감_임박_이벤트의_리마인더_호출_후_히스토리가_저장된다() {
        // given
        var org = organizationRepository.save(Organization.create("조직", "설명", "img.png"));
        var host = saveOrganizationMember("주최자", "host@email.com", org);
        var ng1 = saveOrganizationMember("비게스트1", "ng1@email.com", org);
        var ng2 = saveOrganizationMember("비게스트2", "ng2@email.com", org);

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, org,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        now.plusMinutes(4),
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var content = "이벤트 신청 마감이 임박했습니다.";

        // when
        sut.notifyRegistrationClosingEvents();

        // then
        var savedHistories = reminderHistoryRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(savedHistories)
                    .hasSize(1);

            var history = savedHistories.get(0);
            softly.assertThat(history.getEvent())
                    .isEqualTo(event);
            softly.assertThat(history.getContent())
                    .isEqualTo("이벤트 신청 마감이 임박했습니다.");
            softly.assertThat(history.getSentAt())
                    .isNotNull();

            softly.assertThat(history.getRecipients())
                    .extracting(ReminderRecipient::getOrganizationMember)
                    .containsExactlyInAnyOrder(ng1, ng2);
        });
    }

    @Test
    void 정원이_다_찼다면_알림을_전송하지_않는다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직", "설명", "img.png"));
        var host = saveOrganizationMember("주최자", "host@email.com", organization);

        saveOrganizationMember("비게스트1", "ng1@email.com", organization);
        saveOrganizationMember("비게스트2", "ng2@email.com", organization);

        var now = LocalDateTime.now();
        var registrationEnd = now.plusMinutes(4);

        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, organization,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        registrationEnd,
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                2
        ));

        saveGuest(event, saveOrganizationMember("게스트1", "g1@email.com", organization));
        saveGuest(event, saveOrganizationMember("게스트2", "g2@email.com", organization));

        // when
        sut.notifyRegistrationClosingEvents();

        // then
        verify(reminder, Mockito.never()).remind(any(), any(), any());
    }

    private static Stream<Arguments> registrationEndOffsets() {
        return Stream.of(
                Arguments.of(4, true),
                Arguments.of(5, true),
                Arguments.of(6, false),
                Arguments.of(-1, false)
        );
    }

    private OrganizationMember saveOrganizationMember(
            String nickname,
            String email,
            Organization organization
    ) {
        var member = memberRepository.save(Member.create(nickname, email, "testPicture"));

        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, organization));
    }

    private Guest saveGuest(Event event, OrganizationMember participant) {
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        return guestRepository.save(guest);
    }
}
