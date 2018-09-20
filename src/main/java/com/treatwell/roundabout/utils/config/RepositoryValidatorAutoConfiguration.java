package com.treatwell.roundabout.utils.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@ConditionalOnClass(Repository.class)
@Configuration
@Import(RepositoryValidatorAutoConfiguration.RepositoryValidator.class)
public class RepositoryValidatorAutoConfiguration {

    /**
     * Example configuration validator component that enforces that all {@link Repository} classes
     * have a non-blank @{@link Transactional} annotation declared on them.
     */
    static class RepositoryValidator implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof RepositoryFactoryBeanSupport) {
                RepositoryFactoryBeanSupport<?, ?, ?> factory = (RepositoryFactoryBeanSupport<?, ?, ?>) bean;
                Class<?> repo = factory.getRepositoryInformation().getRepositoryInterface();

                // Do we have a Transactional annotation on the Repository?
                Transactional transactionAnno = AnnotationUtils.findAnnotation(repo, Transactional.class);
                if (transactionAnno == null || StringUtils.isEmpty(transactionAnno.value())) {
                    throw new IllegalStateException(repo.getSimpleName()
                            + "instance must be configured with an @Transactional annotation");
                }
            }
            return bean;
        }
    }
}
