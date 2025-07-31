package com.ahmadda.infra.slack;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import com.ahmadda.infra.slack.config.SlackAlarmProperties;
import com.ahmadda.infra.slack.dto.MemberCreationAlarmPayload;
import com.ahmadda.infra.slack.exception.SlackReminderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

@Slf4j
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class AsyncSlackReminder implements SlackReminder {

    private final SlackAlarmProperties slackAlarmProperties;
    private final RestClient restClient;

    public AsyncSlackReminder(final RestClient restClient, final SlackAlarmProperties slackAlarmProperties) {
        this.slackAlarmProperties = slackAlarmProperties;
        this.restClient = restClient;
    }

    @Async
    public void alarmMemberCreation(final MemberCreateAlarmDto memberCreateAlarmDto) {
        try {
            ResponseSpec retrieve = restClient.post()
                    .body(MemberCreationAlarmPayload.create(memberCreateAlarmDto, slackAlarmProperties.getChannelId()))
                    .retrieve();
            ResponseEntity<Void> bodilessEntity = retrieve.toBodilessEntity();

            if (bodilessEntity.getStatusCode() != HttpStatus.ACCEPTED) {
                throw new SlackReminderException("유저 생성 슬랙 알람을 보내는데 실패 하였습니다");
            }
        } catch (Exception e) {
            log.error("Slack 회원 가입 알림 전송에 실패했습니다. member={}",
                      memberCreateAlarmDto,
                      e
            );
        }
    }
}
