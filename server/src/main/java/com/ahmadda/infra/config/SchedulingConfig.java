package com.ahmadda.infra.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }

    /**
     * 공통 스케줄러 스레드풀.
     * <p>
     * 스케줄러는 예약된 작업의 정시 실행(시간 정확도) 을 보장하기 위해 별도의 워커 스레드풀을 유지한다.
     * CPU 활용률 최적화보다는, 하나의 스케줄 작업이 DB I/O 등으로 지연되더라도
     * 다른 예약 작업이 밀리지 않도록 병렬성을 확보하는 것이 목적이다.
     * <p>
     * 스레드풀 크기 산정은 사용 가능한 코어 수를 기준으로 하되,
     * 저사양 환경(1코어)에서도 병렬 스케줄링이 가능하도록 최소 2개로 보정한다.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("scheduler-");

        int cores = Runtime.getRuntime()
                .availableProcessors();
        scheduler.setPoolSize(Math.max(cores, 2));

        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setRemoveOnCancelPolicy(true);

        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public LockProvider lockProvider(final DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }
}
