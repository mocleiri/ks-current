package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Offering types.
 *
 * @author Sergey Godunov
 */
public enum OfferingType implements Identifiable {

    PROGRAM_OFFERING(OfferingType.PROGRAM_OFFERING_CODE),
    ACTIVITY_OFFERING(OfferingType.ACTIVITY_OFFERING_CODE);

    public static final String PROGRAM_OFFERING_CODE = "PO";
    public static final String ACTIVITY_OFFERING_CODE = "AO";

    private String id;

    private OfferingType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String toString() {
        switch (this) {
            case PROGRAM_OFFERING:
                return "Program Offering";
            case ACTIVITY_OFFERING:
                return "Activity Offering";
        }
        throw new IllegalStateException("No OfferingType found for " + name() + " value.");
    }
}
