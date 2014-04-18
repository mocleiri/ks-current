package org.kuali.student.ap.test.mock;

import org.kuali.student.ap.coursesearch.CourseSearchForm;
import org.kuali.student.ap.coursesearch.CourseSearchItem;
import org.kuali.student.ap.coursesearch.CourseSearchStrategy;
import org.kuali.student.ap.coursesearch.Credit;

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
    public CourseSearchForm createSearchForm() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public Credit getCreditByID(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCourseOffered(CourseSearchForm form, CourseSearchItem course) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
    public Map<String, String> fetchCourseDivisions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String extractDivisions(Map<String, String> divisionMap, String query, List<String> divisions, boolean isSpaceAllowed) {
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
}
