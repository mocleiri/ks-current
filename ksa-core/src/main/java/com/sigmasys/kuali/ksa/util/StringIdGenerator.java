package com.sigmasys.kuali.ksa.util;

/**
 * A custom String ID Generator. Typically used by Hibernate to generate JPA entity IDs.
 *
 * @author Michael Ivanov
 */
public class StringIdGenerator extends AbstractIdGenerator<String> {

    private static final GuidGenerator guidGenerator = new GuidGenerator();


    @Override
    public String generateId() {
        return generateString();
    }

    public static String generateString() {
        return guidGenerator.getNewGuid();
    }
}
