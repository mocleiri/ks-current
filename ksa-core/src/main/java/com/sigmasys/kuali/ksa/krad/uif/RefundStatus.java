package com.sigmasys.kuali.ksa.model;

/**
 * Refund status
 *
 * @author Michael Ivanov
 */
public enum RefundStatus implements Identifiable {

    UNVERIFIED(RefundStatus.UNVERIFIED_CODE),
    VERIFIED(RefundStatus.VERIFIED_CODE),
    COMPLETED(RefundStatus.COMPLETED_CODE),
    FAILED(RefundStatus.FAILED_CODE);

    public static final String UNVERIFIED_CODE = "U";
    public static final String VERIFIED_CODE = "V";
    public static final String COMPLETED_CODE = "R";
    public static final String FAILED_CODE = "F";

    private String id;

    private RefundStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case UNVERIFIED:
                return "Unverified";
            case FAILED:
                return "Failed";
            case VERIFIED:
                return "Verified";
            case COMPLETED:
                return "Completed";
        }
        throw new IllegalStateException("No refund status found for " + name() + " value");
    }

}
