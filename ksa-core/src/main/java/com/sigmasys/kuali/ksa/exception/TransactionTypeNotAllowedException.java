package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction type is not allowed for whatever reason.
 *
 * @author Michael Ivanov
 */
public class TransactionTypeNotAllowedException extends GenericException {

    public TransactionTypeNotAllowedException(String message) {
        super(message);
    }
}
