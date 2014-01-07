package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a user has not been found.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class UserNotFoundException extends GenericException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
