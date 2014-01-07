package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a refund has already been cancelled.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class RefundCancelledException extends GenericException {

    public RefundCancelledException(String message) {
        super(message);
    }
}
