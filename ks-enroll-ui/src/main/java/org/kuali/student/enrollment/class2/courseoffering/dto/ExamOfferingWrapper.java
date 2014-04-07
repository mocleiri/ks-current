package org.kuali.student.enrollment.class2.courseoffering.dto;

import org.apache.commons.lang.StringUtils;
import org.kuali.student.enrollment.class2.scheduleofclasses.sort.ComparatorModel;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.enrollment.examoffering.dto.ExamOfferingInfo;
import org.kuali.student.r2.core.scheduling.dto.ScheduleRequestSetInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Main model object in Edit AO view.
 *
 * @see org.kuali.student.enrollment.class2.courseoffering.dto.ColocatedActivity
 *
 */
public class ExamOfferingWrapper implements Serializable, ComparatorModel{


    private String stateName;
    private String typeName;
    private String typeKey;
    private String startTimeDisplay = "";
    private String endTimeDisplay = "";
    private String daysDisplayName = "";
    private String buildingName = "";
    private String buildingCode = "";
    private String bldgCodeSimple = "";
    private String roomName = "";
    private ExamOfferingInfo eoInfo;
    private List<String> startTime;
    private List<String> endTime;
    private List<String> weekDays;
    private ActivityOfferingInfo aoInfo;

    private List<ScheduleWrapper> requestedScheduleComponents;
    private List<ScheduleWrapper> deletedRequestedScheduleComponents;
    private ScheduleWrapper requestedSchedule;
    private ScheduleWrapper scheduleRequest;
    private ScheduleRequestSetInfo scheduleRequestSetInfo;
    private boolean driverPerAO;
    private boolean overrideMatrix;
    private String overrideMatrixUI;

    public ExamOfferingWrapper(){
        startTime = new ArrayList<String>();
        endTime = new ArrayList<String>();
        weekDays = new ArrayList<String>();
        aoInfo = new ActivityOfferingInfo();
        requestedScheduleComponents = new ArrayList<ScheduleWrapper>();
        deletedRequestedScheduleComponents = new ArrayList<ScheduleWrapper>();
    }

    public ExamOfferingWrapper(ExamOfferingInfo info){
        this();
        eoInfo = info;
        startTime = new ArrayList<String>();
        endTime = new ArrayList<String>();
        weekDays = new ArrayList<String>();
        requestedScheduleComponents = new ArrayList<ScheduleWrapper>();
        deletedRequestedScheduleComponents = new ArrayList<ScheduleWrapper>();

    }

    public String courseOfferingTitle;
    public void setCourseOfferingTitle(String courseOfferingTitle) {
        this.courseOfferingTitle = courseOfferingTitle;
    }

    public String getCourseOfferingTitle() {
        return courseOfferingTitle;
    }


    private String activityCode = "";

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStartTimeDisplay() {
        return startTimeDisplay;
    }

    public void setStartTimeDisplay(String startTimeDisplay) {
       this.startTimeDisplay = StringUtils.defaultString(startTimeDisplay);
    }

    public String getEndTimeDisplay() {
        return endTimeDisplay;
    }

    public void setEndTimeDisplay(String endTimeDisplay) {
        this.endTimeDisplay = StringUtils.defaultString(endTimeDisplay);
    }

    public String getDaysDisplayName() {
        return daysDisplayName;
    }

    public void setDaysDisplayName(String daysDisplayName) {
        this.daysDisplayName = StringUtils.defaultString(daysDisplayName);
    }

    public List<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }

    public List<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<String> endTime) {
        this.endTime = endTime;
    }

    public List<String> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<String> weekDays) {
        this.weekDays = weekDays;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBldgCodeSimple() {
        return bldgCodeSimple;
    }

    public void setBldgCodeSimple(String bldgCodeSimple) {
        this.bldgCodeSimple = bldgCodeSimple;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        String cssClass = "style=\"border-bottom: 1px dotted;\"";
        this.buildingCode = "<span " + cssClass + " >" + StringUtils.defaultString(buildingCode) + "</span>";
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = StringUtils.defaultString(roomName);
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public ExamOfferingInfo getEoInfo() {
        return eoInfo;
    }

    public void setEoInfo(ExamOfferingInfo eoInfo) {
        this.eoInfo = eoInfo;
    }

    public ActivityOfferingInfo getAoInfo() {
        return aoInfo;
    }

    public void setAoInfo(ActivityOfferingInfo aoInfo) {
        this.aoInfo = aoInfo;
    }

    public List<ScheduleWrapper> getRequestedScheduleComponents() {
        return requestedScheduleComponents;
    }

    public void setRequestedScheduleComponents(List<ScheduleWrapper> requestedScheduleComponents) {
        this.requestedScheduleComponents = requestedScheduleComponents;
    }

    public List<ScheduleWrapper> getDeletedRequestedScheduleComponents() {
        return deletedRequestedScheduleComponents;
    }

    public void setDeletedRequestedScheduleComponents(List<ScheduleWrapper> deletedRequestedScheduleComponents) {
        this.deletedRequestedScheduleComponents = deletedRequestedScheduleComponents;
    }

    public ScheduleWrapper getRequestedSchedule() {
        return requestedSchedule;
    }

    public void setRequestedSchedule(ScheduleWrapper requestedSchedule) {
        this.requestedSchedule = requestedSchedule;
    }

    public ScheduleWrapper getScheduleRequest() {
        return scheduleRequest;
    }

    public void setScheduleRequest(ScheduleWrapper scheduleRequest) {
        this.scheduleRequest = scheduleRequest;
    }

    public ScheduleRequestSetInfo getScheduleRequestSetInfo() {
        return scheduleRequestSetInfo;
    }

    public void setScheduleRequestSetInfo(ScheduleRequestSetInfo scheduleRequestSetInfo) {
        this.scheduleRequestSetInfo = scheduleRequestSetInfo;
    }

    public boolean isDriverPerAO() {
        return driverPerAO;
    }

    public void setDriverPerAO(boolean driverPerAO) {
        this.driverPerAO = driverPerAO;
    }

    public boolean isOverrideMatrix() {
        return overrideMatrix;
    }

    public void setOverrideMatrix(boolean overrideMatrix) {
        this.overrideMatrix = overrideMatrix;
    }

    public String getOverrideMatrixUI() {
        return String.valueOf(isOverrideMatrix());
    }

    public void setOverrideMatrixUI(String overrideMatrixUI) {
        this.overrideMatrixUI = overrideMatrixUI;
    }
}
