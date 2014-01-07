package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;

import javax.persistence.*;

/**
 * Account protected information
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
 *         Date: 1/22/12
 *         Time: 3:47 PM
 */
@Auditable
@Entity
@Table(name = "KSSA_ACNT_PROTECTED_INFO")
public class AccountProtectedInfo implements Identifiable {

    /**
     * AccountInfo ID
     */
    private String id;

    /**
     * Tax reference
     */
    private String taxReference;

    /**
     * Bank details
     */
    private String bankDetails;

    /**
     * Tax type
     */
    private TaxType taxType;

    /**
     * Bank type
     */
    private BankType bankType;

    /**
     * ID type
     */
    private IdType idType;


    @Id
    @Column(name = "ID", nullable = false, updatable = false, length = 45)
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "TAX_REFERENCE", length = 45)
    public String getTaxReference() {
        return taxReference;
    }

    public void setTaxReference(String taxReference) {
        this.taxReference = taxReference;
    }

    @Column(name = "BANK_DETAILS", length = 100)
    public String getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(String bankDetails) {
        this.bankDetails = bankDetails;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_TYPE_ID_FK")
    public TaxType getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_TYPE_ID_FK")
    public BankType getBankType() {
        return bankType;
    }

    public void setBankType(BankType bankType) {
        this.bankType = bankType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TYPE_ID_FK")
    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }
}
