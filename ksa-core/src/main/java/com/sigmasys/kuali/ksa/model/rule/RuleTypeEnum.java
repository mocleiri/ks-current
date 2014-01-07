package com.sigmasys.kuali.ksa.model.rule;

import com.sigmasys.kuali.ksa.model.Identifiable;

/**
 * Drools rule type model.
 *
 * @author Michael Ivanov
 */
public enum RuleTypeEnum implements Identifiable {

    DRL(RuleTypeEnum.DRL_CODE),
    XDRL(RuleTypeEnum.XDRL_CODE),
    DSL(RuleTypeEnum.DSL_CODE),
    DSLR(RuleTypeEnum.DSLR_CODE);


    public static final String DRL_CODE = "DRL";
    public static final String XDRL_CODE = "XDRL";
    public static final String DSL_CODE = "DSL";
    public static final String DSLR_CODE = "DSLR";

    private String id;

    private RuleTypeEnum(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        switch (this) {
            case DRL:
                return "Drools Rule Language";
            case XDRL:
                return "Drools XML Rule Language";
            case DSL:
                return "Drools DSL";
            case DSLR:
                return "Drools DSL Rule";
        }
        throw new IllegalStateException("No Rule Type found for " + name() + " value");
    }

}
