package com.sigmasys.kuali.ksa.model;

/**
 * General ledger transaction status.
 *
 * @author Michael Ivanov
 */
public enum GlTransactionStatus implements Identifiable {

    QUEUED(GlTransactionStatus.QUEUED_CODE),
    IN_PROCESS(GlTransactionStatus.IN_PROCESS_CODE),
    WAITING(GlTransactionStatus.WAITING_CODE),
    COMPLETED(GlTransactionStatus.COMPLETED_CODE),
    FAILED(GlTransactionStatus.FAILED_CODE);

    public static final String QUEUED_CODE = "Q";
    public static final String IN_PROCESS_CODE = "P";
    public static final String WAITING_CODE = "W";
    public static final String COMPLETED_CODE = "C";
    public static final String FAILED_CODE = "F";

    private String id;

    private GlTransactionStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case QUEUED:
                return "Queued";
            case FAILED:
                return "Failed";
            case IN_PROCESS:
                return "In Process";
            case WAITING:
                return "Waiting";
            case COMPLETED:
                return "Completed";
        }
        throw new IllegalStateException("No GL transaction status found for " + name() + " value");
    }

}
