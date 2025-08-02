package com.ahmadda.domain;

import com.ahmadda.domain.util.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationRecipient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_notification_recipient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // TODO. 추후 기기당 토큰의 중복을 허용할지 여부를 결정해야 함
    @Column(nullable = false)
    private String pushToken;

    private PushNotificationRecipient(
            final Member member,
            final String pushToken
    ) {
        validateMember(member);
        validateNotificationToken(pushToken);

        this.member = member;
        this.pushToken = pushToken;
        member.getPushNotificationRecipients()
                .add(this);
    }

    public static PushNotificationRecipient create(
            final Member member,
            final String notificationToken
    ) {
        return new PushNotificationRecipient(member, notificationToken);
    }

    private void validateMember(final Member member) {
        Assert.notNull(member, "회원은 null이 되면 안됩니다.");
    }

    private void validateNotificationToken(final String notificationToken) {
        Assert.notBlank(notificationToken, "알림 토큰은 공백이면 안됩니다.");
    }
}
