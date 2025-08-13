package com.ahmadda.infra.notification.push;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class FcmPushErrorHandlerTest {

    @Autowired
    private FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @Test
    void 요청_실패시_유효하지_않은_토큰이_있으면_제거한다() {
        // given
        var tokenValue = "expired-token";
        var saved = fcmRegistrationTokenRepository.save(
                FcmRegistrationToken.create(1L, tokenValue, java.time.LocalDateTime.now())
        );

        var exception = mock(FirebaseMessagingException.class);
        when(exception.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);

        var response = mock(SendResponse.class);
        when(response.isSuccessful()).thenReturn(false);
        when(response.getException()).thenReturn(exception);

        var batchResponse = mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(List.of(response));

        var sut = new FcmPushErrorHandler(fcmRegistrationTokenRepository);

        // when
        sut.handleFailures(batchResponse, List.of(tokenValue));

        // then
        assertThat(fcmRegistrationTokenRepository.findById(saved.getId())).isEmpty();
    }
}
