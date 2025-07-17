package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService sut;

    @Test
    void 새로운_회원이면_저장하고_새로운_회원을_반환한다() {
        // given
        var userInfo = new GoogleOAuthUserInfo("new@test.com", "new_name");

        // when
        sut.processGoogleOAuthLogin(userInfo);

        // then
        assertThat(memberRepository.existsMemberByEmail("new@test.com")).isTrue();
    }

    @Test
    void 기존_회원이면_해당_회원을_반환한다() {
        // given
        var userInfo = new GoogleOAuthUserInfo("test@test.com", "test_name");
        var member = Member.create("test_name", "test@test.com");
        var existingMember = memberRepository.save(member);

        // when
        Member result = sut.processGoogleOAuthLogin(userInfo);

        // then
        assertThat(result).isEqualTo(existingMember);
    }
}
