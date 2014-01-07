package com.sigmasys.kuali.ksa.model.fm;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The composite primary key for RateCatalogAtp entity
 *
 * @author Michael Ivanov
 */
@Embeddable
public class RateCatalogAtpId implements Serializable {

    private String code;
    private String atpId;


    public RateCatalogAtpId() {
    }

    public RateCatalogAtpId(String code, String atpId) {
        setCode(code);
        setAtpId(atpId);
    }

    @Column(name = "RATE_CATALOG_CODE", length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "ATP_ID", length = 45)
    public String getAtpId() {
        return atpId;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getAtpId() == null) ? 0 : getAtpId().hashCode());
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

        RateCatalogAtpId other = (RateCatalogAtpId) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }

        if (getAtpId() == null) {
            if (other.getAtpId() != null) {
                return false;
            }
        } else if (!getAtpId().equals(other.getAtpId())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "RateCatalogAtpId{id='" + getCode() + '\'' + ", atpId=" + getAtpId() + '}';
    }
}
