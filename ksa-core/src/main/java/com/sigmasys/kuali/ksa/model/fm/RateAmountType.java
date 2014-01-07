package com.sigmasys.kuali.ksa.model.fm;


import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Rate amount type model.
 *
 * @author Michael Ivanov
 */
public enum RateAmountType implements Identifiable {

    FLAT,
    FIXED,
    FLEXIBLE;


    @Override
    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        switch (this) {
            case FLAT:
                return "Flat";
            case FIXED:
                return "Fixed";
            case FLEXIBLE:
                return "Flexible";
        }
        throw new IllegalStateException("No Rate amount type found for " + name() + " value");
    }


}
