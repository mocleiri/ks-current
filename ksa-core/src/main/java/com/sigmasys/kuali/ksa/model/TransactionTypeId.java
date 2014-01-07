package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The composite primary key for TransactionType entity
 *
 * @author Michael Ivanov
 */
@Embeddable
public class TransactionTypeId implements Serializable {

    private String id;
    private int subCode;


    public TransactionTypeId() {
    }

    public TransactionTypeId(String id, Integer subCode) {
        setId(id);
        setSubCode(subCode);
    }

    @Column(name = "ID", length = 20)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "SUB_CODE")
    public int getSubCode() {
        return subCode;
    }

    public void setSubCode(int subCode) {
        this.subCode = subCode;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        TransactionTypeId transactionTypeId = (TransactionTypeId) object;

        return (subCode == transactionTypeId.subCode) &&
                !(id != null ? !id.equals(transactionTypeId.id) : transactionTypeId.id != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + subCode;
        return result;
    }

    @Override
    public String toString() {
        return "TransactionTypeId{id='" + getId() + ", subCode=" + getSubCode() + '}';
    }
}
