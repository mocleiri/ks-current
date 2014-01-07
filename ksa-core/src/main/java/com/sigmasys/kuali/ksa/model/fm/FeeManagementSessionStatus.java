package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * FeeManagementSession status values.
 *
 * @author Michael Ivanov
 */
public enum FeeManagementSessionStatus implements Identifiable {

    SIMULATED(FeeManagementSessionStatus.SIMULATED_CODE),
    RECONCILED(FeeManagementSessionStatus.RECONCILED_CODE),
    SIMULATED_RECONCILED(FeeManagementSessionStatus.SIMULATED_RECONCILED_CODE),
    CURRENT(FeeManagementSessionStatus.CURRENT_CODE),
    CHARGED(FeeManagementSessionStatus.CHARGED_CODE);

    public static final String SIMULATED_CODE = "SI";
    public static final String RECONCILED_CODE = "RE";
    public static final String CURRENT_CODE = "CU";
    public static final String SIMULATED_RECONCILED_CODE = "SR";
    public static final String CHARGED_CODE = "CH";


    private String id;

    private FeeManagementSessionStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case SIMULATED:
                return "Simulated";
            case RECONCILED:
                return "Reconciled";
            case SIMULATED_RECONCILED:
                return "Simulated Reconciled";
            case CURRENT:
                return "Current";
            case CHARGED:
                return "Charged";
        }
        throw new IllegalStateException("No Fee Management Session status found for " + name() + " value");
    }
}
