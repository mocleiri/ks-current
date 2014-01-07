package com.sigmasys.kuali.ksa.service.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Collections;

/**
 * SearchQueryBuilder
 *
 * @author Michael Ivanov
 */
public class SearchQueryBuilder {

    private static final Log logger = LogFactory.getLog(SearchQueryBuilder.class);

    protected String selectClause;
    protected String fromClause;
    protected String whereClause;
    protected String outerWhereClause;

    protected List<String> ignoredAttributes;
    protected List<String> predicates;

    protected AbstractColumnMapper sortMapping;
    protected String defaultSort = "";

    protected boolean useFetch = true;
    protected boolean useNativeSql = false;

    // to customize count string, e.g. count(*) or count(distinct column) etc
    private String countString;

    public SearchQueryBuilder(String selectClause) {
        this(selectClause, null);
    }

    public SearchQueryBuilder(String selectClause, String fromClause) {
        this(selectClause, fromClause, null);
    }

    /**
     * SearchQueryBuilder Constructor
     *
     * @param selectClause includes "select "
     * @param fromClause   includes "from "
     * @param whereClause  optional, does not include "where "
     */
    public SearchQueryBuilder(String selectClause, String fromClause, String whereClause) {
        this(selectClause, fromClause, whereClause, null);
    }

    public SearchQueryBuilder(String selectClause, String fromClause, String whereClause, String outerWhereClause) {
        this.selectClause = selectClause;
        this.fromClause = fromClause;
        this.whereClause = whereClause;
        this.outerWhereClause = outerWhereClause;
        ignoredAttributes = new ArrayList<String>();
        predicates = new ArrayList<String>();
    }

    protected SearchQueryBuilder copyTo(SearchQueryBuilder builder) {
        builder.setDefaultSort(getDefaultSort());
        builder.setSortMapping(getSortMapping());
        builder.setUseFetch(isUseFetch());
        builder.setUseNativeSql(isUseNativeSql());
        builder.setIgnoredAttributes(getIgnoredAttributes());
        builder.setPredicates(getPredicates());
        builder.setCountString(getCountString());
        return builder;
    }

    public SearchQueryBuilder copy() {
        return copyTo(new SearchQueryBuilder(getSelectClause(), getFromClause(), getWhereClause(), outerWhereClause));
    }

    public List<String> getIgnoredAttributes() {
        return ignoredAttributes;
    }

    public List<String> getPredicates() {
        return predicates;
    }

    public void setIgnoredAttributes(List<String> ignoredAttributes) {
        this.ignoredAttributes = ignoredAttributes;
    }

    public void setPredicates(List<String> predicates) {
        this.predicates = predicates;
    }

    public String buildCountQuery(SearchCriteria searchCriteria) {
        return buildQuery(searchCriteria, null, null, true);
    }

    public String buildQuery(SearchCriteria searchCriteria) {
        return buildQuery(searchCriteria, null, null);
    }

    public String buildQuery(SearchCriteria searchCriteria, String sortDir, String sortField) {
        return buildQuery(searchCriteria, sortDir, sortField, false);
    }

    protected String buildQuery(SearchCriteria searchCriteria, String sortDir, String sortField, boolean isCountQuery) {

        StringBuilder sb = new StringBuilder();

        if (isCountQuery) {
            sb.append("select " + getCountString() + " \n");
        } else {
            sb.append(selectClause + " \n");
        }

        // remove 'fetch' from fromClause
        String modifiedFromClause = getFromClause();
        if (!isUseNativeSql() && (isCountQuery || !isUseFetch())) {
            modifiedFromClause = modifiedFromClause.replaceAll(" fetch ", " ");
        }
        sb.append(modifiedFromClause + " \n");

        String where = buildWhereClause(searchCriteria);
        sb.append(where);

        // outer where clause. e.g. for ranking
        if (isUseNativeSql() && outerWhereClause != null) {
            sb.append("\n").append(outerWhereClause).append("\n");
        }

        // no need to deal with more than searchCriteria.maxRows number of records
        if (!searchCriteria.isUnlimitedRows()) {
            if (where.trim().length() == 0) {
                sb.append("where ");
            } else {
                sb.append("and ");
            }
            sb.append("rownum <= " + searchCriteria.getMaxRows() + " \n");
        }

        if (!isCountQuery && (isUseFetch() || isUseNativeSql())) {
            sb.append(buildOrderByClause(sortDir, sortField));
        }

        return sb.toString();
    }

    public String buildWhereClause(SearchCriteria searchCriteria) {
        return buildWhereClause(searchCriteria, true);
    }

    public String buildWhereClause(SearchCriteria searchCriteria, boolean addWhere) {

        List<String> where = new LinkedList<String>();

        // where predicate(s) from constructor
        if (whereClause != null && !whereClause.equals("")) {
            where.add(whereClause);
        }

        // where predicates from mappings
        if (searchCriteria != null) {
            for (Map.Entry<String, Serializable> entry : searchCriteria.entrySet()) {
                String attribute = entry.getKey();
                if (ignoredAttributes.contains(attribute)) {
                    continue;
                }
                Object value = entry.getValue();
                String autoPredicate = buildPredicateFromAttribute(attribute, value);
                if (autoPredicate != null && !autoPredicate.equals("")) {
                    where.add(autoPredicate);
                }
            }
        }

        // where predicates from manual HQL
        for (String manualPredicate : predicates) {
            if (manualPredicate != null && manualPredicate.trim().length() > 0) {
                where.add(manualPredicate);
            }
        }

        if (where.isEmpty()) {
            return "";
        }

        // Sorting the predicates in alphabetical order for use in caching as a
        // cache key
        Collections.sort(where);

        // Building where clause
        StringBuilder b = new StringBuilder();
        boolean firstPredicate = true;
        for (String w : where) {
            if (w == null || w.trim().length() == 0) {
                continue;
            }
            if (firstPredicate) {
                if (addWhere) {
                    b.append("where ");
                }
                firstPredicate = false;
            } else {
                b.append("and ");
            }
            b.append("(");
            b.append(w);
            b.append(") \n");
        }

        return b.toString();
    }

    protected String buildPredicateFromAttribute(String attribute, Object value) {
        String hqlExpression;
        hqlExpression = (getSortMapping() != null) ? getSortMapping().getFieldName(attribute) : attribute;
        if (hqlExpression == null) {
            return "";
        }
        if (value instanceof Collection<?>) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Object subValue : (Collection<?>) value) {
                String subHql = buildPredicateFromHqlExpression(hqlExpression, subValue);
                if (subHql != null && !subHql.isEmpty()) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append("or ");
                    }
                    sb.append(subHql);
                }
            }
            return sb.toString();
        }
        return buildPredicateFromHqlExpression(hqlExpression, value);
    }

    /**
     * All different types should be handled here.
     *
     * @param hqlExpression
     * @param value
     * @return
     */
    protected String buildPredicateFromHqlExpression(String hqlExpression, Object value) {
        if (value == null) {
            return "";
        }
        // INFO all different types go here.
        if (value instanceof String) {
            return "upper(" + hqlExpression + ") = upper('" + value + "')";
        } else if (value instanceof Number) {
            return hqlExpression + " = " + value.toString();
        } else if (value instanceof Collection<?>) {

            Collection<?> values = (Collection<?>) value;
            if (values.isEmpty()) {
                return "";
            }

            boolean canBeNull = false;
            // Getting non-null values
            ArrayList<Object> items = new ArrayList<Object>();
            for (Object object : values) {
                if (object != null) {
                    items.add(object);
                } else if (!canBeNull) {
                    canBeNull = true;
                }
            }
            StringBuilder result = new StringBuilder("(");
            if (!items.isEmpty()) {
                result.append(hqlExpression);
                result.append(" in (");
            }
            int i = 0;
            for (Object item : items) {
                if (item instanceof String) {
                    result.append("'").append(item.toString()).append("'");
                } else {
                    result.append(item.toString());
                }
                result.append(++i < items.size() ? "," : ")");
            }
            if (canBeNull) {
                if (i > 0) {
                    result.append(" or ");
                }
                result.append(hqlExpression);
                result.append(" is null");
            }
            result.append(")");
            return result.toString();

        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date theDate = (Date) value;
            String dateString = dateFormat.format(theDate);
            return hqlExpression + " is not null and trunc(" + hqlExpression + ", 'DD') = to_date('" + dateString + "', 'mm/dd/yyyy') ";
        } else {
            // default - empty value
            return "";
        }
    }

    protected String buildOrderByClause(String sortDir, String sortField) {

        List<String> orderByEntries = new LinkedList<String>();

        String orderByPrefix = "order by ";
        String relevantOrderBy = buildOrderByClauseInternal(sortDir, sortField);
        if (!relevantOrderBy.equals("")) {
            orderByEntries.add(relevantOrderBy);
        }
        if (getDefaultSort() != null && !getDefaultSort().trim().isEmpty()) {
            orderByEntries.add(getDefaultSort());
        }

        StringBuilder sb = new StringBuilder();
        if (!orderByEntries.isEmpty()) {
            sb.append(orderByPrefix);
        }
        boolean first = true;
        for (String entry : orderByEntries) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(entry);
        }
        return sb.toString();
    }

    public boolean isSorted(String sortDir, String sortField) {
        return !("NONE".equals(sortDir) || sortDir == null || sortField == null);
    }

    protected String buildOrderByClauseInternal(String sortDir, String sortField) {

        if (!isSorted(sortDir, sortField)) {
            return "";
        }

        String dbField = (getSortMapping() != null) ? getSortMapping().getQueryColumn(sortField) : null;
        if (dbField == null) {
            String message = "Unexpected remote sort field: " + sortField;
            logger.warn(message);
            // do not throw any exceptions.
            return "";
        }

        return dbField + " " + sortDir;
    }

    /**
     * Adds HQL complaint predicate.
     *
     * @param predicate predicate of "where" clause
     */
    public void addPredicate(String predicate) {
        this.predicates.add(predicate);
    }

    /**
     * Indicates that SearchQueryBuilder should not generate predicate for
     * attribute <i>attribute</i> from SearchQuery. In general it is not
     * recommended to modify SearchQuery sent from client. If some SearchQuery
     * keys (each key represents search query attribute) require special
     * handling and you generated HQL predicate manually, then for each such
     * case call addPredicate(customPredicate) and ignoreAttribute(attribute).
     *
     * @param attribute attribute for which the predicate should not be generated
     */
    public void ignoreAttribute(String attribute) {
        this.ignoredAttributes.add(attribute);
    }

    /**
     * @return the sortMapping
     */
    public AbstractColumnMapper getSortMapping() {
        return sortMapping;
    }

    /**
     * @param sortMapping the sortMapping to set
     */
    public void setSortMapping(AbstractColumnMapper sortMapping) {
        this.sortMapping = sortMapping;
    }

    /**
     * @return the defaultSort
     */
    public String getDefaultSort() {
        return defaultSort;
    }

    /**
     * @param defaultSort the defaultSort to set
     */
    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }

    /**
     * @return the useFetch
     */
    public boolean isUseFetch() {
        return useFetch;
    }

    /**
     * @param useFetch the useFetch to set
     */
    public void setUseFetch(boolean useFetch) {
        this.useFetch = useFetch;
    }

    /**
     * @return the selectClause
     */
    public String getSelectClause() {
        return selectClause;
    }

    /**
     * @param selectClause the selectClause to set
     */
    public void setSelectClause(String selectClause) {
        this.selectClause = selectClause;
    }

    /**
     * @return the fromClause
     */
    public String getFromClause() {
        return fromClause;
    }

    /**
     * @param fromClause the fromClause to set
     */
    public void setFromClause(String fromClause) {
        this.fromClause = fromClause;
    }

    /**
     * @return the whereClause
     */
    public String getWhereClause() {
        return whereClause;
    }

    /**
     * @param whereClause the whereClause to set
     */
    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public String getCountString() {
        return countString != null && countString.length() > 0 ? countString : "count(*)";
    }

    public void setCountString(String countString) {
        this.countString = countString;
    }

    public boolean isUseNativeSql() {
        return useNativeSql;
    }

    public void setUseNativeSql(boolean useNativeSql) {
        this.useNativeSql = useNativeSql;
    }
}
