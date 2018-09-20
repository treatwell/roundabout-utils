package com.treatwell.roundabout.app.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sample repository for the {@link PhonebookEntry}. Commenting out the
 * {@link Transactional} annotation will cause an exception on startup, as the
 * configuration validator requires that all our {@link Repository} instances
 * have such annotations.
 */
@Transactional("transactionManager")
public interface PhonebookEntryRepository extends CrudRepository<PhonebookEntry, Long> {
}
