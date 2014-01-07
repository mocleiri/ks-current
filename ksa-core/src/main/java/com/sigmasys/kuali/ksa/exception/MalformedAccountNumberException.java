package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when an account number is invalid.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class MalformedAccountNumberException extends GenericException {

    public MalformedAccountNumberException(String message) {
        super(message);
    }
}
