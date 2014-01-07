package com.sigmasys.kuali.ksa.model;

/**
 * General ledger mode.
 *
 * @author Michael Ivanov
 */
public enum GeneralLedgerMode implements Identifiable {

    INDIVIDUAL(GeneralLedgerMode.INDIVIDUAL_CODE),
    BATCH(GeneralLedgerMode.BATCH_CODE),
    BATCH_ROLLUP(GeneralLedgerMode.BATCH_ROLLUP_CODE);

    public static final String INDIVIDUAL_CODE = "Individual";
    public static final String BATCH_CODE = "Batch";
    public static final String BATCH_ROLLUP_CODE = "BatchRollup";

    private String id;

    private GeneralLedgerMode(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

}
