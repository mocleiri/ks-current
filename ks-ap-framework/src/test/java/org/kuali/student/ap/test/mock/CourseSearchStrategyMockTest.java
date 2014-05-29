package org.kuali.student.ap.test.mock;

import org.kuali.student.ap.coursesearch.CourseSearchForm;
import org.kuali.student.ap.coursesearch.CourseSearchItem;
import org.kuali.student.ap.coursesearch.CourseSearchStrategy;
import org.kuali.student.ap.coursesearch.Credit;
import org.kuali.student.ap.coursesearch.Hit;
import org.kuali.student.ap.coursesearch.QueryTokenizer;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: johglove
 * Date: 11/19/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CourseSearchStrategyMockTest implements CourseSearchStrategy {
    /**
     * Create a new instance of the course search form.
     *
     * @return A new instance of the course search form.
     */
    @Override
    public CourseSearchForm createInitialSearchForm() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Creates formatted ordered list of sql search requests objects from data passed in through the search form.
     *
     * @param form - Form containing search parameters and query
     * @return A formatted and order list of requests.
     */
    @Override
    public List<SearchRequestInfo> buildSearchRequests(CourseSearchForm form) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Process the Request adding any additional values, additional checks, or additional valiadations
     *
     * @param requests - The list of requests.
     * @param form     - The search form.
     * @return A formatted list of requests.
     */
    @Override
    public List<SearchRequestInfo> adjustSearchRequests(List<SearchRequestInfo> requests, CourseSearchForm form) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Preforms sql queries on a list of requests and returns the results in the form of Hits.
     *
     * @param requests - List of sql requests to search on.
     * @return A list of results along with how many times each was found.
     */
    @Override
    public List<Hit> preformSearch(List<SearchRequestInfo> requests) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Determines a list of terms to filter results on based on a filter value
     *
     * @param termFilter - A value that determines what terms to filter on.
     * @return A list of term ids.
     */
    @Override
    public List<String> getTermsToFilterOn(String termFilter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Add searches based on the components found in the query string
     *
     * @param componentMap - A map of the different components to make requests on
     * @param requests     - The list of search requests to be ran
     */
    @Override
    public void addComponentRequests(Map<String, List<String>> componentMap, List<SearchRequestInfo> requests) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Add searches based on text tokens found in the query string
     * Tokens are used in search of the course description and title text.
     *
     * @param query      - The query string
     * @param requests   - The list of search requests to be ran
     * @param searchTerm - The term filter for the search (used for CO searches)
     */
    @Override
    public void addFullTextRequests(String query, List<SearchRequestInfo> requests, String searchTerm) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> getCurriculumMap(Set<String> orgIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> getGenEdMap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Credit> getCreditMap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void populateFacets(CourseSearchForm form, List<CourseSearchItem> courses) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<CourseSearchItem> courseSearch(CourseSearchForm form, String studentId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Comparator<String>> getFacetSort() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isLimitExceeded() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryTokenizer getQueryTokenizer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
