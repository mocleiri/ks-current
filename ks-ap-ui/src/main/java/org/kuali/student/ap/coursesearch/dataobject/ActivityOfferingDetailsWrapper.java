package org.kuali.student.ap.coursesearch.dataobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;

/**
 * Created with IntelliJ IDEA.
 * User: chmaurer
 * Date: 6/9/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActivityOfferingDetailsWrapper {
    private String activityOfferingId;
    private String activityOfferingCode;
    //From Bonnie: need to investigate how to handle multiple instructors vs. single instructor
    private String instructorName;
    private String firstInstructorDisplayName;
    private String instructorDisplayNames;

    private String days;
    private String time;
    private String location;
    private int currentEnrollment;
    private int maxEnrollment;
    private boolean honors;
    private String classUrl;
    private String requirementsUrl;
    private boolean selected;
    private String regGroupCode;
    private String activityFormatType;
    private boolean partOfRegGroup = false;

    public ActivityOfferingDetailsWrapper() {
    }

    public ActivityOfferingDetailsWrapper(ActivityOfferingInfo activityOffering, boolean partOfRegGroup) {
        this.activityOfferingId = activityOffering.getId();
        this.activityOfferingCode = activityOffering.getActivityCode();
//        this.days = activityOffering.get
        this.maxEnrollment = activityOffering.getMaximumEnrollment();
        this.honors = activityOffering.getIsHonorsOffering();
        this.partOfRegGroup = partOfRegGroup;

    }

    public String getActivityFormatType() {
        return activityFormatType;
    }

    public void setActivityFormatType(String activityFormatType) {
        this.activityFormatType = activityFormatType;
    }

    public String getActivityOfferingId() {
        return activityOfferingId;
    }

    public void setActivityOfferingId(String activityOfferingId) {
        this.activityOfferingId = activityOfferingId;
    }

    public String getActivityOfferingCode() {
        return activityOfferingCode;
    }

    public void setActivityOfferingCode(String activityOfferingCode) {
        this.activityOfferingCode = activityOfferingCode;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getFirstInstructorDisplayName() {
        return firstInstructorDisplayName;
    }

    public void setFirstInstructorDisplayName(String firstInstructorDisplayName) {
        this.firstInstructorDisplayName = firstInstructorDisplayName;
    }

    public String getInstructorDisplayNames() {
        return instructorDisplayNames;
    }

    public void setInstructorDisplayNames(String instructorDisplayNames) {
        this.instructorDisplayNames = instructorDisplayNames;
    }

    public void setInstructorDisplayNames(String instructorDisplayNames, boolean appendForDisplay) {
        if (appendForDisplay && this.instructorDisplayNames != null) {
            this.instructorDisplayNames = this.instructorDisplayNames + "<br>" + StringUtils.defaultString(instructorDisplayNames);
        } else {
            this.instructorDisplayNames = StringUtils.defaultString(instructorDisplayNames);
        }

    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public void setMaxEnrollment(int maxEnrollment) {
        this.maxEnrollment = maxEnrollment;
    }

    public boolean isHonors() {
        return honors;
    }

    public void setHonors(boolean honors) {
        this.honors = honors;
    }

    public String getClassUrl() {
        return classUrl;
    }

    public void setClassUrl(String classUrl) {
        this.classUrl = classUrl;
    }

    public String getRequirementsUrl() {
        return requirementsUrl;
    }

    public void setRequirementsUrl(String requirementsUrl) {
        this.requirementsUrl = requirementsUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRegGroupCode() {
        return regGroupCode;
    }

    public void setRegGroupCode(String regGroupCode) {
        this.regGroupCode = regGroupCode;
    }

    public boolean isPartOfRegGroup() {
        return partOfRegGroup;
    }

    public void setPartOfRegGroup(boolean partOfRegGroup) {
        this.partOfRegGroup = partOfRegGroup;
    }
}
