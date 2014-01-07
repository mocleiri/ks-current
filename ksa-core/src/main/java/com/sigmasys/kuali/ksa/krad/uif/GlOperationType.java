package com.sigmasys.kuali.ksa.model;

/**
 * General ledger operation type.
 *
 * @author Michael Ivanov
 */
public enum GlOperationType implements Identifiable {

    CREDIT(GlOperationType.CREDIT_CODE),
    DEBIT(GlOperationType.DEBIT_CODE);

    public static final String CREDIT_CODE = "C";
    public static final String DEBIT_CODE = "D";

    private String id;

    private GlOperationType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case CREDIT:
                return "Credit";
            case DEBIT:
                return "Debit";
        }
        throw new IllegalStateException("No GL operation type found for " + name() + " value");
    }

}
