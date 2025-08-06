package com.ahmadda.application;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationToken;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @MockitoBean
    private EmailNotifier emailNotifier;

    @MockitoBean
    private PushNotifier pushNotifier;

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

        saveFcmRegistrationToken(ng1, "token-ng1");
        saveFcmRegistrationToken(ng2, "token-ng2");

        // when
        sut.notifyRegistrationClosingEvents();

        // then
        var expectedEmail = EventEmailPayload.of(event, "이벤트 신청 마감이 임박했습니다.");
        var expectedPush = PushNotificationPayload.of(event, "이벤트 신청 마감이 임박했습니다.");

        if (expectToSend) {
            verify(emailNotifier).sendEmails(List.of(ng1, ng2), expectedEmail);
            verify(pushNotifier).sendPushs(List.of(ng1, ng2), expectedPush);
        } else {
            verify(emailNotifier, Mockito.never()).sendEmails(any(), any());
            verify(pushNotifier, Mockito.never()).sendPushs(any(), any());
        }
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

    private void saveFcmRegistrationToken(OrganizationMember organizationMember, String token) {
        var fcmToken = FcmRegistrationToken.create(
                organizationMember.getMember()
                        .getId(),
                token,
                LocalDateTime.now()
        );

        fcmRegistrationTokenRepository.save(fcmToken);
    }
}
