package org.kuali.student.sqlOrganizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Control Mappings are headers on .sql files. These headers allow bypassing all of the parsing and logical processing
 * of statements. This is generally done in the case where sql statements are unparseable (e.g. oracle specific) or
 * reference tables of ambiguous type categorization (e.g. RICH TEXT tables which contain some reference and some
 * bootstrap data). These headers contain encoded keys.  The first key is the encoded file name and the second key is
 * the encoded mapping. These encodings meet 2 requirements:
 *
 * 1. We want to prevent developers from casually modifying the headers
 * 2. We want to prevent copy/paste of headers from one file to another
 *
 * We also want non-casual devs to be able to quickly decipher the mapping so we have the mapping unencrypted. The
 * format is as follows:
 *
 * <pre>
 * {@code
 * -- DO NOT COPY AND PASTE THIS COMMENT.  VIOLATORS WILL LOSE COMMIT ACCESS.  CONTROL LEVEL: FILE.
 * -- KEY1:MjAxMy0xMC0xNi1TY2hlZHVsaW5nLVB1cmdlLU9ycGhhbi1SZWYtRGF0YS5zcWw=
 * -- TYPE:REFERENCE
 * -- KEY2:TUlHUkFUSU9O
 * }
 * </pre>
 */

public class ControlMappingUtils {

    public static ControlMappingValidationStatus setControlDataType(String statement, String filename, TypeMapping typeMapping) {
        BufferedReader reader = new BufferedReader(new StringReader(statement));
        Boolean validKey = false;
        Boolean validType = false;
        try {
            // skip first line
            reader.readLine();
            for (int i = 0; i < 3; i++) {
                String comment = reader.readLine();
                String[] kvps = comment.substring(3).split(":");
                if (kvps.length == 2) {
                    if (kvps[0].equals("KEY1")) {
                        if (!validateKey(filename, kvps[1])) {
                            return ControlMappingValidationStatus.INVALID_KEY;
                        } else {
                            validKey = true;
                        }
                    } else if (kvps[0].equals("TYPE")) {
                        for (DatabaseDataType type : DatabaseDataType.values()) {
                            if (type.toString().equals(kvps[1].trim())) {
                                typeMapping.setType(type);
                                validType = true;
                            }
                        }
                        if (typeMapping.getType() == null) {
                            return ControlMappingValidationStatus.TYPE_NOT_FOUND;
                        }
                    } else if (kvps[0].equals("KEY2")) {
                        typeMapping.setTypeKey(kvps[1]);
                    } else {
                        return ControlMappingValidationStatus.INVALID_PROPERTY;
                    }
                } else {
                    return ControlMappingValidationStatus.FORMAT_ERROR;
                }
            }
        } catch (IOException e) {
            typeMapping.setType(DatabaseDataType.EXCEPTION);
            return ControlMappingValidationStatus.IOEXCEPTION;
        }

        if (!validKey) {
            return ControlMappingValidationStatus.MISSING_KEY;
        } else if (!validType) {
            return ControlMappingValidationStatus.MISSING_TYPE;
        } else if (!typeMapping.isValid()) {
            return ControlMappingValidationStatus.INVALID_TYPE_KEY;
        }
        return ControlMappingValidationStatus.VALID;
    }

    private static boolean validateKey(String filename, String encoded) {
        if (filename.equals(InsecureStringEncoder.decode(encoded))) {
            return true;
        } else {
            System.out.println("Control comment likely copied from: " + InsecureStringEncoder.decode(encoded));
        }
        return false;
    }

    public static boolean skipSqlOrganization(String firstLine) {
        return firstLine.trim().startsWith("-- DO NOT COPY AND PASTE THIS COMMENT.  VIOLATORS WILL LOSE COMMIT ACCESS.");
    }

}
