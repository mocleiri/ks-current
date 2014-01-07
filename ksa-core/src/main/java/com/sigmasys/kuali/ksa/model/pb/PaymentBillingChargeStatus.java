package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Payment billing charge status constants.
 *
 * @author Michael Ivanov
 */
public enum PaymentBillingChargeStatus implements Identifiable {

    ACTIVE(PaymentBillingChargeStatus.ACTIVE_CODE),
    REVERSED(PaymentBillingChargeStatus.REVERSED_CODE),
    INITIALIZED(PaymentBillingChargeStatus.INITIALIZED_CODE),
    ALLOWABLE(PaymentBillingChargeStatus.ALLOWABLE_CODE),
    SCHEDULED(PaymentBillingChargeStatus.SCHEDULED_CODE);

    public static final String ACTIVE_CODE = "A";
    public static final String REVERSED_CODE = "R";
    public static final String INITIALIZED_CODE = "I";
    public static final String ALLOWABLE_CODE = "L";
    public static final String SCHEDULED_CODE = "S";

    private String id;

    private PaymentBillingChargeStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case ACTIVE:
                return "Active";
            case REVERSED:
                return "Reversed";
            case INITIALIZED:
                return "Initialized";
            case ALLOWABLE:
                return "Allowable";
            case SCHEDULED:
                return "Scheduled";

        }
        throw new IllegalStateException("No payment billing plan status found for " + name() + " value");
    }
}
