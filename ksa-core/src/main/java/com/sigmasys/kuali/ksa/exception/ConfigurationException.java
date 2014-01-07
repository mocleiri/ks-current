package com.sigmasys.kuali.ksa.exception;

/**
 * This exception is thrown when a configuration is missing or invalid.
 *
 * @author Michael Ivanov
 */
public class ConfigurationException extends GenericException {

    public ConfigurationException(String message) {
        super(message);
    }

}
