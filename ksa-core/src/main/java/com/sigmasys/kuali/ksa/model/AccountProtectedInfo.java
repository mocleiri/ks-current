package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;

import javax.persistence.*;

/**
 * Account protected information.
 * <p/>
 * <p/>
 *
 * @author Michael Ivanov
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
     * The actual tax identifier as referenced in TAX_TYPE.
     */
    private String taxReference;

    /**
     * The actual detail as references in BANK_TYPE, for example, the actual IBAN of the account holder.
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
    private IdentityType identityType;

    /**
     * The issuing authority of a document. For a passport, this would often be a country.
     * For a US Driver’s license, it would most often be the state that issued the license.
     */
    private String identityIssuer;

    /**
     * The actual serial number of the document defined by ID_TYPE.
     * For example, if the document is a passport, this would likely be the passport’s number.
     */
    private String identitySerial;


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
    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @Column(name = "ID_ISSUER", length = 100)
    public String getIdentityIssuer() {
        return identityIssuer;
    }

    public void setIdentityIssuer(String identityIssuer) {
        this.identityIssuer = identityIssuer;
    }

    @Column(name = "ID_SERIAL", length = 100)
    public String getIdentitySerial() {
        return identitySerial;
    }

    public void setIdentitySerial(String identitySerial) {
        this.identitySerial = identitySerial;
    }
}
