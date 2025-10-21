package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Member getMember(final LoginMember loginMember) {
        return getMember(loginMember.memberId());
    }

    @Transactional(readOnly = true)
    public List<Event> getOwnerEvents(final LoginMember loginMember) {
        Member member = getMember(loginMember.memberId());

        return eventRepository.findAllOrganizedBy(member);
    }

    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(final LoginMember loginMember) {
        Member member = getMember(loginMember.memberId());

        return eventRepository.findAllParticipatedBy(member);
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}
