package com.ahmadda.support;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.Poke;
import com.ahmadda.domain.notification.PushNotifier;
import com.ahmadda.domain.notification.Reminder;
import com.ahmadda.domain.organization.RandomCodeGenerator;
import com.ahmadda.infra.auth.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.notification.mail.EmailSender;
import io.jeyong.nplus1detector.test.annotation.NPlusOneTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles(profiles = "test")
@NPlusOneTest(NPlusOneTest.Mode.LOGGING)
public abstract class IntegrationTest {

    @MockitoBean
    protected GoogleOAuthProvider googleOAuthProvider;

    @MockitoSpyBean
    protected Reminder reminder;

    @MockitoSpyBean
    protected Poke poke;

    @MockitoBean
    protected EmailNotifier emailNotifier;

    @MockitoBean
    @Qualifier("failoverEmailSender")
    protected EmailSender emailSender;

    @MockitoBean
    protected PushNotifier pushNotifier;

    @MockitoBean
    protected RandomCodeGenerator randomCodeGenerator;

}
