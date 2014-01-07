package com.sigmasys.kuali.ksa.model;

/**
 * GlBatchBaselineType defines baseline types used by GlBatchBaseline model.
 *
 * @author Michael Ivanov
 */
public enum GlBatchBaselineType implements Identifiable {

    GL_TYPE(GlBatchBaselineType.GL_TYPE_CODE),
    UNALLOCATED_CASH(GlBatchBaselineType.UNALLOCATED_CASH_CODE),
    DEFERMENT(GlBatchBaselineType.DEFERMENT_CODE);

    public static final String GL_TYPE_CODE = "G";
    public static final String UNALLOCATED_CASH_CODE = "U";
    public static final String DEFERMENT_CODE = "D";

    private String id;

    private GlBatchBaselineType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case GL_TYPE:
                return "General Ledger Type";
            case UNALLOCATED_CASH:
                return "Unallocated Cash";
            case DEFERMENT:
                return "Deferment";
        }
        throw new IllegalStateException("No GL batch baseline type found for " + name() + " value");
    }


}
