package com.treatwell.roundabout.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Logs out every Spring {@link ApplicationEvent} received, so easily see the lifecycle of all such
 * events.
 */
public class SpringEventLogger implements ApplicationContextInitializer<ConfigurableApplicationContext>,
        ApplicationListener<ApplicationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SpringEventLogger.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(this);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        LOG.info("Received event {}: {}", event.getClass().getSimpleName(), event);
    }
}
