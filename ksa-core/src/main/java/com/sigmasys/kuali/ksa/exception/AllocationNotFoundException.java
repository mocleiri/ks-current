package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when an allocation is not found.
 *
 * @author Michael Ivanov
 *         Date: 8/25/12
 */
public class AllocationNotFoundException extends GenericException {

    public AllocationNotFoundException(String message) {
        super(message);
    }
}
