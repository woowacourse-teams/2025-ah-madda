package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.OrganizationMember;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MockEmailNotifier implements EmailNotifier {

    @Override
    public void sendEmails(
            final List<OrganizationMember> recipients,
            final EventEmailPayload eventEmailPayload
    ) {
        log.info(
                "[Mock Email] To: {} | Subject: {} | Body: {}",
                recipients,
                eventEmailPayload.subject(),
                eventEmailPayload.body()
        );
    }
}
