package com.treatwell.roundabout.utils.health;

import java.io.Serializable;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;


/**
 * An event class to denote that a health check has occurred, including the result of the check. This
 * allows for listeners to react to such events, and when applications become unhealthy to disable
 * certain functionality for example.
 */
public abstract class ApplicationHealthEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * @return the most recent health check result, which fired this event
     * in the first place.
     */
    public abstract Health getHealth();

    /**
     * @return the {@link Status} for the most recent health check event.
     */
    public Status getStatus() {
        return getHealth().getStatus();
    }

    public static ApplicationHealthEvent create(Health health) {
        if (Status.UP.equals(health.getStatus())) {
            return ApplicationHealthyEvent.of(health);
        }
        return ApplicationUnhealthyEvent.of(health);
    }

    /**
     * Sub-class to allow event listeners to specifically trap events
     * that communicate the application is healthy.
     */
    public static class ApplicationHealthyEvent extends ApplicationHealthEvent {
        private static final long serialVersionUID = 1L;

        private Health health;

        public static ApplicationHealthyEvent of(Health health) {
            return new ApplicationHealthyEvent(health);
        }

        private ApplicationHealthyEvent(Health health) {
            this.health = health;
            if (!Status.UP.equals(getHealth().getStatus())) {
                throw new IllegalArgumentException("ApplicationUnhealthyEvent must have UP Health");
            }
        }

        @Override
        public Health getHealth() {
            return health;
        }
    }

    /**
     * Sub-class to allow event listeners to specifically trap events
     * that communicate the application becoming unhealthy in some way.
     */
    public static class ApplicationUnhealthyEvent extends ApplicationHealthEvent {
        private static final long serialVersionUID = 1L;

        private Health health;

        public static ApplicationUnhealthyEvent of(Health health) {
            return new ApplicationUnhealthyEvent(health);
        }

        private ApplicationUnhealthyEvent(Health health) {
            this.health = health;
            if (Status.UP.equals(getHealth().getStatus())) {
                throw new IllegalArgumentException("ApplicationUnhealthyEvent must NOT have UP Health");
            }
        }

        @Override
        public Health getHealth() {
            return health;
        }
    }
}
