package com.sigmasys.kuali.ksa.model.pb;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * ScheduleType values.
 *
 * @author Michael Ivanov
 */
public enum ScheduleType implements Identifiable {

    NOT_ALLOWED(ScheduleType.NOT_ALLOWED_CODE),
    BACK_BILLED(ScheduleType.BACK_BILLED_CODE),
    SKIP_EARLIER(ScheduleType.SKIP_EARLIER_CODE),
    BALANCE_TODAY(ScheduleType.BALANCE_TODAY_CODE);


    public static final String NOT_ALLOWED_CODE = "N";
    public static final String BACK_BILLED_CODE = "B";
    public static final String SKIP_EARLIER_CODE = "S";
    public static final String BALANCE_TODAY_CODE = "D";


    private String id;

    private ScheduleType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case NOT_ALLOWED:
                return "Not Allowed";
            case BACK_BILLED:
                return "Back-billed";
            case SKIP_EARLIER:
                return "Skip Earlier";
            case BALANCE_TODAY:
                return "Due Today";
        }
        throw new IllegalStateException("No late start type found for " + name() + " value");
    }
}
