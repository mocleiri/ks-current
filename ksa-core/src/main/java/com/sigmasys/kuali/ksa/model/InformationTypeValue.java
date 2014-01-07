package com.sigmasys.kuali.ksa.model;

/**
 * Information type value enum.
 * It is used to distinguish different information types in the KSA code and UI forms
 *
 * @author Michael Ivanov
 */
public enum InformationTypeValue {

    ALERT(InformationTypeValue.ALERT_CODE),
    FLAG(InformationTypeValue.FLAG_CODE),
    MEMO(InformationTypeValue.MEMO_CODE);

    public static final String ALERT_CODE = "A";
    public static final String FLAG_CODE = "F";
    public static final String MEMO_CODE = "M";

    private String code;

    private InformationTypeValue(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        switch (this) {
            case ALERT:
                return "Alert";
            case FLAG:
                return "Flag";
            case MEMO:
                return "Memo";
        }
        throw new IllegalStateException("No information type found for " + name() + " value");
    }
}
