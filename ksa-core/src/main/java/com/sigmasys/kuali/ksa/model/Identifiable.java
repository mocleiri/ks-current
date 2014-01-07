package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;

/**
 * This interface will normally be implemented by all JPA entities
 *
 * @author Michael Ivanov
 * Date: 3/13/12
 * Time: 3:51 PM
 */
public interface Identifiable extends Serializable {

    Serializable getId();

}
