package com.treatwell.roundabout.app;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link HealthIndicator} implementation that allows us to change its state
 * manually at runtime. This is then used to demonstrate that hitting the
 * {@code /actuator/health} endpoint once the state is set to DOWN will
 * broadcast that information across the Spring event bus.
 */
@RestController
public class MutableHealthIndicator implements HealthIndicator {

    private Status status = Status.UP;
    private String message = "All working well!";

    @Override
    public Health health() {
        return Health.status(status).withDetail("message", message).build();
    }

    // This should normally be @PostMapping or @PutMapping, but for demo, I'm allowing
    // updates from the browser address bar
    @GetMapping("/health/mutable/{status}/")
    public String updateHealth(@PathVariable Status status, @RequestParam String message) {
        this.status = status;
        this.message = message;
        return "Health status is now " + status;
    }
}
