package com.ahmadda.infra.database;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(
            prefix = "spring.datasource.replication",
            name = "enabled",
            havingValue = "true"
    )
    public DataSource routingDataSource(
            @Qualifier("writerDataSource") DataSource writer,
            @Qualifier("readerDataSource") DataSource reader
    ) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("writer", writer);
        targetDataSources.put("reader", reader);

        ReplicationRoutingDataSource routing = new ReplicationRoutingDataSource();
        routing.setDefaultTargetDataSource(writer);
        routing.setTargetDataSources(targetDataSources);
        return routing;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.writer")
    @ConditionalOnProperty(
            prefix = "spring.datasource.replication",
            name = "enabled",
            havingValue = "true"
    )
    public DataSource writerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.reader")
    @ConditionalOnProperty(
            prefix = "spring.datasource.replication",
            name = "enabled",
            havingValue = "true"
    )
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().build();
    }
}
