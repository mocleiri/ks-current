package org.kuali.student.ap.coursesearch.dataobject;

import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chmaurer
 * Date: 6/6/14
 * Time: 8:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlannedRegGroupDetailsWrapper {

    private String regGroupCode;

    public List<ActivityOfferingDetailsWrapper> getActivityOfferingDetailsWrappers() {
        return activityOfferingDetailsWrappers;
    }

    public void setActivityOfferingDetailsWrappers(
            List<ActivityOfferingDetailsWrapper> activityOfferingDetailsWrappers) {
        this.activityOfferingDetailsWrappers = activityOfferingDetailsWrappers;
    }

    private List<ActivityOfferingDetailsWrapper> activityOfferingDetailsWrappers;

    public String getCourseOfferingCode() {
        return regGroupCode;
    }

    public void setCourseOfferingCode(String courseOfferingCode) {
        this.regGroupCode = courseOfferingCode;
    }

}
