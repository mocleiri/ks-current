package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.sigmasys.kuali.ksa.model.Pair;
import com.sigmasys.kuali.ksa.model.UserPreference;

/**
 * Account Search information holder.
 *
 * @author Sergey Godunov
 */
@SuppressWarnings("serial")
public class AccountSearchInformationHolder implements Serializable {

    private UserPreference userPreference;
    private Pair<Date, Date> dobDateRange;
    private Pair<Date, Date> creationDateRange;
    private Pair<Date, Date> lastUpdateDateRange;
    private Boolean lastNameSubstringSearch;
    private List<String> searchResultFields;


    public Pair<Date, Date> getDobDateRange() {
        return dobDateRange;
    }

    public void setDobDateRange(Pair<Date, Date> dobDateRange) {
        this.dobDateRange = dobDateRange;
    }

    public Pair<Date, Date> getCreationDateRange() {
        return creationDateRange;
    }

    public void setCreationDateRange(Pair<Date, Date> creationDateRange) {
        this.creationDateRange = creationDateRange;
    }

    public Pair<Date, Date> getLastUpdateDateRange() {
        return lastUpdateDateRange;
    }

    public void setLastUpdateDateRange(Pair<Date, Date> lastUpdateDateRange) {
        this.lastUpdateDateRange = lastUpdateDateRange;
    }

    public UserPreference getUserPreference() {
        return userPreference;
    }

    public void setUserPreference(UserPreference userPreference) {
        this.userPreference = userPreference;
    }

    public Boolean getLastNameSubstringSearch() {
        return lastNameSubstringSearch;
    }

    public void setLastNameSubstringSearch(Boolean lastNameSubstringSearch) {
        this.lastNameSubstringSearch = lastNameSubstringSearch;
    }

    public List<String> getSearchResultFields() {
        return searchResultFields;
    }

    public void setSearchResultFields(List<String> searchResultFields) {
        this.searchResultFields = searchResultFields;
    }
}