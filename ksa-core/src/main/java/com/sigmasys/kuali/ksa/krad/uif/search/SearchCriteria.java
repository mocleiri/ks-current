package com.sigmasys.kuali.ksa.model.search;

import java.io.Serializable;

/**
 * This is a base class that is used as a search criteria in the search of JPA entities.
 *
 * @author Michael Ivanov
 *         Date: 4/8/12
 *         Time: 10:46 PM
 */
public abstract class SearchCriteria implements Serializable {

    public static final int UNLIMITED_ITEMS_NUMBER = -1;

    private int offset = 0;
    private int limit = UNLIMITED_ITEMS_NUMBER;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
