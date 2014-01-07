package com.sigmasys.kuali.ksa.model.fm;

import com.sigmasys.kuali.ksa.model.Identifiable;

import javax.persistence.*;

/**
 * Rate Catalog ATP model.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Entity
@Table(name = "KSSA_RATE_CATALOG_ATP")
public class RateCatalogAtp implements Identifiable {


    private RateCatalogAtpId id;

    private RateCatalog rateCatalog;


    @Override
    @EmbeddedId
    public RateCatalogAtpId getId() {
         return id;
    }

    public void setId(RateCatalogAtpId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_CATALOG_ID_FK")
    public RateCatalog getRateCatalog() {
        return rateCatalog;
    }

    public void setRateCatalog(RateCatalog rateCatalog) {
        this.rateCatalog = rateCatalog;
    }
}
