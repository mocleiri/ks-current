package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when ACH information is invalid.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class InvalidAchInformationException extends GenericException {

    public InvalidAchInformationException(String message) {
        super(message);
    }
}
