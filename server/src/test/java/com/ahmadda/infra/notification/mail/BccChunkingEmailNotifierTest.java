package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BccChunkingEmailNotifierTest {

    private BccChunkingEmailNotifier sut;

    private EmailNotifier delegate;
    private EventEmailPayload payload;

    @BeforeEach
    void setUp() {
        delegate = mock(EmailNotifier.class);
        sut = new BccChunkingEmailNotifier(delegate, 50);

        payload = new EventEmailPayload(
                new EventEmailPayload.Subject("이벤트 스페이스", "이벤트"),
                new EventEmailPayload.Body(
                        "본문",
                        "이벤트 스페이스",
                        "이벤트",
                        "주최자",
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L, 1L
                )
        );
    }

    @Test
    void 수신자가_여러명일때_수신자가_제한_이하면_한_번만_호출된다() {
        // given
        var recipients = createRecipients(30);

        // when
        sut.sendEmails(recipients, payload);

        // then
        verify(delegate, times(1)).sendEmails(recipients, payload);
    }

    @Test
    void 수신자가_여러명일때_수신자가_제한을_초과하면_분할되어_여러_번_호출된다() {
        // given
        var recipients = createRecipients(120);

        // when
        sut.sendEmails(recipients, payload);

        // then
        verify(delegate, times(3)).sendEmails(anyList(), eq(payload));
    }

    private List<OrganizationMember> createRecipients(int count) {
        var org = Organization.create("이벤트 스페이스", "설명", "logo.png");
        List<OrganizationMember> members = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var member = Member.create("닉네임" + i, "user" + i + "@example.com", "pic.png");
            members.add(OrganizationMember.create("닉네임" + i, member, org, OrganizationMemberRole.USER));
        }

        return members;
    }
}
