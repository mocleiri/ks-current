package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when an Information entity not been found.
 *
 * @author Michael Ivanov
 */
public class InformationNotFoundException extends GenericException {

    public InformationNotFoundException(String message) {
        super(message);
    }
}
