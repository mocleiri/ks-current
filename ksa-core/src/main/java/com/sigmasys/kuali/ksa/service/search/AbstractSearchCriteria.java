package com.sigmasys.kuali.ksa.service.search;


import java.io.Serializable;

/**
 * Abstract Search Criteria.
 *
 * @author Michael Ivanov
 */
public abstract class AbstractSearchCriteria implements Serializable {

    protected String name;

    // The following flag indicates whether it is necessary to clean the cached search results
    protected boolean expireCache;

    protected boolean useCache = true;

    protected boolean runCount = true;

    // parameter to perform paging, counting on limited number of rows
    // it differs from limit (which is by the way probably should be moved to
    // search query as well)
    protected int maxRows;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows > 0 ? maxRows : 0;
    }

    public boolean isUnlimitedRows() {
        return maxRows == 0;
    }

    public boolean isExpireCache() {
        return expireCache;
    }

    public void setExpireCache(boolean expireCache) {
        this.expireCache = expireCache;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isRunCount() {
        return runCount;
    }

    public void setRunCount(boolean runCount) {
        this.runCount = runCount;
    }

    public <T extends AbstractSearchCriteria> T copyTo(AbstractSearchCriteria sc) {
        sc.setName(name);
        sc.setMaxRows(maxRows);
        sc.setUseCache(useCache);
        sc.setRunCount(runCount);
        sc.setExpireCache(expireCache);
        return (T) sc;
    }

}
