package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.presentation.OrganizationCreateRequest;
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
    private OrganizationService organizationService;

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(String name,
                                                                      String description,
                                                                      String imageUrl) {
        return new OrganizationCreateRequest(name, description, imageUrl);
    }

    @Test
    void 조직을_ID로_조회한다() {
        // given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);

        // when
        var sut = this.organizationService.getOrganization(organization.getId());

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(sut.getName()).isEqualTo("Org");
            softly.assertThat(sut.getDescription()).isEqualTo("Desc");
            softly.assertThat(sut.getImageUrl()).isEqualTo("img.png");
        });
    }

    @Test
    void 조직을_생성한다() {
        // given
        var request = createOrganizationCreateRequest("조직명", "조직 설명", "image.png");

        // when
        organizationService.createOrganization(request);

        // then
        var organizations = organizationRepository.findAll();
        assertThat(organizations).hasSize(1);
        var sut = organizations.get(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(sut.getName()).isEqualTo("조직명");
            softly.assertThat(sut.getDescription()).isEqualTo("조직 설명");
            softly.assertThat(sut.getImageUrl()).isEqualTo("image.png");
        });
    }

    @Test
    void 존재하지_않는_조직_ID로_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> organizationService.getOrganization(999L))
                .isInstanceOf(BusinessFlowViolatedException.class);
    }
}
