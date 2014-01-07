package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a refund is not verified.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class RefundNotVerifiedException extends GenericException {

    public RefundNotVerifiedException(String message) {
        super(message);
    }
}
