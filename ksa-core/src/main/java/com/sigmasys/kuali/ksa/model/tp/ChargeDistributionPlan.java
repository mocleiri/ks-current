package com.sigmasys.kuali.ksa.model.tp;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Third-party ChargeDistributionPlan values.
 *
 * @author Michael Ivanov
 */
public enum ChargeDistributionPlan implements Identifiable {

    FULL(ChargeDistributionPlan.FULL_CODE),
    DIVIDED(ChargeDistributionPlan.DIVIDED_CODE);

    public static final String FULL_CODE = "F";
    public static final String DIVIDED_CODE = "D";

    private String id;

    private ChargeDistributionPlan(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case FULL:
                return "Full";
            case DIVIDED:
                return "Divided";
        }
        throw new IllegalStateException("No distribution plan found for " + name() + " value");
    }
}
