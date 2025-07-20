package com.ahmadda.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventTest {

    private Organization baseOrganization;
    private OrganizationMember baseOrganizer;

    @BeforeEach
    void setUp() {
        Member baseMember = createMember("테스트 멤버", "test@example.com");
        baseOrganization = createOrganization();
        baseOrganizer = createOrganizationMember("주최자", baseMember, baseOrganization);
    }

    @Test
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var sut = createEvent();
        var guest = createOrganizationMember("조직원", createMember("참가자1", "guest1@example.com"), baseOrganization);
        var notGuest = createOrganizationMember("다른 조직원", createMember("참가자2", "guest2@example.com"), baseOrganization);
        Guest.create(sut, guest);

        // when
        var actual = sut.hasGuest(guest);
        var unexpected = sut.hasGuest(notGuest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual)
                    .isTrue();
            softly.assertThat(unexpected)
                    .isFalse();
        });
    }

    @Test
    void 이벤트에_참여한_게스트들을_조회할_수_있다() {
        // given
        var sut = createEvent();
        var guest1 = createOrganizationMember("게스트1", createMember("게스트1", "g1@email.com"), baseOrganization);
        var guest2 = createOrganizationMember("게스트2", createMember("게스트2", "g2@email.com"), baseOrganization);
        Guest.create(sut, guest1);
        Guest.create(sut, guest2);

        // when
        var guests = sut.getGuestOrganizationMembers();

        // then
        assertSoftly(softly -> {
            softly.assertThat(guests)
                    .hasSize(2);
            softly.assertThat(guests)
                    .extracting("nickname")
                    .containsExactlyInAnyOrder("게스트1", "게스트2");
        });
    }

    @Test
    void 이벤트에_참여하지_않은_조직원을_조회할_수_있다() {
        // given
        var sut = createEvent();
        var guest = createOrganizationMember("게스트", createMember("게스트", "guest@email.com"), baseOrganization);
        var nonGuest1 = createOrganizationMember("비게스트1", createMember("비게스트1", "non1@email.com"), baseOrganization);
        var nonGuest2 = createOrganizationMember("비게스트2", createMember("비게스트2", "non2@email.com"), baseOrganization);
        Guest.create(sut, guest);
        var allMembers = List.of(baseOrganizer, guest, nonGuest1, nonGuest2);

        // when
        var nonGuests = sut.getNonGuestOrganizationMembers(allMembers);

        // then
        assertSoftly(softly -> {
            softly.assertThat(nonGuests)
                    .hasSize(2);
            softly.assertThat(nonGuests)
                    .extracting("nickname")
                    .containsExactlyInAnyOrder("비게스트1", "비게스트2");
        });
    }

    private Event createEvent() {
        var now = LocalDateTime.now();

        return Event.create(
                "테스트 이벤트", "설명", "장소", baseOrganizer, baseOrganization,
                now.plusDays(1), now.plusDays(5),
                now.plusDays(10), now.plusDays(11),
                50
        );
    }

    private Member createMember(String name, String email) {
        return Member.create(name, email);
    }

    private Organization createOrganization() {
        return Organization.create("테스트 조직", "설명", "image.png");
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization organization
    ) {
        return OrganizationMember.create(nickname, member, organization);
    }
}
