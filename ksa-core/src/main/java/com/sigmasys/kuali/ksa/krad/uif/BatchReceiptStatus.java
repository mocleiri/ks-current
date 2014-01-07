package com.sigmasys.kuali.ksa.model;

/**
 * Batch receipt status
 *
 * @author Michael Ivanov
 */
public enum BatchReceiptStatus implements Identifiable {

    IN_PROCESS(BatchReceiptStatus.IN_PROCESS_CODE),
    FAILED(BatchReceiptStatus.FAILED_CODE),
    PARTIALLY_ACCEPTED(BatchReceiptStatus.PARTIALLY_ACCEPTED_CODE),
    ACCEPTED(BatchReceiptStatus.ACCEPTED_CODE);

    public static final String IN_PROCESS_CODE = "Q";
    public static final String FAILED_CODE = "F";
    public static final String PARTIALLY_ACCEPTED_CODE = "P";
    public static final String ACCEPTED_CODE = "E";

    private String id;

    private BatchReceiptStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case IN_PROCESS:
                return "In Process";
            case FAILED:
                return "Failed";
            case PARTIALLY_ACCEPTED:
                return "Partially Accepted";
            case ACCEPTED:
                return "Accepted";
        }
        throw new IllegalStateException("No batch receipt status found for " + name() + " value");
    }

}
