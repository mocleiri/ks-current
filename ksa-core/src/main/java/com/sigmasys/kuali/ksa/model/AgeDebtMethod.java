package com.sigmasys.kuali.ksa.model;

/**
 * Age Debt Method.
 *
 * @author Michael Ivanov
 */
public enum AgeDebtMethod {

    BALANCE_FORWARD,
    OPEN_ITEM;

    @Override
    public String toString() {
        switch (this) {
            case BALANCE_FORWARD:
                return "Balance Forward";
            case OPEN_ITEM:
                return "Open Item";
        }
        throw new IllegalStateException("No age debt method found for " + name() + " value");
    }

}
