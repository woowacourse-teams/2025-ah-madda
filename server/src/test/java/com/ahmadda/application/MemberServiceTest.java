package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
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
    private MemberService memberService;

    private GoogleOAuthUserInfo createGoogleOAuthUserInfo(String name, String email) {
        return new GoogleOAuthUserInfo(name, email);
    }

    private Member createMember(String name, String email) {
        return Member.create(name, email);
    }

    @DisplayName("새로운 회원이면 저장하고 새로운 회원을 반환한다.")
    @Test
    void processGoogleOAuthLogin_newMember() {
        // given
        GoogleOAuthUserInfo userInfo = createGoogleOAuthUserInfo("new@test.com", "new_name");
        createMember(userInfo.name(), userInfo.email());

        // when
        memberService.processGoogleOAuthLogin(userInfo);

        // then
        assertThat(memberRepository.existsMemberByEmail("new@test.com")).isTrue();
    }

    @Test
    void 기존_회원이면_해당_회원을_반환한다() {
        // given
        GoogleOAuthUserInfo userInfo = createGoogleOAuthUserInfo("test@test.com", "test_name");
        Member member = createMember("test_name", "test@test.com");
        Member existingMember = memberRepository.save(member);

        // when
        Member result = memberService.processGoogleOAuthLogin(userInfo);

        // then
        assertThat(result).isEqualTo(existingMember);
    }
}
