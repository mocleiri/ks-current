package com.sigmasys.kuali.ksa.model;

/**
 * Created by: dmulderink on 9/11/12 at 12:39 PM
 */
public enum SearchTypeValue {

    ACCOUNT,
    TRANSACTION,
    ACTIVITY;

    @Override
    public String toString() {
        switch (this) {
            case ACCOUNT:
                return "Account";
            case TRANSACTION:
                return "Transaction";
            case ACTIVITY:
                return "Activity";
        }
        throw new IllegalStateException("No search type description found for " + name() + " value");
    }

}
