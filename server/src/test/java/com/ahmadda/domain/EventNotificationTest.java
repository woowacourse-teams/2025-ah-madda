package com.ahmadda.domain;

import com.ahmadda.infra.mail.MockNotificationMailer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(OutputCaptureExtension.class)
class EventNotificationTest {

    private final EventNotification sut = new EventNotification(new MockNotificationMailer());

    @Test
    void 수신자_목록에게_이메일을_전송한다(CapturedOutput output) {
        // given
        var organizationName = "조직명";
        var organizerNickname = "주최자";
        var eventTitle = "이벤트제목";
        var content = "이메일 본문입니다.";

        var organization = Organization.create(organizationName, "설명", "img.png");
        var organizer = createOrganizationMember(organizerNickname, "host@email.com", organization);

        var now = LocalDateTime.now();
        var event = Event.create(
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
                organizerNickname,
                100
        );

        var om1 = createOrganizationMember("수신자1", "r1@email.com", organization);
        var om2 = createOrganizationMember("수신자2", "r2@email.com", organization);
        var recipients = List.of(om1, om2);

        // when
        sut.sendEmails(event, recipients, content);

        // then
        assertSoftly(softly -> {
            softly.assertThat(output)
                    .contains(om1.getMember()
                            .getEmail());
            softly.assertThat(output)
                    .contains(om2.getMember()
                            .getEmail());
            softly.assertThat(output)
                    .contains(String.format(
                            "[%s] %s님의 이벤트 안내: %s",
                            organizationName,
                            organizerNickname,
                            eventTitle
                    ));
            softly.assertThat(output)
                    .contains(content);
        });
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            String email,
            Organization organization
    ) {
        var member = Member.create(nickname, email);

        return OrganizationMember.create(nickname, member, organization);
    }
}
