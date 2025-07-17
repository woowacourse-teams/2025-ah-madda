package com.ahmadda.application;

import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrganizationServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

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
                .isInstanceOf(BusinessFlowViolatedException.class);
    }

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(String name,
                                                                      String description,
                                                                      String imageUrl) {
        return new OrganizationCreateRequest(name, description, imageUrl);
    }
}
