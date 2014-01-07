package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Alert
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(InformationTypeValue.ALERT_CODE)
public class Alert extends Information {

}

