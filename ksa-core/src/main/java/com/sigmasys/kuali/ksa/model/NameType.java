package com.sigmasys.kuali.ksa.model;

/**
 * KSA Name Type enum.
 *
 * @author Michael Ivanov
 */
public enum NameType implements Identifiable {

    PERSON(NameType.PERSON_CODE),
    ORGANIZATION(NameType.ORGANIZATION_CODE);

    public static final String PERSON_CODE = "P";
    public static final String ORGANIZATION_CODE = "O";

    private String id;

    private NameType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case PERSON:
                return "Person";
            case ORGANIZATION:
                return "Organization";
        }
        throw new IllegalStateException("No name type found for " + name() + " value");
    }
}
