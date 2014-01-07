package com.sigmasys.kuali.ksa.model;


/**
 * GL Transmission status model.
 *
 * @author Michael Ivanov
 */
public enum GlTransmissionStatus implements Identifiable {

    GENERATED(GlTransmissionStatus.GENERATED_CODE),
    TRANSMITTED(GlTransmissionStatus.TRANSMITTED_CODE),
    COMPLETED(GlTransmissionStatus.COMPLETED_CODE),
    FAILED(GlTransmissionStatus.FAILED_CODE);


    public static final String GENERATED_CODE = "G";
    public static final String TRANSMITTED_CODE = "T";
    public static final String COMPLETED_CODE = "C";
    public static final String FAILED_CODE = "F";

    private String id;

    private GlTransmissionStatus(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case GENERATED:
                return "Generated";
            case TRANSMITTED:
                return "Transmitted";
            case COMPLETED:
                return "Completed";
            case FAILED:
                return "Failed";
        }
        throw new IllegalStateException("No Transmission status found for " + name() + " value");
    }


}
