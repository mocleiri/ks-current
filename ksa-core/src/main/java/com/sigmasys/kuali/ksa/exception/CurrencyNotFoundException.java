package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a currency is not found.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class CurrencyNotFoundException extends GenericException {

    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
