package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
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
import com.ahmadda.domain.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(
        properties = {
                "mail.mock=true"
        })
@Transactional
class EventNotificationServiceTest {

    @Autowired
    private EventNotificationService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void 비게스트_조직원에게_이메일을_전송한다(CapturedOutput output) {
        // given
        var organizationName = "조직명";
        var organizerNickname = "주최자";
        var eventTitle = "이벤트제목";
        var emailContent = "이메일 내용입니다.";

        var organization = organizationRepository.save(Organization.create(organizationName, "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember(organizerNickname, "host@email.com", organization);
        LocalDateTime now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                eventTitle,
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        Period.create(now.minusDays(2), now.minusDays(1)),
                        Period.create(now.plusDays(1), now.plusDays(2)),
                        now.minusDays(3)
                ),
                100
        ));

        var guestEmail = "guest@email.com";
        guestRepository.save(Guest.create(
                event,
                createAndSaveOrganizationMember("게스트", guestEmail, organization),
                event.getRegistrationStart()
        ));

        var nonGuest1Email = "ng1@email.com";
        var nonGuest2Email = "ng2@email.com";
        createAndSaveOrganizationMember("비게스트1", nonGuest1Email, organization);
        createAndSaveOrganizationMember("비게스트2", nonGuest2Email, organization);

        // when
        sut.notifyNonGuestOrganizationMembers(event.getId(), emailContent);

        // then
        assertSoftly(softly -> {
            softly.assertThat(output)
                    .contains("To: " + nonGuest1Email);
            softly.assertThat(output)
                    .contains("To: " + nonGuest2Email);
            softly.assertThat(output)
                    .doesNotContain("To: " + guestEmail);
            softly.assertThat(output)
                    .contains("Subject: " + String.format(
                            "[%s] %s님의 이벤트 안내: %s",
                            organizationName,
                            organizerNickname,
                            eventTitle
                    ));
            softly.assertThat(output)
                    .contains("Content: " + emailContent);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_메일_전송시_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.notifyNonGuestOrganizationMembers(999L, "이메일 내용입니다."))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }


    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            String email,
            Organization organization
    ) {
        var member = memberRepository.save(Member.create(nickname, email));

        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, organization));
    }
}
