package com.sigmasys.kuali.ksa.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A custom Long ID Generator. Typically used by Hibernate to generate JPA entity IDs.
 *
 * @author Michael Ivanov
 */
public class LongIdGenerator extends AbstractIdGenerator<Long> {

    private final AtomicLong longIdGenerator = new AtomicLong((System.currentTimeMillis() - 1384922000000L) * 1000000);


    @Override
    public Long generateId() {
        return generateLong();
    }

    public long generateLong() {
        return Math.abs(longIdGenerator.incrementAndGet());
    }

}
