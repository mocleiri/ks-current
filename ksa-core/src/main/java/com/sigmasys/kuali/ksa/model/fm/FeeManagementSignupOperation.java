package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * FeeManagementSignupOperation status values.
 *
 * @author Michael Ivanov
 */
public enum FeeManagementSignupOperation implements Identifiable {

    ADD(FeeManagementSignupOperation.ADD_CODE),
    DROP(FeeManagementSignupOperation.DROP_CODE),
    WITHDRAW(FeeManagementSignupOperation.WITHDRAW_CODE),
    ADD_WITHOUT_PENALTY(FeeManagementSignupOperation.ADD_WITHOUT_PENALTY_CODE),
    DROP_WITHOUT_PENALTY(FeeManagementSignupOperation.DROP_WITHOUT_PENALTY_CODE),
    TRANSFER_IN(FeeManagementSignupOperation.TRANSFER_IN_CODE),
    TRANSFER_OUT(FeeManagementSignupOperation.TRANSFER_OUT_CODE);

    public static final String ADD_CODE = "A";
    public static final String DROP_CODE = "D";
    public static final String WITHDRAW_CODE = "W";
    public static final String ADD_WITHOUT_PENALTY_CODE = "AP";
    public static final String DROP_WITHOUT_PENALTY_CODE = "DP";
    public static final String TRANSFER_IN_CODE = "TI";
    public static final String TRANSFER_OUT_CODE = "TO";


    private String id;

    private FeeManagementSignupOperation(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case ADD:
                return "Add";
            case DROP:
                return "Drop";
            case WITHDRAW:
                return "Withdraw";
            case ADD_WITHOUT_PENALTY:
                return "Add without penalty";
            case DROP_WITHOUT_PENALTY:
                return "Drop without penalty";
            case TRANSFER_IN:
                return "Transfer in";
            case TRANSFER_OUT:
                return "Transfer out";
        }
        throw new IllegalStateException("No Fee Management Signup operation found for " + name() + " value");
    }
}
