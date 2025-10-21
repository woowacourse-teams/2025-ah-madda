package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 자신의_회원_정보를_조회한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "hong@gildong.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getMember(loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getName())
                    .isEqualTo("홍길동");
            softly.assertThat(result.getEmail())
                    .isEqualTo("hong@gildong.com");
            softly.assertThat(result.getProfileImageUrl())
                    .isEqualTo("testPicture");
        });
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다() {
        // given
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.getMember(loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}
