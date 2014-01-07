package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when an account type has not been found.
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class AccountTypeNotFoundException extends GenericException {

    public AccountTypeNotFoundException(String message) {
        super(message);
    }

}
