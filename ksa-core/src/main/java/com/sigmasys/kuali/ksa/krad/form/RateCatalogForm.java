package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.RateCatalogModel;
import com.sigmasys.kuali.ksa.krad.util.IdentifiableKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.fm.TransactionDateType;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * The form object behind the "Rate Catalog" screen.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:43 PM
 */
public class RateCatalogForm extends AbstractViewModel {


    private KeyValuesFinder rateTypeOptionsFinder;

    private String rateTypeId;

    private List<RateCatalogModel> rateCatalogs;

    public String getRateTypeId() {
        return rateTypeId;
    }

    public void setRateTypeId(String rateTypeId) {
        this.rateTypeId = rateTypeId;
    }

    public KeyValuesFinder getRateTypeOptionsFinder() {
        return rateTypeOptionsFinder;
    }

    public void setRateTypeOptionsFinder(KeyValuesFinder rateTypeOptionsFinder) {
        this.rateTypeOptionsFinder = rateTypeOptionsFinder;
    }

    public List<RateCatalogModel> getRateCatalogs() {
        if(rateCatalogs == null) {
            rateCatalogs = new ArrayList<RateCatalogModel>();
        }
        return rateCatalogs;
    }

    public void setRateCatalogs(List<RateCatalogModel> rateCatalogs) {
        this.rateCatalogs = rateCatalogs;
    }

    public KeyValuesFinder getTransactionDateTypeFinder() {
        return new IdentifiableKeyValuesFinder<TransactionDateType>(TransactionDateType.class);
    }

}
