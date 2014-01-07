package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction type has not been found.
 *
 * @author Michael Ivanov
 */
public class TransactionTypeNotFoundException extends GenericException {

    public TransactionTypeNotFoundException(String message) {
        super(message);
    }
}
