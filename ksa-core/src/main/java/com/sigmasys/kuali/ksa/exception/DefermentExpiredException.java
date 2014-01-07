package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a deferment has already been expired.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class DefermentExpiredException extends GenericException {

    public DefermentExpiredException(String message) {
        super(message);
    }

}
