package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * PaymentRoundingType values.
 *
 * @author Michael Ivanov
 */
public enum PaymentRoundingType implements Identifiable {

    FIRST(PaymentRoundingType.FIRST_CODE),
    LAST(PaymentRoundingType.LAST_CODE);

    public static final String FIRST_CODE = "F";
    public static final String LAST_CODE = "L";


    private String id;

    private PaymentRoundingType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case FIRST:
                return "Non-rounded First";
            case LAST:
                return "Non-rounded Last";
        }
        throw new IllegalStateException("No payment rounding type found for " + name() + " value");
    }
}
