package com.sigmasys.kuali.ksa.model;

/**
 * KSA Account type value enum.
 * It is used to distinguish different KSA account types in the KSA code and UI forms
 *
 * @author Michael Ivanov
 */
public enum AccountTypeValue implements Identifiable {

    DIRECT_CHARGE(AccountTypeValue.DIRECT_CHARGE_CODE),
    THIRD_PARTY(AccountTypeValue.THIRD_PARTY_CODE),
    DELEGATE(AccountTypeValue.DELEGATE_CODE),
    COLLECTION_AGENCY(AccountTypeValue.COLLECTION_AGENCY_CODE);


    public static final String DIRECT_CHARGE_CODE = "ACD";
    public static final String THIRD_PARTY_CODE = "ACT";
    public static final String DELEGATE_CODE = "AND";
    public static final String COLLECTION_AGENCY_CODE = "ANC";


    private String id;

    private AccountTypeValue(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case DIRECT_CHARGE:
                return "Direct Charge";
            case THIRD_PARTY:
                return "Third Party";
            case DELEGATE:
                return "Delegate";
            case COLLECTION_AGENCY:
                return "Collection Agency";
        }
        throw new IllegalStateException("No account type found for " + name() + " value");
    }
}
