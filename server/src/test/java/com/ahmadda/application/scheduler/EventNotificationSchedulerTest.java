package com.ahmadda.application.scheduler;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventReminderGroup;
import com.ahmadda.domain.event.EventReminderGroupRepository;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.EventNotificationOptOut;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import com.ahmadda.domain.notification.ReminderRecipient;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class EventNotificationSchedulerTest extends IntegrationTest {

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

    @Autowired
    private EventNotificationOptOutRepository eventNotificationOptOutRepository;

    @Autowired
    private EventReminderGroupRepository eventReminderGroupRepository;

    @Autowired
    private ReminderHistoryRepository reminderHistoryRepository;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @ParameterizedTest
    @MethodSource("registrationEndOffsets")
    void 등록_마감_30분전_리마인더그룹의_수신가능한_비게스에게만_알람을_보낸다(
            int minutesUntilRegistrationEnds,
            boolean expectToSend
    ) {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
        var group = createGroup();
        var host = saveOrganizationMember("주최자", "host@email.com", organization, group);
        var ng1 = saveOrganizationMember("비게스트1", "ng1@email.com", organization, group);
        var ng2 = saveOrganizationMember("비게스트2", "ng2@email.com", organization, group);

        var now = LocalDateTime.now();

        var registrationEnd = now.plusMinutes(30)
                .plusMinutes(minutesUntilRegistrationEnds);

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
                100,
                false
        ));
        eventReminderGroupRepository.save(EventReminderGroup.create(event, group));
        var ng2OptOut =
                eventNotificationOptOutRepository.save(EventNotificationOptOut.create(ng2, event));
        eventNotificationOptOutRepository.save(ng2OptOut);

        // when
        sut.notifyRegistrationClosingIn30Minutes();

        // then
        if (expectToSend) {
            verify(reminder).remind(List.of(ng1), event, "이벤트 신청 마감이 임박했습니다.");
        } else {
            verify(reminder, Mockito.never()).remind(any(), any(), any());
        }
    }

    @Test
    void 등록_마감_30분_전_리마인더_호출_후_히스토리가_저장된다() {
        // given
        var org = organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
        var group = createGroup();
        var host = saveOrganizationMember("주최자", "host@email.com", org, group);
        var ng1 = saveOrganizationMember("비게스트1", "ng1@email.com", org, group);
        var ng2 = saveOrganizationMember("비게스트2", "ng2@email.com", org, group);

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, org,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        now.plusMinutes(34),
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                100,
                false
        ));
        eventReminderGroupRepository.save(EventReminderGroup.create(event, group));

        // when
        sut.notifyRegistrationClosingIn30Minutes();

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
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
        var group = createGroup();
        var host = saveOrganizationMember("주최자", "host@email.com", organization, group);

        saveOrganizationMember("비게스트1", "ng1@email.com", organization, group);
        saveOrganizationMember("비게스트2", "ng2@email.com", organization, group);

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
                2,
                false
        ));

        saveGuest(event, saveOrganizationMember("게스트1", "g1@email.com", organization, group));
        saveGuest(event, saveOrganizationMember("게스트2", "g2@email.com", organization, group));

        // when
        sut.notifyRegistrationClosingIn30Minutes();

        // then
        verify(reminder, Mockito.never()).remind(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("eventStartOffsets")
    void 이벤트_시작_24시간_전_수신_거부하지_않는_게스트에게만_알람을_전송한다(
            int minutesFrom24hOffset,
            boolean expectToSend
    ) {
        // given
        var org = organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
        var group = createGroup();
        var host = saveOrganizationMember("주최자", "host@email.com", org, group);

        var now = LocalDateTime.now();
        var eventStart = now.plusHours(24)
                .plusMinutes(minutesFrom24hOffset);

        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, org,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        now.plusMinutes(4),
                        eventStart,
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                100,
                false
        ));

        var guest1 = saveOrganizationMember("게스트1", "g1@email.com", org, group);
        var guest2 = saveOrganizationMember("게스트2", "g2@email.com", org, group);
        saveGuest(event, guest1);
        saveGuest(event, guest2);
        saveOrganizationMember("비게스트", "ng@email.com", org, group);

        // when
        sut.notifyEventStartIn24Hours();

        // then
        if (expectToSend) {
            verify(reminder).remind(List.of(guest1, guest2), event, "내일 이벤트가 시작됩니다. 준비되셨나요?");
        } else {
            verify(reminder, Mockito.never()).remind(
                    Mockito.any(),
                    Mockito.any(),
                    Mockito.eq("내일 이벤트가 시작됩니다. 준비되셨나요?")
            );
        }
    }

    @Test
    void 이벤트_시작_24시간_전_리마인더_호출_후_히스토리가_저장된다() {
        // given
        var org = organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
        var group = createGroup();
        var host = saveOrganizationMember("주최자", "host@email.com", org, group);

        var now = LocalDateTime.now();
        var eventStart = now.plusHours(24)
                .plusMinutes(3);

        var event = eventRepository.save(Event.create(
                "이벤트", "설명", "장소",
                host, org,
                EventOperationPeriod.create(
                        now.minusDays(2),
                        now.plusMinutes(4),
                        eventStart,
                        now.plusDays(2),
                        now.minusDays(3)
                ),
                100,
                false
        ));

        var g1 = saveOrganizationMember("게스트1", "g1@email.com", org, group);
        var g2 = saveOrganizationMember("게스트2", "g2@email.com", org, group);
        saveGuest(event, g1);
        saveGuest(event, g2);

        var ng2OptOut =
                eventNotificationOptOutRepository.save(EventNotificationOptOut.create(g2, event));
        eventNotificationOptOutRepository.save(ng2OptOut);

        // when
        sut.notifyEventStartIn24Hours();

        // then
        var savedHistories = reminderHistoryRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(savedHistories)
                    .hasSize(1);

            var history = savedHistories.get(0);
            softly.assertThat(history.getEvent())
                    .isEqualTo(event);
            softly.assertThat(history.getContent())
                    .isEqualTo("내일 이벤트가 시작됩니다. 준비되셨나요?");
            softly.assertThat(history.getSentAt())
                    .isNotNull();

            softly.assertThat(history.getRecipients())
                    .extracting(ReminderRecipient::getOrganizationMember)
                    .containsExactlyInAnyOrder(g1);
        });
    }

    private static Stream<Arguments> registrationEndOffsets() {
        return Stream.of(
                Arguments.of(1, true),
                Arguments.of(4, true),
                Arguments.of(5, true),
                Arguments.of(6, false),
                Arguments.of(-1, false)
        );
    }

    private static Stream<Arguments> eventStartOffsets() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(4, true),
                Arguments.of(6, false),
                Arguments.of(-1, false)
        );
    }

    private OrganizationMember saveOrganizationMember(
            String nickname,
            String email,
            Organization organization,
            OrganizationGroup group
    ) {
        var member = memberRepository.save(Member.create(nickname, email, "testPicture"));

        return organizationMemberRepository.save(OrganizationMember.create(
                nickname,
                member,
                organization,
                OrganizationMemberRole.USER,
                group
        ));
    }

    private Guest saveGuest(Event event, OrganizationMember participant) {
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        return guestRepository.save(guest);
    }

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
