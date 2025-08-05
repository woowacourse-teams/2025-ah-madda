package com.ahmadda.infra.aws;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class HealthMetricExporter {

    public HealthMetricExporter(final MeterRegistry registry, final HealthEndpoint healthEndpoint) {
        Gauge.builder("app.health.status", () -> {
            HealthComponent health = healthEndpoint.health();
            return health.getStatus().equals(Status.UP) ? 1 : 0;
        }).register(registry);
    }
}
