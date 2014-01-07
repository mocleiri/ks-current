package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a general ledger account is invalid.
 *
 * @author Michael Ivanov
 *         Date: 8/25/12
 */
public class InvalidGeneralLedgerAccountException extends GenericException {

    public InvalidGeneralLedgerAccountException(String message) {
        super(message);
    }

}
