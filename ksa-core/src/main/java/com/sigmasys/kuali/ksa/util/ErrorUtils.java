package com.sigmasys.kuali.ksa.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;

/**
 * This class contains utility-methods for handling error messages.
 *
 * @author Michael Ivanov
 */
public class ErrorUtils {

    private static final Log logger = LogFactory.getLog(ErrorUtils.class);

    private ErrorUtils() {
    }

    public static String getMessage(Throwable t) {
        return retrieveErrorMessages(t);
    }

    private static String retrieveErrorMessages(Throwable t) {
        int depth = 20;
        StringBuilder buffer = new StringBuilder();
        for (Throwable error = t; error != null && (depth--) > 0; error = error.getCause()) {
            addMessage(buffer, error.getMessage());
            if (error instanceof SQLException) {
                SQLException sqle = (SQLException) error;
                while ((sqle = sqle.getNextException()) != null) {
                    addMessage(buffer, sqle.getMessage());
                }
            }
        }
        String message = buffer.toString();
        logger.error("ErrorUtils: " + message, t);
        return (t != null && message.trim().isEmpty()) ? t.toString() : message;
    }

    private static void addMessage(StringBuilder buffer, String message) {
        if (message != null && !message.isEmpty()) {
            if (!buffer.toString().contains(message)) {
                buffer.append(message);
                if (!message.trim().endsWith(".")) {
                    buffer.append(".");
                }
                buffer.append(" ");
            }
        }
    }

}