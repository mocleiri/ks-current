package com.sigmasys.kuali.ksa.model;

/**
 * Reversal status
 *
 * @author Michael Ivanov
 */
public enum ReversalStatus implements Identifiable {

    REVERSED(ReversalStatus.REVERSED_CODE),
    PARTIALLY_REVERSED(ReversalStatus.PARTIALLY_REVERSED_CODE),
    NOT_REVERSED(ReversalStatus.NOT_REVERSED_CODE);

    public static final String REVERSED_CODE = "R";
    public static final String PARTIALLY_REVERSED_CODE = "P";
    public static final String NOT_REVERSED_CODE = "N";

    private String id;

    private ReversalStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case REVERSED:
                return "Reversed";
            case PARTIALLY_REVERSED:
                return "Partially Reversed";
            case NOT_REVERSED:
                return "Not Reversed";
        }
        throw new IllegalStateException("No reversal status found for " + name() + " value");
    }

}
