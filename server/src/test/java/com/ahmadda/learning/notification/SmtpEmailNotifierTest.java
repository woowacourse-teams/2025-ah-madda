package com.ahmadda.learning.notification;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Disabled
@IntegrationTest
@TestPropertySource(properties = "mail.mock=false")
class SmtpEmailNotifierTest {

    @Autowired
    private EmailNotifier smtpEmailNotifier;

    @Test
    void 실제_SMTP로_메일을_발송한다() {
        // given
        var organizationName = "테스트 조직";
        var eventTitle = "테스트 이벤트";
        var organizerNickname = "주최자";

        var member = Member.create("주최자", "amadda.team@gmail.com", "testPicture");
        var organization = Organization.create(organizationName, "설명", "logo.png");
        var organizationMember = OrganizationMember.create(organizerNickname, member, organization, Role.USER);

        var emailPayload = new EventEmailPayload(
                new EventEmailPayload.Subject(
                        organizationName,
                        organizerNickname,
                        eventTitle
                ),
                new EventEmailPayload.Body(
                        "테스트 메일 본문입니다.",
                        organizationName,
                        eventTitle,
                        organizerNickname,
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L,
                        1L
                )
        );

        // when // then
        smtpEmailNotifier.sendEmails(List.of(organizationMember), emailPayload);
    }

    @Test
    void BCC_수신자가_100명_이상이면_예외가_발생한다() {
        // given
        var organizationName = "테스트 조직";
        var eventTitle = "테스트 이벤트";
        var organizerNickname = "주최자";

        var organization = Organization.create(organizationName, "설명", "logo.png");

        var recipients = new ArrayList<OrganizationMember>();
        for (int i = 0; i < 100; i++) {
            var email = "dummy" + i + "@example.com";
            var dummyMember = Member.create("유저" + i, email, "profile.png");
            var orgMember = OrganizationMember.create("닉네임" + i, dummyMember, organization, Role.USER);
            recipients.add(orgMember);
        }

        var emailPayload = new EventEmailPayload(
                new EventEmailPayload.Subject(
                        organizationName,
                        organizerNickname,
                        eventTitle
                ),
                new EventEmailPayload.Body(
                        "100명 이상의 수신자에게 발송되는 테스트 메일입니다.",
                        organizationName,
                        eventTitle,
                        organizerNickname,
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L,
                        1L
                )
        );

        // when
        smtpEmailNotifier.sendEmails(recipients, emailPayload);
    }
}
