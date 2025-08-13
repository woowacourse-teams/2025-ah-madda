package com.ahmadda.learning.notification;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationToken;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "push.mock=false")
@Disabled
class FcmPushNotifierTest {

    @Autowired
    private PushNotifier fcmPushNotifier;

    @Autowired
    private FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 실제_FCM으로_푸시를_전송한다() {
        // given
        var member = Member.create("테스트 회원", "amadda.team@gmail.com", "testPicture");
        var organization = Organization.create("테스트 조직", "설명", "logo.png");
        var organizationMember = OrganizationMember.create("푸시대상", member, organization);

        memberRepository.save(member);

        var token = FcmRegistrationToken.create(
                member.getId(),
                "d_v82UVaFsZcOUIPWbvYHI:APA91bEZSNYInGcWUg97mmqHNbH9TyUiYQ-uC0cs0F5-Mktw0hCYjw_HutZ644-AMdmV9NSDNgJv1YN1g-0RJFKndPwfd2U_oZxgZ9gdmGghs34QH3_yyXg",
                LocalDateTime.now()
        );

        fcmRegistrationTokenRepository.save(token);

        var payload = new PushNotificationPayload(
                "테스트 알림 제목",
                "이것은 테스트 메시지입니다.",
                1L
        );

        // when // then
        fcmPushNotifier.sendPushs(List.of(organizationMember), payload);
    }

    @Test
    void 실제_FCM으로_한명에게_푸시알람을_전송한다() {
        // given
        var member = Member.create("테스트 회원", "amadda.team@gmail.com", "testPicture");
        var organization = Organization.create("테스트 조직", "설명", "logo.png");
        var organizationMember = OrganizationMember.create("푸시대상", member, organization);

        memberRepository.save(member);

        var token = FcmRegistrationToken.create(
                member.getId(),
                "d_v82UVaFsZcOUIPWbvYHI:APA91bEZSNYInGcWUg97mmqHNbH9TyUiYQ-uC0cs0F5-Mktw0hCYjw_HutZ644-AMdmV9NSDNgJv1YN1g-0RJFKndPwfd2U_oZxgZ9gdmGghs34QH3_yyXg",
                LocalDateTime.now()
        );

        fcmRegistrationTokenRepository.save(token);

        var payload = new PushNotificationPayload(
                "테스트 알림 제목",
                "이것은 테스트 메시지입니다.",
                1L
        );

        // when // then
        fcmPushNotifier.sendPush(organizationMember, payload);
    }
}
