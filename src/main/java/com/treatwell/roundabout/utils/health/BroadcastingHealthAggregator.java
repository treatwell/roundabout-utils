package com.treatwell.roundabout.utils.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Whenever a health-check is executed, this aggregator will aggregate as normal, but also broadcast
 * the result to the Spring {@link ApplicationEventPublisher} system so that other components can react
 * to the application (or components of it) becoming unhealthy.
 */
public class BroadcastingHealthAggregator implements HealthAggregator, ApplicationEventPublisherAware {
    private final HealthAggregator underlying;
    private ApplicationEventPublisher applicationEventPublisher;

    public BroadcastingHealthAggregator(HealthAggregator underlying) {
        this.underlying = underlying;
    }

    @Override
    public Health aggregate(Map<String, Health> healths) {
        Health health = underlying.aggregate(healths);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(ApplicationHealthEvent.create(health));
        }
        return health;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
