/**
 * Package-level Hibernate type definitions go here
 *
 * @author Michael Ivanov
 */

@TypeDefs({@TypeDef(name = "unitNumber", typeClass = com.sigmasys.kuali.ksa.model.fm.UnitNumber.class)})
package com.sigmasys.kuali.ksa.model.fm;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;