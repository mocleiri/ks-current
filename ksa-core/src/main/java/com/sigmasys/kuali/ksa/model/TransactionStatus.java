package com.sigmasys.kuali.ksa.model;


/**
 * Transaction status model.
 *
 * @author Michael Ivanov
 */
public enum TransactionStatus implements Identifiable {

    ACTIVE,
    BOUNCING,
    REFUNDING,
    REFUND_REQUESTED,
    EXPIRED,
    CANCELLING,
    WRITING_OFF,
    REVERSING,
    DISCOUNTING,
    TRANSFERRING,
    RECIPROCAL_OFFSET;


    @Override
    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        switch (this) {
            case ACTIVE:
                return "Active";
            case BOUNCING:
                return "Bouncing";
            case REFUNDING:
                return "Refunding";
            case REFUND_REQUESTED:
                return "Refund Requested";
            case EXPIRED:
                return "Expired";
            case CANCELLING:
                return "Cancelling";
            case WRITING_OFF:
                return "Writing Off";
            case REVERSING:
                return "Reversing";
            case DISCOUNTING:
                return "Discounting";
            case TRANSFERRING:
                return "Transferring";
            case RECIPROCAL_OFFSET:
                return "Reciprocal Offset";
        }
        throw new IllegalStateException("No Transaction status found for " + name() + " value");
    }


}
