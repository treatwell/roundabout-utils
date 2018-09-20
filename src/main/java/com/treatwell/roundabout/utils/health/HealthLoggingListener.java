package com.treatwell.roundabout.utils.health;

import static java.util.stream.Collectors.joining;

import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;

/**
 * Logs recorded changes to application health.
 */
public class HealthLoggingListener implements ApplicationListener<PayloadApplicationEvent<? extends ApplicationHealthEvent>> {
    private static final Logger LOG = LoggerFactory.getLogger(HealthLoggingListener.class);

    private volatile Status current = null;

    @Override
    public void onApplicationEvent(PayloadApplicationEvent<? extends ApplicationHealthEvent> event) {
        if (!Objects.equals(current, event.getPayload().getStatus())) {
            Status old = current;
            current = event.getPayload().getStatus();
            if (Status.UP.equals(current)) {
                LOG.info("Application Status has returned to {} (was {})", current, old);
            } else {
                // Look at the detail of the event to find the underlying details that are DOWN
                String failures = event.getPayload().getHealth().getDetails().entrySet().stream()
                        .filter(e -> e.getValue() instanceof Health)
                        .map(e -> new SimpleEntry<>(e.getKey(), (Health) e.getValue()))
                        .filter(e -> e.getValue().getStatus() != Status.UP)
                        .map(e -> e.getKey() + " (" + e.getValue().getStatus() + ")")
                        .collect(joining(", ", "[", "]"));
                LOG.warn("Application Status is now {} (was {}). Failures: {}", current, old, failures);
            }
        }
    }
}
