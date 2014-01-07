package com.sigmasys.kuali.ksa.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The composite primary key for TransactionType entity
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("serial")
@Embeddable
public class TransactionTypeId implements Serializable {

    private String id;
    private Integer subCode;


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
    public Integer getSubCode() {
        return subCode;
    }

    public void setSubCode(Integer subCode) {
        this.subCode = subCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((subCode == null) ? 0 : subCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        TransactionTypeId other = (TransactionTypeId) obj;

        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }

        if (getSubCode() == null) {
            if (other.getSubCode() != null) {
                return false;
            }
        } else if (!getSubCode().equals(other.getSubCode())) {
            return false;
        }

        return true;
    }

}
