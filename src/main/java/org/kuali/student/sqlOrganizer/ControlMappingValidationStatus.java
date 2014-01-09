package org.kuali.student.sqlOrganizer;

/**
 * see: ControlMappingUtils
 */
public enum ControlMappingValidationStatus {
    VALID("Control properties read succesfully\n"),
    INVALID_KEY("Encoded filename key does not match file name\n"),
    INVALID_TYPE_KEY("Encoded type key does not match type\n"),
    MISSING_KEY("Missing KEY property\n"),
    MISSING_TYPE("Missing TYPE property\n"),
    FORMAT_ERROR("Illegal format of control properties\n" +
            "Acceptable format:\n\n" +
            "-- KEY1:<encoded filename>\n" +
            "-- KEY2:<encoded type>\n" +
            "-- TYPE:<REFERENCE,BOOTSTRAP,MANUAL,EXCEPTION,MIGRATION>"),
    INVALID_PROPERTY("Unknown property encountered in control properties\n"),
    TYPE_NOT_FOUND("Invalid type in control properties\n"),
    IOEXCEPTION("IOEXCEPTION reading control properties\n");
    private final String stringVal;

    private ControlMappingValidationStatus(String stringVal) {
        this.stringVal = stringVal;
    }

    public String toString() {
        return stringVal;
    }
}
