package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction type is invalid.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class InvalidTransactionTypeException extends GenericException {

    public InvalidTransactionTypeException(String message) {
        super(message);
    }

}
