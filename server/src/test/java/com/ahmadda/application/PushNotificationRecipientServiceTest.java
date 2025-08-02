package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.PushNotificationRecipientRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.PushNotificationRecipientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class PushNotificationRecipientServiceTest {

    @Autowired
    private PushNotificationRecipientService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PushNotificationRecipientRepository recipientRepository;

    @Test
    void 푸시알림_수신자를_등록한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "test@example.com"));
        var loginMember = new LoginMember(member.getId());
        var request = new PushNotificationRecipientRequest("토큰값");

        // when
        var saved = sut.registerRecipient(request, loginMember);

        // then
        assertThat(recipientRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(recipient -> {
                    assertSoftly(softly -> {
                        softly.assertThat(recipient.getMember())
                                .isEqualTo(member);
                        softly.assertThat(recipient.getPushToken())
                                .isEqualTo("토큰값");
                    });
                });
    }

    @Test
    void 회원이_존재하지_않으면_예외가_발생한다() {
        // given
        var nonExistentMemberId = 999L;
        var loginMember = new LoginMember(nonExistentMemberId);
        var request = new PushNotificationRecipientRequest("임의의토큰");

        // when // then
        assertThatThrownBy(() -> sut.registerRecipient(request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}
