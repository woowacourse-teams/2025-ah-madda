package com.ahmadda.infra.notification.slack;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.infra.notification.slack.config.SlackAlarmProperties;
import com.ahmadda.infra.notification.slack.dto.MemberCreatAlarmRequestBody;
import com.ahmadda.infra.notification.slack.exception.SlackAlarmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

@Slf4j
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class AsyncSlackAlarm implements SlackAlarm {

    private final RestClient restClient;
    private final SlackAlarmProperties slackAlarmProperties;

    public AsyncSlackAlarm(
            final RestClient.Builder restClientBuilder,
            final SlackAlarmProperties slackAlarmProperties
    ) {
        restClientBuilder.requestFactory(simpleClientHttpRequestFactory(slackAlarmProperties));
        restClientBuilder.baseUrl(slackAlarmProperties.getPostMessageUrl());
        restClientBuilder.defaultHeaders(headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(slackAlarmProperties.getBotToken());
        });

        this.restClient = restClientBuilder.build();
        this.slackAlarmProperties = slackAlarmProperties;
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(final SlackAlarmProperties slackAlarmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(slackAlarmProperties.getConnectTimeout());
        factory.setReadTimeout(slackAlarmProperties.getReadTimeout());

        return factory;
    }

    @Override
    @Async("bulkheadExecutor")
    public void alarmMemberCreation(final MemberCreateAlarmPayload memberCreateAlarmPayload) {
        try {
            ResponseSpec retrieve = restClient.post()
                    .body(MemberCreatAlarmRequestBody.create(
                            memberCreateAlarmPayload,
                            slackAlarmProperties.getChannelId()
                    ))
                    .retrieve();
            ResponseEntity<Void> bodilessEntity = retrieve.toBodilessEntity();

            if (bodilessEntity.getStatusCode() != HttpStatus.OK) {
                throw new SlackAlarmException("유저 생성 슬랙 알람을 보내는데 실패 하였습니다");
            }
        } catch (Exception e) {
            log.error(
                    "Slack 회원 가입 알림 전송에 실패했습니다. member={}",
                    memberCreateAlarmPayload,
                    e
            );
        }
    }
}
