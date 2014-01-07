package com.sigmasys.kuali.ksa.util;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * Abstract ID Generator. Typically used by Hibernate to generate JPA entity IDs.
 *
 * @author Michael Ivanov
 */
public abstract class AbstractIdGenerator<T extends Serializable> implements IdentifierGenerator {

    public abstract T generateId();

    @Override
    public T generate(SessionImplementor session, Object object) {
        return generateId();
    }

}
