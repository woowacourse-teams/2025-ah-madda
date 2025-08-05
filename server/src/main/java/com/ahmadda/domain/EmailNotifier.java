package com.ahmadda.domain;

import java.util.List;

public interface EmailNotifier {

    void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload eventEmailPayload);
}
