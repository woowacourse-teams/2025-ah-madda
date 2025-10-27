package com.ahmadda.common.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class HealthMetricBinder implements MeterBinder {

    private final HealthEndpoint healthEndpoint;

    public HealthMetricBinder(final HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @Override
    public void bindTo(final MeterRegistry registry) {
        Gauge.builder(
                        "app.health.status", () -> {
                            HealthComponent health = healthEndpoint.health();
                            Status status = health.getStatus();

                            if (Status.UP.equals(status)) {
                                return 1.0;
                            }
                            return 0.0;
                        }
                )
                .register(registry);
    }
}
