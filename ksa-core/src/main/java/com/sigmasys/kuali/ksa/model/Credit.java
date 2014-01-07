package com.sigmasys.kuali.ksa.model;

import javax.persistence.MappedSuperclass;

/**
*  The Credit class is entirely abstract and exists only to differentiate the differential between credits and debits.
*
*  @author Michael Ivanov
*/

@MappedSuperclass
public abstract class Credit extends Transaction {

}

