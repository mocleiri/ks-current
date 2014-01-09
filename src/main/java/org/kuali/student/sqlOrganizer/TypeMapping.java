package org.kuali.student.sqlOrganizer;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/30/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeMapping {
    DatabaseDataType type;
    private String typeKey;

    public DatabaseDataType getType() {
        return type;
    }

    public void setType(DatabaseDataType type) {
        this.type = type;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public Boolean isValid() {
        return type.toString().equals(InsecureStringEncoder.decode(typeKey));
    }
}
