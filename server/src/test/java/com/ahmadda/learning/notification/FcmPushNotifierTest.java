package com.ahmadda.learning.notification;

import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = "push.mock=false")
class FcmPushNotifierTest {

    @Autowired
    private PushNotifier fcmPushNotifier;

    @Test
    void 실제_FCM으로_푸시를_전송한다() {
        // given
        List<String> tokens = List.of(
                "토큰을 입력하세요"
        );

        PushNotificationPayload payload = new PushNotificationPayload(
                "테스트 알림 제목",
                "이것은 테스트 메시지입니다.",
                1L
        );

        // when // then
        fcmPushNotifier.sendPushs(tokens, payload);
    }
}
