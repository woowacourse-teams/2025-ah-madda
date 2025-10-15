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
     * 스레드풀 크기 산정은 Brian Goetz가 『Java Concurrency in Practice』에서 제시한 추정식에 기반한다.
     * <pre>
     * Threads = Cores × TargetUtilization × (1 + Wait / Service)
     * </pre>
     * 스케줄러는 CPU 바운드 작업에 가깝기 때문에 Wait/Service ≈ 0 으로 간주하며,
     * 결과적으로 Threads ≈ Cores 수준이 가장 효율적이다.
     * 다만, 저사양 환경(1코어)에서는 병렬 스케줄링을 보장하기 위해 최소 2개로 보정한다.
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
        scheduler.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);

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
