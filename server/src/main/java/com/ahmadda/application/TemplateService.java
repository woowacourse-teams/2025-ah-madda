package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Template;
import com.ahmadda.domain.TemplateRepository;
import com.ahmadda.presentation.dto.TemplateCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final MemberRepository memberRepository;
    private final TemplateRepository templateRepository;

    @Transactional
    public Template createTemplate(final LoginMember loginMember, final TemplateCreateRequest templateCreateRequest) {
        Member member = getMember(loginMember);
        Template template = Template.create(
                member,
                templateCreateRequest.title(),
                templateCreateRequest.description()
        );

        templateRepository.save(template);

        return template;
    }

    public Template getTemplate(final LoginMember loginMember, final Long templateId) {
        Member member = getMember(loginMember);

        validateTemplateWriter(templateId, member.getId());

        return getTemplate(templateId);
    }

    public List<Template> getTemplates(final LoginMember loginMember) {
        Member member = getMember(loginMember);

        return getOwnTemplates(member.getId());
    }

    @Transactional
    public void deleteTemplate(final LoginMember loginMember, final Long templateId) {
        Member member = getMember(loginMember);
        Template deleteTemplate = getTemplate(templateId);

        validateTemplateWriter(templateId, member.getId());

        templateRepository.delete(deleteTemplate);
    }

    private List<Template> getOwnTemplates(final Long memberId) {
        return templateRepository.findAllByMemberId(memberId);
    }

    private void validateTemplateWriter(final Long templateId, final Long memberId) {
        if (!templateRepository.existsByIdAndMemberId(templateId, memberId)) {
            throw new AccessDeniedException("본인이 작성한 템플릿이 아닙니다.");
        }
    }

    private Template getTemplate(final Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 템플릿입니다."));
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}
