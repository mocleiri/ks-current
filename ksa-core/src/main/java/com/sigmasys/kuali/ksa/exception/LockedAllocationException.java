package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a transaction has a locked allocation
 *
 * @author Michael Ivanov
 *         Date: 8/7/12
 */
public class LockedAllocationException extends GenericException {

    public LockedAllocationException(String message) {
        super(message);
    }
}
