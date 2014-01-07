package com.sigmasys.kuali.ksa.model;

/**
 * Transaction type value enum.
 * It is used to distinguish different transaction types in the KSA code and UI forms
 *
 * @author Michael Ivanov
 */
public enum TransactionTypeValue {

    CHARGE(TransactionTypeValue.CHARGE_CODE),
    PAYMENT(TransactionTypeValue.PAYMENT_CODE),
    DEFERMENT(TransactionTypeValue.DEFERMENT_CODE);

    public static final String CHARGE_CODE = "TDC";
    public static final String PAYMENT_CODE = "TCP";
    public static final String DEFERMENT_CODE = "TCD";

    private String code;

    private TransactionTypeValue(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        switch (this) {
            case CHARGE:
                return "Charge";
            case PAYMENT:
                return "Payment";
            case DEFERMENT:
                return "Deferment";
        }
        throw new IllegalStateException("No transaction type description found for " + name() + " value");
    }
}
