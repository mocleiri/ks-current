package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction cancellation rule is invalid.
 *
 * @author Michael Ivanov
 */
public class InvalidCancellationRuleException extends GenericException {

    public InvalidCancellationRuleException(String message) {
        super(message);
    }

}
