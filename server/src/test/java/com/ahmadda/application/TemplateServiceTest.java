package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Template;
import com.ahmadda.domain.TemplateRepository;
import com.ahmadda.presentation.dto.TemplateCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class TemplateServiceTest {

    @Autowired
    private TemplateService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Test
    void 템플릿을_생성할_수_있다() {
        // given
        var savedMember = createMember();
        var login = new LoginMember(savedMember.getId());
        var req = new TemplateCreateRequest("title", "desc");

        // when
        var saved = sut.createTemplate(login, req);

        // then
        assertSoftly(softly -> {
            softly.assertThat(saved.getTitle())
                    .isEqualTo("title");
            softly.assertThat(saved.getDescription())
                    .isEqualTo("desc");
        });
    }

    @Test
    void 회원이_존재하지_않으면_예외가_발생한다() {
        // given
        var invalid = new LoginMember(999_999L);
        var req = new TemplateCreateRequest("title", "description");

        // when // then
        assertThatThrownBy(() -> sut.createTemplate(invalid, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 내_템플릿_목록을_조회할_수_있다() {
        // given
        var me = createMember();
        var loginMember = new LoginMember(me.getId());
        var other = createMember("otherName", "otherEmail");

        createTemplate(me);
        createTemplate(me);
        createTemplate(other);

        // when
        var myTemplate = sut.getTemplates(loginMember);

        // then
        assertThat(myTemplate)
                .hasSize(2);
    }

    @Test
    void 존재하지_않는_회원의_템플릿을_조회하면_예외가_발생한다() {
        // given
        var loginMember = new LoginMember(123_456L);

        // when // then
        assertThatThrownBy(() -> sut.getTemplates(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 내_템플릿을_조회할_수_있다() {
        // given
        var owner = createMember();
        var loginMember = new LoginMember(owner.getId());
        var tmpl = createTemplate(owner, "title", "desc");

        // when
        var found = sut.getTemplate(loginMember, tmpl.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getId())
                    .isEqualTo(tmpl.getId());
            softly.assertThat(found.getTitle())
                    .isEqualTo("title");
            softly.assertThat(found.getDescription())
                    .isEqualTo("desc");
        });
    }

    @Test
    void 다른_사람의_템플릿을_조회하면_예외가_발생한다() {
        // given
        var owner = createMember("owner", "owner@mail.com");
        var other = createMember("other", "other@mail.com");
        var tmpl = createTemplate(owner);
        var loginMember = new LoginMember(other.getId());

        // when // then
        assertThatThrownBy(() -> sut.getTemplate(loginMember, tmpl.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("본인이 작성한 템플릿이 아닙니다.");
    }

    @Test
    void 내_템플릿을_삭제할_수_있다() {
        // given
        var owner = createMember();
        var loginMember = new LoginMember(owner.getId());
        var tmpl = createTemplate(owner);

        // when
        sut.deleteTemplate(loginMember, tmpl.getId());

        // then
        assertThat(templateRepository.findById(tmpl.getId())).isEmpty();
    }

    @Test
    void 다른_사람의_템플릿을_삭제하면_예외가_발생한다() {
        // given
        var owner = createMember("owner", "owner@mail.com");
        var other = createMember("other", "other@mail.com");
        var myTmpl = createTemplate(owner);
        var otherLoginMember = new LoginMember(other.getId());

        // when // then
        assertThatThrownBy(() -> sut.deleteTemplate(otherLoginMember, myTmpl.getId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("본인이 작성한 템플릿이 아닙니다.");

        assertThat(templateRepository.findById(myTmpl.getId())).isPresent();
    }

    @Test
    void 존재하는_템플릿만_제거할_수_있다() {
        // given
        var owner = createMember();
        var loginMember = new LoginMember(owner.getId());
        var invalidTemplateId = 111_222L;

        // when // then
        assertThatThrownBy(() -> sut.deleteTemplate(loginMember, invalidTemplateId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 템플릿입니다.");
    }

    @Test
    void 존재하는_멤버만_템플릿을_삭제할_수_있다() {
        // given
        var invalidMemberId = 999L;
        var loginMember = new LoginMember(invalidMemberId);
        var invalidTemplateId = 111_222L;

        // when // then
        assertThatThrownBy(() -> sut.deleteTemplate(loginMember, invalidTemplateId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    private Member createMember() {
        return memberRepository.save(Member.create("name", "email", "pic"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "pic"));
    }

    private Template createTemplate(Member member) {
        return templateRepository.save(Template.create(member, "title", "desc"));
    }

    private Template createTemplate(Member member, String title, String description) {
        return templateRepository.save(Template.create(member, title, description));
    }
}
