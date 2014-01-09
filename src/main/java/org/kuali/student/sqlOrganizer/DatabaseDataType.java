package org.kuali.student.sqlOrganizer;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DatabaseDataType {
    STRUCTURE("STRUCTURE"),
    BOOTSTRAP("BOOTSTRAP"),
    MANUAL("MANUAL"),
    REFERENCE("REFERENCE"),
    EXCEPTION("EXCEPTION"),
    MIGRATION("MIGRATION");

    private final String stringVal;

    private DatabaseDataType(String stringVal) {
        this.stringVal = stringVal;
    }

    public String toString() {
        return stringVal;
    }

    public static DatabaseDataType getDatabaseDataType (String dataType) {
        for (DatabaseDataType dataTypeIter : DatabaseDataType.values()) {
            if (dataTypeIter.toString().equals(dataType)) {
                return dataTypeIter;
            }
        }
        return null;
    }
}
