package com.treatwell.roundabout.utils.health;

import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorProperties;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures a {@link BroadcastingHealthAggregator} which will broadcast the current
 * state as an {@link ApplicationHealthEvent} across the Spring event bus
 * upon every health check.
 */
@Configuration
@ConditionalOnClass(HealthIndicator.class)
@EnableConfigurationProperties(HealthIndicatorProperties.class)
public class BroadcastHealthAggregatorAutoConfiguration {

    private final HealthIndicatorProperties properties;

    public BroadcastHealthAggregatorAutoConfiguration(HealthIndicatorProperties properties) {
        this.properties = properties;
    }

    @Bean
    public BroadcastingHealthAggregator healthAggregator() {
        OrderedHealthAggregator healthAggregator = new OrderedHealthAggregator();
        if (this.properties.getOrder() != null) {
            healthAggregator.setStatusOrder(this.properties.getOrder());
        }
        return new BroadcastingHealthAggregator(healthAggregator);
    }

    @Bean
    HealthLoggingListener healthLoggingListener() {
        return new HealthLoggingListener();
    }
}
