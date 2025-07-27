package com.ahmadda.infra.slack;

import com.ahmadda.domain.Member;
import com.ahmadda.infra.slack.config.SlackAlarmProperties;
import com.ahmadda.infra.slack.dto.MemberCreationAlarmPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
@Profile("prod")
@EnableConfigurationProperties(SlackAlarmProperties.class)
public class SlackReminder {

    private final SlackAlarmProperties slackAlarmProperties;
    private final RestClient restClient;

    public SlackReminder(RestClient.Builder restClientBuilder, SlackAlarmProperties slackAlarmProperties) {
        this.slackAlarmProperties = slackAlarmProperties;

        restClientBuilder.requestFactory(simpleClientHttpRequestFactory());
        restClientBuilder.baseUrl(slackAlarmProperties.getPostMessageUrl());
        restClientBuilder.defaultHeaders(headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(slackAlarmProperties.getBotToken());
        });

        restClient = restClientBuilder.build();
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(slackAlarmProperties.getConnectTimeout());
        factory.setReadTimeout(slackAlarmProperties.getReadTimeout());

        return factory;
    }

    public void alarmMemberCreation(final Member member) {
        try {
            restClient.post()
                    .body(MemberCreationAlarmPayload.create(member, slackAlarmProperties.getChannelId()));
        } catch (RestClientException e) {
            log.error("Slack 회원 가입 알림 전송에 실패했습니다. email={}, error={}", member.getEmail(), e.getMessage());
        }
    }
}
