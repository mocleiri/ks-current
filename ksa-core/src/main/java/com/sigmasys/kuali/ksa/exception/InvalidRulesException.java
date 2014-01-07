package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a Drools rule set is invalid and cannot compile.
 *
 * @author Michael Ivanov
 */
public class InvalidRulesException extends GenericException {

    public InvalidRulesException(String message) {
        super(message);
    }

}
