package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.presentation.OrganizationCreateRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(String name,
                                                                      String description,
                                                                      String imageUrl) {
        return new OrganizationCreateRequest(name, description, imageUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_설명이_비어있으면_예외가_발생한다(String blankDescription) {
        // when // then
        assertThatThrownBy(() -> Organization.create("정상 이름", blankDescription, "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_길면_예외가_발생한다() {
        // given
        var longName = "스무글자를넘어가는엄청나게긴조직이름입니다";

        // when // then
        assertThatThrownBy(() -> Organization.create(longName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 조직_생성시_이름이_규칙보다_짧으면_예외가_발생한다() {
        // given
        var shortName = "한";

        // when // then
        assertThatThrownBy(() -> Organization.create(shortName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void 조직_생성시_이름이_비어있으면_예외가_발생한다(String blankName) {
        // when // then
        assertThatThrownBy(() -> Organization.create(blankName, "설명", "url"))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

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
}
