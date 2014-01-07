package com.sigmasys.kuali.ksa.model;

/**
 * Cash Limit Event status
 *
 * @author Michael Ivanov
 */
public enum CashLimitEventStatus implements Identifiable {

    QUEUED(CashLimitEventStatus.QUEUED_CODE),
    COMPLETED(CashLimitEventStatus.COMPLETED_CODE),
    IGNORED(CashLimitEventStatus.IGNORED_CODE);

    public static final String QUEUED_CODE = "Q";
    public static final String COMPLETED_CODE = "C";
    public static final String IGNORED_CODE = "I";

    private String id;

    private CashLimitEventStatus(String id) {
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
            case COMPLETED:
                return "Completed";
            case IGNORED:
                return "Ignored";
        }
        throw new IllegalStateException("No cash limit event status found for " + name() + " value");
    }

}
