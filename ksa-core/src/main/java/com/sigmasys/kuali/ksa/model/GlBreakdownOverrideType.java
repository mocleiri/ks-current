package com.sigmasys.kuali.ksa.model;

/**
 * GL Breakdown override types.
 *
 * @author Michael Ivanov
 */
public enum GlBreakdownOverrideType implements Identifiable {

    TRANSACTION(GlBreakdownOverrideType.TRANSACTION_CODE),
    MANIFEST(GlBreakdownOverrideType.MANIFEST_CODE);

    public static final String TRANSACTION_CODE = "T";
    public static final String MANIFEST_CODE = "M";

    private String id;

    private GlBreakdownOverrideType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case TRANSACTION:
                return "Transaction GL Override";
            case MANIFEST:
                return "Manifest GL Override";
        }
        throw new IllegalStateException("No GL Breakdown Override type found for " + name() + " value");
    }
}
