package com.ahmadda.application;

import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrganizationServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationService sut;

    @Test
    void 조직을_ID로_조회한다() {
        // given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);

        // when
        var found = sut.getOrganization(organization.getId());

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(found.getName()).isEqualTo("Org");
            softly.assertThat(found.getDescription()).isEqualTo("Desc");
            softly.assertThat(found.getImageUrl()).isEqualTo("img.png");
        });
    }

    @Test
    void 조직을_생성한다() {
        // given
        var request = createOrganizationCreateRequest("조직명", "조직 설명", "image.png");

        // when
        sut.createOrganization(request);

        // then
        var organizations = organizationRepository.findAll();
        assertThat(organizations).hasSize(1);
        var saved = organizations.get(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(saved.getName()).isEqualTo("조직명");
            softly.assertThat(saved.getDescription()).isEqualTo("조직 설명");
            softly.assertThat(saved.getImageUrl()).isEqualTo("image.png");
        });
    }

    @Test
    void 존재하지_않는_조직_ID로_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.getOrganization(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 존재하지_않는_조직의_이벤트를_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.getOrganizationEvents(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 여러_조직의_이벤트가_있을때_선택된_조직의_활성화된_이벤트만_가져온다() {
        // given
        var member = memberRepository.save(Member.create("name", "test@test.com"));
        var orgA = organizationRepository.save(createOrganization("OrgA", "DescA", "a.png"));
        var orgB = organizationRepository.save(createOrganization("OrgB", "DescB", "b.png"));
        var orgMemberA = organizationMemberRepository.save(OrganizationMember.create("nickname", member, orgA));

        var now = LocalDateTime.now();
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA1", now.plusDays(1), now.plusDays(2)));
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA2", now.plusDays(2), now.plusDays(3)));
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA3", now.minusDays(2), now.minusDays(1))); // inactive
        eventRepository.save(createEvent(orgMemberA, orgB, "EventB1", now.plusDays(1), now.plusDays(2)));

        // when
        var events = sut.getOrganizationEvents(orgA.getId());

        // then
        assertThat(events).hasSize(2)
                .extracting(Event::getTitle)
                .containsExactlyInAnyOrder("EventA1", "EventA2");
    }

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private Event createEvent(OrganizationMember organizer,
                              Organization organization,
                              String title,
                              LocalDateTime start,
                              LocalDateTime end) {
        
        return Event.create(
                title,
                "description",
                "place",
                organizer,
                organization,
                start,
                end,
                start.plusHours(1),
                end.plusHours(1),
                100
        );
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(String name,
                                                                      String description,
                                                                      String imageUrl) {
        return new OrganizationCreateRequest(name, description, imageUrl);
    }
}
