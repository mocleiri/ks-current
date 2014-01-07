package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.GeneralLedgerType;
import com.sigmasys.kuali.ksa.model.GlOperationType;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 4/20/13
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralLedgerAccountModel implements Serializable {

    /**
     * ID of a GL Account.
     */
    private String glAccountId;

    /**
     * Human-readable name of the account. Some accounts don't have names, so ID becomes the name.
     */
    private String name;

    /**
     * Account operation, Debit or Credit.
     */
    private GlOperationType operationType;

    /**
     * General Ledger Type.
     */
    private GeneralLedgerType glType;

    /**
     * Total amount of the account.
     */
    private BigDecimal totalAmount;

    /**
     * Associated General Ledger Transactions.
     */
    private List<GeneralLedgerTransactionModel> glTransactions;


    /**
     * Compares this object to another Object.
     * Two GeneralLedgerAccountModel objects are considered equal when they have the same
     * GL Account ID and Operation type.
     *
     * @param o An object to compare to.
     * @return boolean Comparison result.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof GeneralLedgerAccountModel) {
            GeneralLedgerAccountModel anotherModel = (GeneralLedgerAccountModel)o;

            return StringUtils.equalsIgnoreCase(glAccountId, anotherModel.getGlAccountId())
                    && (operationType == anotherModel.getOperationType());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31*(31*glAccountId.hashCode() + operationType.hashCode());
    }

    public String getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(String glAccountId) {
        this.glAccountId = glAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GlOperationType getOperationType() {
        return operationType;
    }

    public String getOperationTypeString() {
        return operationType.toString();
    }

    public void setOperationType(GlOperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<GeneralLedgerTransactionModel> getGlTransactions() {
        if (glTransactions == null) {
            glTransactions = new ArrayList<GeneralLedgerTransactionModel>();
        }

        return glTransactions;
    }

    public void setGlTransactions(List<GeneralLedgerTransactionModel> glTransactions) {
        this.glTransactions = glTransactions;
    }

    public GeneralLedgerType getGlType() {
        return glType;
    }

    public void setGlType(GeneralLedgerType glType) {
        this.glType = glType;
    }
}