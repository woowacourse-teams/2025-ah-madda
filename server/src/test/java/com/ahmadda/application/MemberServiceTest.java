package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 자신의_회원_정보를_조회한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "hong@gildong.com"));
        var loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail());

        // when
        var result = sut.getMember(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getName())
                    .isEqualTo(member.getName());
            softly.assertThat(result.getEmail())
                    .isEqualTo(member.getEmail());
        });
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다() {
        // given
        var loginMember = new LoginMember(999L, "이름", "email@none.com");

        // when // then
        assertThatThrownBy(() -> sut.getMember(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}
