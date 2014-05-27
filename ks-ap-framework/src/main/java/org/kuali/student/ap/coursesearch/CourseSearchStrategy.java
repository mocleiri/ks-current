package org.kuali.student.ap.coursesearch;

import org.kuali.student.r2.core.search.dto.SearchRequestInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strategy interface for specifying course search behavior.
 */
public interface CourseSearchStrategy {

    public static final int MAX_HITS = 1000;

	/**
	 * Create a new instance of the course search form.
	 * 
	 * @return A new instance of the course search form.
	 */
	public CourseSearchForm createInitialSearchForm();

    /**
     * Creates formatted ordered list of sql search requests objects from data passed in through the search form.
     *
     * @param form - Form containing search parameters and query
     * @return A formatted and order list of requests.
     */
    public List<SearchRequestInfo> buildSearchRequests(CourseSearchForm form);

    /**
     * Process the Request adding any additional values, additional checks, or additional valiadations
     *
     * @param requests - The list of requests.
     * @param form     - The search form.
     * @return A formatted list of requests.
     */
    public List<SearchRequestInfo> adjustSearchRequests(List<SearchRequestInfo> requests, CourseSearchForm form);

    /**
     * Preforms sql queries on a list of requests and returns the results in the form of Hits.
     *
     * @param requests - List of sql requests to search on.
     * @return A list of results along with how many times each was found.
     */
    public List<Hit> preformSearch(List<SearchRequestInfo> requests);

    /**
     * Determines a list of terms to filter results on based on a filter value
     *
     * @param termFilter - A value that determines what terms to filter on.
     * @return A list of term ids.
     */
    public List<String> getTermsToFilterOn(String termFilter);

    /**
     * Add searches based on the components found in the query string
     *
     * @param componentMap    - A map of the different components to make requests on
     * @param requests        - The list of search requests to be ran
     */
    public void addComponentRequests(Map<String, List<String>> componentMap, List<SearchRequestInfo> requests);

    Map<String, String> getCurriculumMap(Set<String> orgIds);

    Map<String, String> getGenEdMap();

	Map<String, Credit> getCreditMap();

	Credit getCreditByID(String id);

	boolean isCourseOffered(CourseSearchForm form, CourseSearchItem course);

	void populateFacets(CourseSearchForm form, List<CourseSearchItem> courses);

	List<CourseSearchItem> courseSearch(CourseSearchForm form, String studentId);

	Map<String, String> fetchCourseDivisions();

	String extractDivisions(Map<String, String> divisionMap, String query,
			List<String> divisions, boolean isSpaceAllowed);

	Map<String, Comparator<String>> getFacetSort();

    public boolean isLimitExceeded();

}
