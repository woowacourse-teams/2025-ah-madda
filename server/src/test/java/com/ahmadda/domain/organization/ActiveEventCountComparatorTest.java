package com.ahmadda.domain.organization;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.member.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ActiveEventCountComparatorTest {

    @Test
    void 활성_이벤트가_많은_조직이_먼저_정렬된다() {
        // given
        var now = LocalDateTime.now();

        var org1 = Organization.create("조직1", "설명", "image1.png");
        var org2 = Organization.create("조직2", "설명", "image2.png");

        var member = Member.create("테스터", "tester@example.com", "profile.png");
        var organizer1 = OrganizationMember.create(
                "organizer1",
                member,
                org1,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
        var organizer2 = OrganizationMember.create(
                "organizer2",
                member,
                org2,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );

        createEvent(org1, organizer1, "이벤트1-1", now.minusDays(1), now.plusDays(1));
        createEvent(org1, organizer1, "이벤트1-2", now.minusDays(2), now.plusDays(2));

        createEvent(org2, organizer2, "이벤트2-1", now.minusDays(1), now.plusDays(1));

        var organizations = new ArrayList<>(List.of(org1, org2));

        // when
        organizations.sort(new ActiveEventCountComparator(now));

        // then
        assertSoftly(softly -> {
            softly.assertThat(organizations.get(0))
                    .isEqualTo(org1);
            softly.assertThat(organizations.get(1))
                    .isEqualTo(org2);
        });
    }

    @Test
    void 활성_이벤트_수가_같으면_순서가_유지된다() {
        // given
        var now = LocalDateTime.now();

        var org1 = Organization.create("조직1", "설명", "image1.png");
        var org2 = Organization.create("조직2", "설명", "image2.png");

        var member = Member.create("테스터", "tester@example.com", "profile.png");
        var organizer1 = OrganizationMember.create(
                "organizer1",
                member,
                org1,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );
        var organizer2 = OrganizationMember.create(
                "organizer2",
                member,
                org2,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("백엔드")
        );

        // 두 조직 모두 활성 이벤트 1개
        createEvent(org1, organizer1, "이벤트1", now.minusDays(1), now.plusDays(1));
        createEvent(org2, organizer2, "이벤트2", now.minusDays(1), now.plusDays(1));

        var organizations = new ArrayList<>(List.of(org1, org2));

        // when
        organizations.sort(new ActiveEventCountComparator(now));

        // then
        assertSoftly(softly -> {
            softly.assertThat(organizations.get(0))
                    .isEqualTo(org1);
            softly.assertThat(organizations.get(1))
                    .isEqualTo(org2);
        });
    }

    private void createEvent(
            Organization org,
            OrganizationMember organizer,
            String title,
            LocalDateTime start,
            LocalDateTime end
    ) {
        Event.create(
                title,
                "설명",
                "장소",
                organizer,
                org,
                EventOperationPeriod.create(
                        start.minusDays(1),
                        start,
                        start,
                        end,
                        start.minusDays(2)
                ),
                50,
                false
        );
    }
}
