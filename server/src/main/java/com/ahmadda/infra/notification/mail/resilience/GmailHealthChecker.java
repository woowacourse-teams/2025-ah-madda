package com.ahmadda.infra.notification.mail.resilience;

import com.ahmadda.infra.notification.mail.EmailSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GmailHealthChecker {

    private final EmailSender googleSmtpEmailSender;

    public GmailHealthChecker(@Qualifier("googleSmtpEmailSender") final EmailSender googleSmtpEmailSender) {
        this.googleSmtpEmailSender = googleSmtpEmailSender;
    }

    public boolean isAvailable() {
        try {
            googleSmtpEmailSender.sendEmails(
                    List.of("amadda.team@gmail.com"),
                    "[시스템 점검] Gmail 발송 상태 확인 메일",
                    "이 메일은 시스템의 Gmail 발송 기능을 점검하기 위한 테스트용 메일입니다."
            );

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
