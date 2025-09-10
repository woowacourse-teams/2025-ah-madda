package com.ahmadda.infra.config;

import com.ahmadda.infra.logger.AsyncTraceLoggingDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // TODO. 추후 ThreadPoolTaskExecutor의 thread 설정 변경 및 graceful shutdown 고려 필요
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(new AsyncTraceLoggingDecorator());

        return executor;
    }
}
