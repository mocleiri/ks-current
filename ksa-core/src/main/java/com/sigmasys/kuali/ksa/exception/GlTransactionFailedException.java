package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a refund has already been cancelled.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class GlTransactionFailedException extends GenericException {

    private Long sourceTransactionId;

    public GlTransactionFailedException(Long sourceTransactionId, String message) {
        super(message);
        this.sourceTransactionId = sourceTransactionId;
    }

    public Long getSourceTransactionId() {
        return sourceTransactionId;
    }
}
