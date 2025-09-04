package com.ahmadda.domain.notification;

import com.ahmadda.domain.organization.OrganizationMember;

import java.util.List;

public interface EmailNotifier {

    void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload eventEmailPayload);
}
