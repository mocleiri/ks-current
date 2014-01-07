package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a GL type has not been found.
 *
 * @author Michael Ivanov
 */
public class GeneralLedgerTypeNotFoundException extends GenericException {

    public GeneralLedgerTypeNotFoundException(String message) {
        super(message);
    }
}
