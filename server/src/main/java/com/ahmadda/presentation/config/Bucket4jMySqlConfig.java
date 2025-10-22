package com.ahmadda.presentation.config;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.mysql.Bucket4jMySQL;
import io.github.bucket4j.mysql.MySQLSelectForUpdateBasedProxyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import javax.sql.DataSource;

import static io.github.bucket4j.distributed.ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax;

@Configuration
public class Bucket4jMySqlConfig {

    @Bean
    public MySQLSelectForUpdateBasedProxyManager<Long> bucketProxyManager(final DataSource dataSource) {
        return Bucket4jMySQL
                .selectForUpdateBasedBuilder(dataSource)
                .expirationAfterWrite(basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(2)))
                .build();
    }

    /**
     * 사용자 단위 요청 제한 정책.
     * <p>
     * short-term(10분)은 순간 폭주를 제어하여 서비스의 안정성을 확보하고,
     * 우발적인 요청 증가를 고려해 자연스럽게 회복되도록 한다.
     * <p>
     * long-term(1시간)은 부하테스트 수준의 지속 요청을 감지해,
     * 지정된 주기 동안 완전 차단(Full Block)하여 시스템을 보호한다.
     */
    @Bean
    public BucketConfiguration memberRateLimitConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(300)
                        .refillGreedy(300, Duration.ofMinutes(10))
                )
                .addLimit(limit -> limit
                        .capacity(1500)
                        .refillIntervally(1500, Duration.ofHours(1))
                )
                .build();
    }
}
