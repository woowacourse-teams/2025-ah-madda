package com.ahmadda.learning;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventNotification;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.Period;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "mail.mock=false")
@Transactional
class EventNotificationTest {

    @Autowired
    private EventNotification eventNotification;

    @Test
    void 실제_SMTP로_메일을_발송한다() {
        // given
        var organization = Organization.create("테스트 조직", "설명", "logo.png");
        var organizer = createOrganizationMember("주최자", "host@test.com", organization);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트",
                "설명입니다",
                "루터회관",
                organizer,
                organization,
                EventOperationPeriod.create(
                        Period.create(now.minusDays(2), now.minusDays(1)),
                        Period.create(now.plusDays(1), now.plusDays(2)),
                        now.minusDays(3)
                ),
                "주최자",
                50
        );

        var om1 = createOrganizationMember("수신자1", "amadda.team@gmail.com", organization);
        var om2 = createOrganizationMember("수신자2", "amadda.mailbot@gmail.com", organization);
        var recipients = List.of(om1, om2);

        // when // then
        eventNotification.sendEmails(event, recipients, "테스트 메일 본문입니다.");
    }

    private OrganizationMember createOrganizationMember(String nickname, String email, Organization organization) {
        var member = Member.create(nickname, email);

        return OrganizationMember.create(nickname, member, organization);
    }
}
