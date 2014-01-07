package com.sigmasys.kuali.ksa.model.tp;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Third-party charge status constants.
 *
 * @author Michael Ivanov
 */
public enum ThirdPartyChargeStatus implements Identifiable {

    ACTIVE(ThirdPartyChargeStatus.ACTIVE_CODE),
    REVERSED(ThirdPartyChargeStatus.REVERSED_CODE);

    public static final String ACTIVE_CODE = "A";
    public static final String REVERSED_CODE = "R";

    private String id;

    private ThirdPartyChargeStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case ACTIVE:
                return "Active";
            case REVERSED:
                return "Reversed";
        }
        throw new IllegalStateException("No third party plan status found for " + name() + " value");
    }
}
