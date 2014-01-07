package com.sigmasys.kuali.ksa.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to mark JPA entities that need to be audited by
 * the audit trail system.
 *
 * @author Michael Ivanov
 *         Date: 5/31/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Auditable {

}
