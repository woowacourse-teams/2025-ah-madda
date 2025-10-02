package com.ahmadda.learning.infra.notification;

import com.ahmadda.annotation.LearningTest;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.PushNotificationPayload;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.infra.notification.push.FcmPushNotifier;
import com.ahmadda.infra.notification.push.FcmRegistrationToken;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@Disabled
@LearningTest
@TestPropertySource(properties = "push.noop=false")
class FcmPushNotifierTest {

    @Autowired
    private FcmPushNotifier sut;

    @Autowired
    private FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 실제_FCM으로_푸시를_전송한다() {
        // given
        var member = Member.create("테스트 회원", "amadda.team@gmail.com", "testPicture");
        var organization = Organization.create("테스트 이벤트 스페이스", "설명", "logo.png");
        var organizationMember = OrganizationMember.create(
                "푸시대상",
                member,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("그룹")
        );

        memberRepository.save(member);

        var token = FcmRegistrationToken.createNow(
                member.getId(),
                "d_v82UVaFsZcOUIPWbvYHI:APA91bEZSNYInGcWUg97mmqHNbH9TyUiYQ-uC0cs0F5-Mktw0hCYjw_HutZ644-AMdmV9NSDNgJv1YN1g-0RJFKndPwfd2U_oZxgZ9gdmGghs34QH3_yyXg"
        );

        fcmRegistrationTokenRepository.save(token);

        var payload = new PushNotificationPayload(
                "테스트 알림 제목",
                "이것은 테스트 메시지입니다.",
                1L,
                1L
        );

        // when // then
        sut.remind(List.of(organizationMember), payload);
    }

    @Test
    void 실제_FCM으로_한명에게_푸시알람을_전송한다() {
        // given
        var member = Member.create("테스트 회원", "amadda.team@gmail.com", "testPicture");
        var organization = Organization.create("테스트 이벤트 스페이스", "설명", "logo.png");
        var organizationMember = OrganizationMember.create(
                "푸시대상",
                member,
                organization,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("그룹")
        );

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
                1L,
                1L
        );

        // when // then
        sut.poke(organizationMember, payload);
    }
}
