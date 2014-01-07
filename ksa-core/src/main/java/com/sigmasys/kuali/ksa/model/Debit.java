package com.sigmasys.kuali.ksa.model;

import javax.persistence.MappedSuperclass;

/**
 * An abstract debit class. The abstraction exists in case other types of debits are to be added at a later time. The concrete Charge class should be used for instantiation of
 * actual debits.
 *
 * @author Michael Ivanov
 */
@MappedSuperclass
public abstract class Debit extends Transaction {


}

