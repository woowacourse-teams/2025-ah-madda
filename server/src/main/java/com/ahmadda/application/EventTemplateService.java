package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.EventTemplate;
import com.ahmadda.domain.event.EventTemplateRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.presentation.dto.EventTemplateCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventTemplateService {

    private final MemberRepository memberRepository;
    private final EventTemplateRepository eventTemplateRepository;

    @Transactional
    public EventTemplate createTemplate(
            final LoginMember loginMember,
            final EventTemplateCreateRequest eventTemplateCreateRequest
    ) {
        Member member = getMember(loginMember);
        EventTemplate eventTemplate = EventTemplate.create(
                member,
                eventTemplateCreateRequest.title(),
                eventTemplateCreateRequest.description()
        );

        eventTemplateRepository.save(eventTemplate);

        return eventTemplate;
    }

    public EventTemplate getTemplate(final LoginMember loginMember, final Long templateId) {
        Member member = getMember(loginMember);

        validateTemplateWriter(templateId, member.getId());

        return getTemplate(templateId);
    }

    public List<EventTemplate> getTemplates(final LoginMember loginMember) {
        Member member = getMember(loginMember);

        return getOwnTemplates(member.getId());
    }

    @Transactional
    public void deleteTemplate(final LoginMember loginMember, final Long templateId) {
        Member member = getMember(loginMember);
        EventTemplate deleteEventTemplate = getTemplate(templateId);

        validateTemplateWriter(templateId, member.getId());

        eventTemplateRepository.delete(deleteEventTemplate);
    }

    private List<EventTemplate> getOwnTemplates(final Long memberId) {
        return eventTemplateRepository.findAllByMemberId(memberId);
    }

    private void validateTemplateWriter(final Long templateId, final Long memberId) {
        if (!eventTemplateRepository.existsByIdAndMemberId(templateId, memberId)) {
            throw new ForbiddenException("본인이 작성한 템플릿이 아닙니다.");
        }
    }

    private EventTemplate getTemplate(final Long templateId) {
        return eventTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 템플릿입니다."));
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}
