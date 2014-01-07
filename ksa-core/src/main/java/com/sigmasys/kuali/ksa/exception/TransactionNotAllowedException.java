package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction is not allowed for whatever reason.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class TransactionNotAllowedException extends GenericException {

    public TransactionNotAllowedException(String message) {
        super(message);
    }
}
