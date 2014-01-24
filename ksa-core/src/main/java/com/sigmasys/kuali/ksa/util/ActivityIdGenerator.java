package com.sigmasys.kuali.ksa.util;


/**
 * A dedicated ID Generator for Activity JPA entity. Singleton.
 * There could be a very large number of Activity persistent instances in the system.
 *
 * @author Michael Ivanov
 */
public class ActivityIdGenerator extends LongIdGenerator {

    private static final LongIdGenerator idGenerator = new ActivityIdGenerator();

    public static LongIdGenerator getInstance() {
        return idGenerator;
    }

}
