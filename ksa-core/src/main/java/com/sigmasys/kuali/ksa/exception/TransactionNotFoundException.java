package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction type has not been found.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class TransactionNotFoundException extends GenericException {

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
