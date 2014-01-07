package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a routing number is invalid.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class MalformedRoutingNumberException extends GenericException {

    public MalformedRoutingNumberException(String message) {
        super(message);
    }
}
