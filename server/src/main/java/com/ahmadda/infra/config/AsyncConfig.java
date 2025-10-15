package com.ahmadda.infra.config;

import com.ahmadda.infra.logger.AsyncTraceLoggingDecorator;
import com.ahmadda.infra.logger.LoggingDiscardPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    /**
     * 공통 비동기 스레드풀 크기 산정은 Brian Goetz가
     * 『Java Concurrency in Practice』에서 제시한 추정식에 기반한다.
     * <p>
     * Threads = Cores × TargetUtilization × (1 + Wait / Service)
     * </p>
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(new AsyncTraceLoggingDecorator());

        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);

        executor.setRejectedExecutionHandler(new LoggingDiscardPolicy("taskExecutor"));

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
