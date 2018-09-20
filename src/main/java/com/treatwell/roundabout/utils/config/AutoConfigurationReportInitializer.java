package com.treatwell.roundabout.utils.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jooq.lambda.Seq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcomes;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Initializer to add hooks so that we can report on the configuration once the application
 * has started up. Note that this doesn't need to be manually instantiated as it will be
 * automatically picked up by Spring Boot via the <code>META-INF/spring.factories</code>
 * file in this module.
 *
 * <p>This feature can be disabled by setting
 * <pre>
 * <code>
 * boot.auto-configuration-report.enabled=false
 * </code>
 * </pre>
 * as a property, property override, JVM argument etc.</p>
 *
 * <p>If you're running in a non-Spring-Boot managed hybrid environment, then you can choose
 * to simply declare an instance of this class as a bean on your context where you've enabled
 * an @EnableAutoConfiguration annotation.</p>
 */
public class AutoConfigurationReportInitializer extends ConditionEvaluationReportLoggingListener {
    public static final String ENABLED_PROPERTY = "boot.auto-configuration-report.enabled";
    private static final Logger LOG = LoggerFactory.getLogger(AutoConfigurationReportInitializer.class);

    private ConfigurableApplicationContext applicationContext;
    private ConditionEvaluationReport report;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        super.initialize(applicationContext);
        this.applicationContext = applicationContext;
        if (applicationContext instanceof GenericApplicationContext) {
            // Get the report early in case the context fails to load
            this.report = ConditionEvaluationReport.get(applicationContext.getBeanFactory());
        }
    }

    @Override
    public void logAutoConfigurationReport(boolean isCrashReport) {
        if (isEnabled()) {
            // We override the standard behaviour so that we'll always log the report
            // regardless of crash or successful startup, and do so at INFO level.
            // Secondly, we produce a slightly different format of report which is a bit
            // more human readable (conditions on separate lines, indented from the
            // component names)
            LOG.info(getLogMessage(report != null ? report :
                // Get the report here and now. In a failed startup, this will likely not
                // include useful information if we weren't able to retrieve the report on
                // startup, as the context isn't a GenericApplicationContext.
                ConditionEvaluationReport.get(applicationContext.getBeanFactory())));
        }
    }

    private boolean isEnabled() {
        Environment environment = applicationContext.getEnvironment();
        return environment != null && environment.getProperty(ENABLED_PROPERTY, "true").equals("true");
    }

    private String getLogMessage(ConditionEvaluationReport report) {
        boolean usefulReport = false;
        StringBuilder message = new StringBuilder()
                .append("\n")
                .append("=========================\n")
                .append("AUTO-CONFIGURATION REPORT\n")
                .append("=========================\n");

        message.append("\n")
                .append("Positive matches:\n")
                .append("-----------------\n");
        Map<String, ConditionAndOutcomes> shortOutcomes = orderByName(
                report.getConditionAndOutcomesBySource());
        if (!shortOutcomes.isEmpty()) {
            usefulReport = true;
        }
        shortOutcomes.entrySet().stream()
                .filter(entry -> entry.getValue().isFullMatch())
                .forEach(entry -> addLogMessage(message, entry.getKey(), entry.getValue()));

        message.append("\n")
                .append("Negative matches:\n")
                .append("-----------------\n");
        shortOutcomes.entrySet().stream()
            .filter(entry -> !entry.getValue().isFullMatch())
            .forEach(entry -> addLogMessage(message, entry.getKey(), entry.getValue()));

        message.append("\n")
                .append("Exclusions:\n")
                .append("-----------\n");
        if (report.getExclusions().isEmpty()) {
            message.append("    None\n");
        } else {
            usefulReport = true;
            for (String exclusion : report.getExclusions()) {
                message.append("   ").append(exclusion).append("\n");
            }
        }
        message.append("\n")
                .append("Unconditional classes:\n")
                .append("----------------------\n");
        if (report.getUnconditionalClasses().isEmpty()) {
            message.append("    None\n");
        } else {
            usefulReport = true;
            for (String unconditionalClass : report.getUnconditionalClasses()) {
                message.append("   ").append(unconditionalClass).append("\n");
            }
        }
        if (!usefulReport) {
            return "Nothing useful to report. No apparent Spring Boot activity occurred.";
        }
        return message.toString();
    }

    private void addLogMessage(StringBuilder message, String source,
            ConditionAndOutcomes conditionAndOutcomes) {
        message.append("   " + source);
        message.append(conditionAndOutcomes.isFullMatch() ? " matched\n" : " did not match\n");
        Seq.seq(conditionAndOutcomes).stream().flatMap(conditionAndOutcome -> {
            Function<String, String> formatter = s ->
                    s + " (" + ClassUtils.getShortName(conditionAndOutcome.getCondition().getClass()) + ")";
            if (!StringUtils.hasLength(conditionAndOutcome.getOutcome().getMessage())) {
                return Seq.of(conditionAndOutcome.getOutcome().isMatch() ? "matched"
                        : "did not match").map(formatter);
            }
            // Otherwise, we'll replace any @Conditional's in the message prefixing with \n,
            // split on that, and filter out any empty values
            return Seq.of(conditionAndOutcome.getOutcome().getMessage().replaceAll("@Conditional", "\n@Conditional").split("\n"))
                    .filter(StringUtils::hasLength)
                    .map(formatter);
        }).forEach(conditionMessage -> {
            message.append("      - ").append(conditionMessage).append("\n");
        });
    }

    private Map<String, ConditionAndOutcomes> orderByName(
            Map<String, ConditionAndOutcomes> outcomes) {
        Map<String, ConditionAndOutcomes> result = new LinkedHashMap<>();
        List<String> names = new ArrayList<>();
        Map<String, String> classNames = new HashMap<>();
        for (String name : outcomes.keySet()) {
            String shortName = ClassUtils.getShortName(name);
            names.add(shortName);
            classNames.put(shortName, name);
        }
        Collections.sort(names);
        for (String shortName : names) {
            result.put(shortName, outcomes.get(classNames.get(shortName)));
        }
        return result;
    }
}
