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
                .expirationAfterWrite(basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(30)))
                .build();
    }

    @Bean
    public BucketConfiguration memberRateLimitConfig() {
        return BucketConfiguration.builder()
                .addLimit(
                        limit -> limit
                                .capacity(1000)
                                // TODO. 추후 버스트 허용이 필요할 경우 refillGreedy 고려
                                .refillIntervally(1000, Duration.ofMinutes(30))
                )
                .build();
    }
}
