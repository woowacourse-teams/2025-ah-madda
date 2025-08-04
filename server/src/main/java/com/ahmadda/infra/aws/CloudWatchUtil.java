package com.ahmadda.infra.aws;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Component
public class CloudWatchUtil {

    private CloudWatchAsyncClient cloudWatch;

    @Bean
    public MeterRegistry meterRegistry() {

        // 자격 증명 생략: SDK가 EC2 인스턴스의 IAM Role을 자동으로 사용
        CloudWatchAsyncClient client = CloudWatchAsyncClient.builder()
                .region(Region.of("ap-northeast-2")) // 서울 리전 예시
                .build();

        CloudWatchConfig config = new CloudWatchConfig() {
            @Override
            public String get(String key) {
                return null; // 기본값 사용
            }

            @Override
            public String namespace() {
                return "EC2/ahmadda-api"; // CloudWatch에서 볼 이름
            }
        };

        this.cloudWatch = client;

        return new CloudWatchMeterRegistry(config, Clock.SYSTEM, cloudWatch);
    }
}
