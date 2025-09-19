package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.OrganizationMember;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BccChunkingEmailNotifier implements EmailNotifier {

    private final EmailNotifier delegate;
    private final int maxBcc;

    @Override
    public void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload payload) {
        for (int i = 0; i < recipients.size(); i += maxBcc) {
            int end = Math.min(i + maxBcc, recipients.size());
            List<OrganizationMember> chunk = recipients.subList(i, end);

            delegate.sendEmails(chunk, payload);
        }
    }
}
