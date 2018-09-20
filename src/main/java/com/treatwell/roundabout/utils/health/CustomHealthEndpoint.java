package com.treatwell.roundabout.utils.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller endpoint that's auto-configured into every application importing this
 * library, by virtue of being listed as an entry under
 * {@code org.springframework.boot.autoconfigure.EnableAutoConfiguration} in the
 * {@code spring.factories} file within this module.
 *
 * <p>
 * This class has no conditions on it, and will thus always be included in any
 * such application.
 * </p>
 */
@RestController
public class CustomHealthEndpoint {

    private final HealthEndpoint healthEndpoint;

    public CustomHealthEndpoint(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/__custom/health")
    @ResponseStatus(HttpStatus.OK)
    public String health() {
        Health current = healthEndpoint.health();
        return current.getStatus() + "|" + current.getDetails();
    }
}
