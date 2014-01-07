package org.kuali.student.enrollment.registration.client.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.entity.EntityDefaultQueryResults;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.student.common.collection.KSCollectionUtils;
import org.kuali.student.enrollment.courseoffering.dto.FormatOfferingInfo;
import org.kuali.student.enrollment.courseoffering.service.CourseOfferingService;
import org.kuali.student.enrollment.lpr.dto.LprInfo;
import org.kuali.student.enrollment.lpr.service.LprService;
import org.kuali.student.enrollment.registration.client.service.ScheduleOfClassesService;
import org.kuali.student.enrollment.ui.registration.dto.ActivityOfferingSearchResult;
import org.kuali.student.enrollment.ui.registration.dto.ActivityTypeSearchResult;
import org.kuali.student.enrollment.ui.registration.dto.CourseSearchResult;
import org.kuali.student.enrollment.ui.registration.dto.InstructorSearchResult;
import org.kuali.student.enrollment.ui.registration.dto.RegGroupSearchResult;
import org.kuali.student.enrollment.ui.registration.dto.ScheduleSearchResult;
import org.kuali.student.r2.common.constants.CommonServiceConstants;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.ContextUtils;
import org.kuali.student.r2.common.util.constants.CourseOfferingServiceConstants;
import org.kuali.student.r2.common.util.constants.LprServiceConstants;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.service.AtpService;
import org.kuali.student.r2.core.class1.search.ActivityOfferingSearchServiceImpl;
import org.kuali.student.r2.core.class1.search.CoreSearchServiceImpl;
import org.kuali.student.r2.core.class1.search.CourseOfferingManagementSearchImpl;
import org.kuali.student.r2.core.constants.AtpServiceConstants;
import org.kuali.student.r2.core.scheduling.util.SchedulingServiceUtil;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultCellInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.service.SearchService;
import org.kuali.student.r2.lum.course.dto.ActivityInfo;
import org.kuali.student.r2.lum.course.service.CourseService;
import org.kuali.student.r2.lum.util.constants.CourseServiceConstants;

import javax.ws.rs.PathParam;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleOfClassesServiceImpl implements ScheduleOfClassesService {
    private SearchService searchService;
    private LprService lprService;
    private IdentityService identityService;
    private AtpService atpService;
    private CourseService courseService;
    private CourseOfferingService courseOfferingService;

    Comparator<RegGroupSearchResult> regResultComparator = new RegResultComparator();

    @Override
    public List<CourseSearchResult> loadCourseOfferingsByTermAndCourseCode(String termId, String courseCode) throws Exception {
        List<CourseSearchResult> courseSearchResults = searchForCourseOfferings(termId, courseCode);
        return courseSearchResults;
    }

    @Override
    public List<CourseSearchResult> loadCourseOfferingsByTermCodeAndCourseCode(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode) throws Exception {
        return loadCourseOfferingsByTermAndCourseCode(getAtpIdByAtpCode(termCode), courseCode);
    }

    @Override
    public List<RegGroupSearchResult> loadRegistrationGroupsByCourseOfferingId(@PathParam("courseOfferingId") String courseOfferingId) throws Exception {
        List<RegGroupSearchResult> regGroupResult = searchForRegGroups(courseOfferingId);
        return regGroupResult;
    }

    @Override
    public List<ActivityOfferingSearchResult> loadActivityOfferingsByCourseOfferingId(@PathParam("courseOfferingId") String courseOfferingId) throws Exception {
        List<ActivityOfferingSearchResult> retList = loadPopulatedActivityOfferingsByCourseOfferingId(courseOfferingId, ContextUtils.createDefaultContextInfo());
        return retList;
    }

    @Override
    public List<InstructorSearchResult> loadInstructorsByTermIdAndCourseCode(@PathParam("termId") String termId, @PathParam("courseCode") String courseCode) throws Exception {
        List<String> coIds = searchForCourseOfferingIdByCourseCodeAndTerm(courseCode, termId);

        return loadInstructorsByCourseOfferingId(KSCollectionUtils.getRequiredZeroElement(coIds));
    }

    @Override
    public List<InstructorSearchResult> loadInstructorsByTermCodeAndCourseCode(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode) throws Exception {
        return loadInstructorsByTermIdAndCourseCode(getAtpIdByAtpCode(termCode), courseCode);
    }

    @Override
    public List<RegGroupSearchResult> loadRegistrationGroupsByTermIdAndCourseCode(@PathParam("termId") String termId, @PathParam("courseCode") String courseCode) throws Exception {
        List<String> coIds = searchForCourseOfferingIdByCourseCodeAndTerm(courseCode, termId);

        return loadRegistrationGroupsByCourseOfferingId(KSCollectionUtils.getRequiredZeroElement(coIds));
    }

    @Override
    public List<RegGroupSearchResult> loadRegistrationGroupsByTermCodeAndCourseCode(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode) throws Exception {
        return loadRegistrationGroupsByTermIdAndCourseCode(getAtpIdByAtpCode(termCode), courseCode);
    }

    @Override
    public RegGroupSearchResult loadRegistrationGroupByTermCodeAndCourseCodeAndRegGroupName(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode, @PathParam("regGroupName") String regGroupName) throws Exception {

        RegGroupSearchResult result = null;
        List<RegGroupSearchResult> regGroupList = loadRegistrationGroupsByTermCodeAndCourseCode(termCode, courseCode);

        for(RegGroupSearchResult rg : regGroupList){
            if(rg.getRegGroupName().equals(regGroupName)){
                result = rg;
                break;
            }
        }
        return result;
    }

    @Override
    public List<ActivityOfferingSearchResult> loadActivityOfferingsByTermIdAndCourseCode(@PathParam("termId") String termId, @PathParam("courseCode") String courseCode) throws Exception {
        List<String> coIds = searchForCourseOfferingIdByCourseCodeAndTerm(courseCode, termId);
        return loadActivityOfferingsByCourseOfferingId(KSCollectionUtils.getRequiredZeroElement(coIds));

    }

    @Override
    public List<ActivityOfferingSearchResult> loadActivityOfferingsByTermCodeAndCourseCode(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode) throws Exception {
        return loadActivityOfferingsByTermIdAndCourseCode(getAtpIdByAtpCode(termCode), courseCode);
    }

    @Override
    public List<InstructorSearchResult> loadInstructorsByCourseOfferingId(@PathParam("courseOfferingId") String courseOfferingId) throws Exception {
        List<ActivityOfferingSearchResult> aoList = searchForRawActivities(courseOfferingId);

        List<String> aoIds = new ArrayList<String>();
        for(ActivityOfferingSearchResult ao : aoList){
            aoIds.add(ao.getActivityOfferingId());
        }

        return getInstructorListByAoIds(aoIds, ContextUtils.createDefaultContextInfo());
    }

    @Override
    public List<InstructorSearchResult> loadInstructorsByActivityOfferingId(@PathParam("activityOfferingId") String activityOfferingId) throws Exception {
        List<String> aoIds = new ArrayList<String>();
        aoIds.add(activityOfferingId);
        return getInstructorListByAoIds(aoIds, ContextUtils.createDefaultContextInfo());
    }


    @Override
    public String getAtpIdByAtpCode(String atpCode) throws Exception {
        String sRet = null;
        List<AtpInfo> atpList = getAtpService().getAtpsByCode(atpCode, ContextUtils.createDefaultContextInfo());
        if(atpList != null && !atpList.isEmpty()){
            sRet = KSCollectionUtils.getRequiredZeroElement(atpList).getId();
        }

        return sRet;
    }

    @Override
    public List<ActivityTypeSearchResult> loadActivitiesByTermCodeAndCourseCode(@PathParam("termCode") String termCode, @PathParam("courseCode") String courseCode) throws Exception {
        List<String> coIds = searchForCourseOfferingIdByCourseCodeAndTerm(courseCode, getAtpIdByAtpCode(termCode));
        return loadActivitiesByCourseOfferingId(KSCollectionUtils.getRequiredZeroElement(coIds));

    }

    @Override
    public List<ActivityTypeSearchResult> loadActivitiesByCourseOfferingId(String courseOfferingId) throws Exception {
        ContextInfo contextInfo = ContextUtils.createDefaultContextInfo();
        List<ActivityTypeSearchResult> activitiesTypeKeys = new ArrayList<ActivityTypeSearchResult>();
        List<FormatOfferingInfo> formatOfferings = getCourseOfferingService().getFormatOfferingsByCourseOffering(courseOfferingId,contextInfo);
        List<ActivityInfo> activities = new ArrayList<ActivityInfo>();
        for (FormatOfferingInfo formatOffering : formatOfferings){
               activities.addAll(getCourseService().getCourseActivitiesByCourseFormat(formatOffering.getFormatId(), contextInfo));
        }

        for(ActivityInfo activityInfo : activities){
            ActivityTypeSearchResult atsr = new ActivityTypeSearchResult();
            atsr.setTypeKey(activityInfo.getTypeKey());
            atsr.setName(activityInfo.getName());
            if(activityInfo.getDescr() != null){
                if(activityInfo.getDescr().getFormatted() != null){
                    atsr.setDescription(activityInfo.getDescr().getFormatted());
                }
                else {
                    atsr.setDescription(activityInfo.getDescr().getPlain());
                }
            }
            activitiesTypeKeys.add(atsr);
        }

        return activitiesTypeKeys;
    }

    private List<CourseSearchResult> searchForCourseOfferings(String termId, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        SearchRequestInfo searchRequest = createSearchRequest(termId, courseCode);
        SearchResultInfo searchResult = getSearchService().search(searchRequest, ContextUtils.createDefaultContextInfo());


        List<CourseSearchResult> results = new ArrayList<CourseSearchResult>();

        for (SearchResultRowInfo row : searchResult.getRows()) {
            CourseSearchResult courseSearchResult = new CourseSearchResult();

            for (SearchResultCellInfo cellInfo : row.getCells()) {

                String value = StringUtils.EMPTY;
                if (cellInfo.getValue() != null) {
                    value = cellInfo.getValue();
                }

                if (CourseOfferingManagementSearchImpl.SearchResultColumns.CODE.equals(cellInfo.getKey())) {
                    courseSearchResult.setCourseOfferingCode(value);
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.DESC.equals(cellInfo.getKey())) {
                    courseSearchResult.setCourseOfferingDesc(value);
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.GRADING_OPTION_NAME.equals(cellInfo.getKey())) {
                    courseSearchResult.setCourseOfferingGradingOptionDisplay(cellInfo.getValue());
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.CREDIT_OPTION_NAME.equals(cellInfo.getKey())) {
                    courseSearchResult.setCourseOfferingCreditOptionDisplay(cellInfo.getValue());
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.CO_ID.equals(cellInfo.getKey())) {
                    courseSearchResult.setCourseOfferingId(value);
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.HAS_STUDENT_SELECTABLE_PASSFAIL.equals(cellInfo.getKey())) {
                    courseSearchResult.setStudentSelectablePassFail(BooleanUtils.toBoolean(value));
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.CAN_AUDIT_COURSE.equals(cellInfo.getKey())) {
                    courseSearchResult.setAuditCourse(BooleanUtils.toBoolean(value));
                } else if (CourseOfferingManagementSearchImpl.SearchResultColumns.IS_HONORS_COURSE.equals(cellInfo.getKey())) {
                    courseSearchResult.setHonorsCourse(BooleanUtils.toBoolean(value));
                }

            }

            results.add(courseSearchResult);
        }

        return results;
    }

    private List<RegGroupSearchResult> searchForRegGroups(String courseOfferingId) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException {

        SearchRequestInfo searchRequest = createRegGroupSearchRequest(courseOfferingId);
        SearchResultInfo searchResult = getSearchService().search(searchRequest, ContextUtils.createDefaultContextInfo());

        Map<String, RegGroupSearchResult> regGroupResultMap = new HashMap<String, RegGroupSearchResult>();


        for (SearchResultRowInfo row : searchResult.getRows()) {


            String activityOfferingId = null;
            String regGroupId = null;
            String regGroupName = null;
            String regGroupState = null;

            for (SearchResultCellInfo cellInfo : row.getCells()) {

                String value = StringUtils.EMPTY;
                if (cellInfo.getValue() != null) {
                    value = cellInfo.getValue();
                }

                if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_ID.equals(cellInfo.getKey())) {
                    activityOfferingId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.RG_ID.equals(cellInfo.getKey())) {
                    regGroupId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.RG_NAME.equals(cellInfo.getKey())) {
                    regGroupName = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.RG_STATE.equals(cellInfo.getKey())) {
                    regGroupState = value;
                }

            }

            if (regGroupResultMap.containsKey(regGroupId)) {
                regGroupResultMap.get(regGroupId).getActivityOfferingIds().add(activityOfferingId);
            } else {
                RegGroupSearchResult regGroupSearchResult = new RegGroupSearchResult();
                regGroupSearchResult.setCourseOfferingId(courseOfferingId);
                regGroupSearchResult.setRegGroupId(regGroupId);
                regGroupSearchResult.setRegGroupName(regGroupName);
                regGroupSearchResult.setRegGroupState(regGroupState);
                regGroupSearchResult.getActivityOfferingIds().add(activityOfferingId);
                regGroupResultMap.put(regGroupId, regGroupSearchResult);

            }


        }

        List<RegGroupSearchResult> resultList = new ArrayList<RegGroupSearchResult>(regGroupResultMap.values());


        Collections.sort(resultList, regResultComparator);
        return resultList;
    }

    /**
     * Searches for a raw ( schedules, and instructors aren't pulled) of activities.
     *
     * @param courseOfferingId
     * @return
     * @throws Exception
     */
    private List<ActivityOfferingSearchResult> searchForRawActivities(String courseOfferingId) throws Exception {
        SearchRequestInfo searchRequestInfo = new SearchRequestInfo(ActivityOfferingSearchServiceImpl.AOS_AND_CLUSTERS_BY_CO_ID_SEARCH_KEY);

        searchRequestInfo.addParam(ActivityOfferingSearchServiceImpl.SearchParameters.CO_ID, courseOfferingId);
        SearchResultInfo searchResult = getSearchService().search(searchRequestInfo, ContextUtils.createDefaultContextInfo());

        List<ActivityOfferingSearchResult> resultList = new ArrayList<ActivityOfferingSearchResult>();


        for (SearchResultRowInfo row : searchResult.getRows()) {

            String formatOfferingId = null;
            String formatOfferingName = null;
            String formatId = null;
            String activityOfferingClusterId = null;
            String activityOfferingClusterName = null;
            String activityOfferingClusterPrivateName = null;
            String activityOfferingId = null;
            String activityOfferingCode = null;
            String activityOfferingType = null;
            String activityOfferingState = null;
            String activityOfferingMaxSeats = null;
            String scheduleId = null;
            String atpId = null;

            ActivityOfferingSearchResult result = new ActivityOfferingSearchResult();

            for (SearchResultCellInfo cellInfo : row.getCells()) {

                String value = StringUtils.EMPTY;
                if (cellInfo.getValue() != null) {
                    value = cellInfo.getValue();
                }

                if (ActivityOfferingSearchServiceImpl.SearchResultColumns.FO_ID.equals(cellInfo.getKey())) {
                    formatOfferingId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.FO_NAME.equals(cellInfo.getKey())) {
                    formatOfferingName = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.FORMAT_ID.equals(cellInfo.getKey())) {
                    formatId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AOC_ID.equals(cellInfo.getKey())) {
                    activityOfferingClusterId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AOC_NAME.equals(cellInfo.getKey())) {
                    activityOfferingClusterName = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AOC_PRIVATE_NAME.equals(cellInfo.getKey())) {
                    activityOfferingClusterPrivateName = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_ID.equals(cellInfo.getKey())) {
                    activityOfferingId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_CODE.equals(cellInfo.getKey())) {
                    activityOfferingCode = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_TYPE.equals(cellInfo.getKey())) {
                    activityOfferingType = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_STATE.equals(cellInfo.getKey())) {
                    activityOfferingState = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.AO_MAX_SEATS.equals(cellInfo.getKey())) {
                    activityOfferingMaxSeats = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.SCHEDULE_ID.equals(cellInfo.getKey())) {
                    scheduleId = value;
                } else if (ActivityOfferingSearchServiceImpl.SearchResultColumns.ATP_ID.equals(cellInfo.getKey())) {
                    atpId = value;
                }
            }

            result.setFormatId(formatId);
            result.setFormatOfferingId(formatOfferingId);
            result.setFormatOfferingName(formatOfferingName);
            result.setActivityOfferingClusterId(activityOfferingClusterId);
            result.setActivityOfferingClusterName(activityOfferingClusterName);
            result.setActivityOfferingClusterPrivateName(activityOfferingClusterPrivateName);
            result.setActivityOfferingClusterId(activityOfferingClusterId);
            result.setActivityOfferingCode(activityOfferingCode);
            result.setActivityOfferingType(activityOfferingType);
            result.setActivityOfferingId(activityOfferingId);
            result.setActivityOfferingState(activityOfferingState);
            result.setActivityOfferingMaxSeats(activityOfferingMaxSeats);
            result.setScheduleId(scheduleId);
            result.setAtpId(atpId);
            resultList.add(result);

        }
        return resultList;

    }

    private List<String> searchForCourseOfferingIdByCourseCodeAndTerm(String courseCode, String atpId) throws Exception {
        List<String> resultList = new ArrayList<String>();



        SearchRequestInfo searchRequestInfo = new SearchRequestInfo(CourseOfferingManagementSearchImpl.COID_BY_TERM_AND_COURSE_CODE_SEARCH_SEARCH_KEY);

        searchRequestInfo.addParam(CourseOfferingManagementSearchImpl.SearchParameters.COURSE_CODE, courseCode);
        searchRequestInfo.addParam(CourseOfferingManagementSearchImpl.SearchParameters.ATP_ID, atpId);

        SearchResultInfo searchResult = getSearchService().search(searchRequestInfo, ContextUtils.createDefaultContextInfo());


        for (SearchResultRowInfo row : searchResult.getRows()) {
            for (SearchResultCellInfo cellInfo : row.getCells()) {
                String value = StringUtils.EMPTY;
                if (cellInfo.getValue() != null) {
                    value = cellInfo.getValue();
                }
                if (CourseOfferingManagementSearchImpl.SearchResultColumns.CO_ID.equals(cellInfo.getKey())) {
                    resultList.add(value);
                }
            }
        }

        return resultList;
    }

    /**
     * This is an internal method that will return a map of scheduleId, ScheduleSearchResult. We are using a map object
     * so it is easier to build up complex objects in a more performant way. ie. If you're building a list of complex
     * ActivityOffering display objects and you want that object to contain schedule information. you can build this
     * list of schedules THEN as your building your list of ActivityObjects you can easily add a schedule object.
     * @param scheduleIds   list of schedule Ids to retrieve from db
     * @param contextInfo
     * @return
     * @throws Exception
     */
    protected Map<String, ScheduleSearchResult> searchForScheduleByScheduleIds(List<String> scheduleIds, ContextInfo contextInfo) throws Exception {
        Map<String, ScheduleSearchResult> resultList = new HashMap<String, ScheduleSearchResult>();

        SearchRequestInfo sr = new SearchRequestInfo(CoreSearchServiceImpl.SCH_AND_ROOM_SEARH_BY_ID_SEARCH_KEY);
        sr.addParam(CoreSearchServiceImpl.SearchParameters.SCHEDULE_IDS, new ArrayList<String>(scheduleIds));
        SearchResultInfo searchResult  = getSearchService().search(sr, contextInfo);

        for (SearchResultRowInfo row : searchResult.getRows()) {
            ScheduleSearchResult searchResultRow = new ScheduleSearchResult();
            for (SearchResultCellInfo cell : row.getCells()) {
                String value = StringUtils.EMPTY;
                if (cell.getValue() != null) {
                    value = cell.getValue();
                }
                if (CoreSearchServiceImpl.SearchResultColumns.SCH_ID.equals(cell.getKey())) {
                    searchResultRow.setScheduleId(value);
                }else if (CoreSearchServiceImpl.SearchResultColumns.START_TIME.equals(cell.getKey())) {
                    searchResultRow.setStartTimeMili(value);
                    searchResultRow.setStartTimeDisplay(convertMiliToDisplayTime(value));
                }else if (CoreSearchServiceImpl.SearchResultColumns.END_TIME.equals(cell.getKey())) {
                    searchResultRow.setEndTimeMili(value);
                    searchResultRow.setEndTimeDisplay(convertMiliToDisplayTime(value));
                }else if (CoreSearchServiceImpl.SearchResultColumns.TBA_IND.equals(cell.getKey())) {
                    searchResultRow.setTba(Boolean.parseBoolean(value));
                }else if (CoreSearchServiceImpl.SearchResultColumns.ROOM_CODE.equals(cell.getKey())) {
                    searchResultRow.setRoomName(value);
                }else if (CoreSearchServiceImpl.SearchResultColumns.BLDG_NAME.equals(cell.getKey())) {
                    searchResultRow.setBuildingName(value);
                }else if (CoreSearchServiceImpl.SearchResultColumns.BLDG_CODE.equals(cell.getKey())) {
                    searchResultRow.setBuildingCode(value);
                }else if (CoreSearchServiceImpl.SearchResultColumns.WEEKDAYS.equals(cell.getKey())) {
                    searchResultRow.setDays(value);
                }else if (CoreSearchServiceImpl.SearchResultColumns.SCH_ID.equals(cell.getKey())) {
                    searchResultRow.setScheduleId(value);
                }
            }
            resultList.put(searchResultRow.getScheduleId(), searchResultRow);
        }

        return resultList;
    }

    /**
     * Most of our public instructor methods take a list of AO IDS and want a list of instructors. Our interal instructor
     * processing returns a map. So, to make things easy we're providing a way to bypass the fact a map is used.
     * @param aoIds
     * @return
     * @throws Exception
     */
    private  List<InstructorSearchResult> getInstructorListByAoIds(List<String> aoIds, ContextInfo contextInfo) throws Exception {
        List<InstructorSearchResult> resultList = new ArrayList<InstructorSearchResult>();
        Map<String, List<InstructorSearchResult>> resultMap = searchForInstructorsByAoIds(aoIds, contextInfo);

        if(resultMap != null && !resultMap.isEmpty()){
            for(List<InstructorSearchResult> insrList : resultMap.values()){
                resultList.addAll(insrList);
            }
        }
        return resultList;

    }

    /**
     * Since schedule data must be pulled in a separate query, it's best if we populate the AOSearchResult
     * in this method. We could do all of this in the the raw activity search, but we have found cases
     * where the additional schedule search is not desired.
     *
     * @param aoList list of ActivityOfferingSearchResults that will be modified to include schedule data.
     * @param contextInfo
     * @throws Exception
     */
    private void populateActivityOfferingsWithScheduleData(List<ActivityOfferingSearchResult> aoList, ContextInfo contextInfo) throws Exception {
        List<String> scheduleIds = new ArrayList<String>();

        for(ActivityOfferingSearchResult ao : aoList){
            String scheduleId = ao.getScheduleId();
            if(scheduleId != null && !"".equals(scheduleId)){
                scheduleIds.add(scheduleId);
            }
        }

        Map<String, ScheduleSearchResult> schedMap = searchForScheduleByScheduleIds(scheduleIds, contextInfo);

        if(schedMap != null && !schedMap.isEmpty()){
            for(ActivityOfferingSearchResult ao : aoList){
                String scheduleId = ao.getScheduleId();
                if(scheduleId != null && !"".equals(scheduleId) && schedMap.containsKey(scheduleId)){
                    ao.setSchedule(schedMap.get(scheduleId));
                }
            }

        }
    }

    /**
     * Since schedule data must be pulled in a separate query, it's best if we populate the AOSearchResult
     * in this method. We could do all of this in the the raw activity search, but we have found cases
     * where the additional schedule search is not desired.
     *
     * @param aoList list of ActivityOfferingSearchResults that will be modified to include schedule data.
     * @param contextInfo
     * @throws Exception
     */
    private void populateActivityOfferingsWithInstructorData(List<ActivityOfferingSearchResult> aoList, ContextInfo contextInfo) throws Exception {
        List<String> aoIds = new ArrayList<String>();

        for(ActivityOfferingSearchResult ao : aoList){
            String aoId = ao.getActivityOfferingId();
            if(aoId != null && !"".equals(aoId)){
                aoIds.add(aoId);
            }
        }

        Map<String, List<InstructorSearchResult>> instMap = searchForInstructorsByAoIds(aoIds, contextInfo);

        if(instMap != null && !instMap.isEmpty()){
            for(ActivityOfferingSearchResult ao : aoList){
                String aoId = ao.getActivityOfferingId();
                if(aoId != null && !"".equals(aoId) && instMap.containsKey(aoId)){
                    ao.setInstructors(instMap.get(aoId));
                }
            }
        }
    }

    /**
     * We're making this a protected method so that implementing institutions can extend this class and change the way
     * that times are displayed.
     * @param mili
     * @return
     */
    protected String convertMiliToDisplayTime(String mili){
        String sRet = "";
        if(mili != null && !"".equals(mili)){
            sRet = SchedulingServiceUtil.makeFormattedTimeFromMillis(Long.parseLong(mili));
        }
        return sRet;

    }

    /**
     * This is an internal method that will return a map of aoId, InstructorSearchResult. We are using a map object
     * so it is easier to build up complex objects in a more performant way
     *
     * @param aoIds
     * @param contextInfo
     * @return
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws DoesNotExistException
     * @throws PermissionDeniedException
     * @throws OperationFailedException
     */
    protected Map<String, List<InstructorSearchResult>> searchForInstructorsByAoIds(List<String> aoIds, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, DoesNotExistException, PermissionDeniedException, OperationFailedException {

        Map<String, List<InstructorSearchResult>> resultList = new HashMap<String, List<InstructorSearchResult>>();
        Map<String, InstructorSearchResult> principalId2aoIdMap = new HashMap<String, InstructorSearchResult>();

        List<LprInfo> lprInfos = getLprService().getLprsByLuis(aoIds, contextInfo);
        if (lprInfos != null) {

            for (LprInfo lprInfo : lprInfos) {
                InstructorSearchResult result = new InstructorSearchResult();

                String aoId = lprInfo.getLuiId();
                //  Only include the main instructor.
                if (!StringUtils.equals(lprInfo.getTypeKey(), LprServiceConstants.INSTRUCTOR_MAIN_TYPE_KEY)) {
                    result.setPrimary(false);
                }else{
                    result.setPrimary(true);
                }
                result.setPrincipalId(lprInfo.getPersonId());
                principalId2aoIdMap.put(lprInfo.getPersonId(), result);
                result.setActivityOfferingId(aoId);

                if(resultList.containsKey(aoId)){
                  resultList.get(aoId).add(result);
                } else{
                    List<InstructorSearchResult> newList = new ArrayList<InstructorSearchResult>();
                    newList.add(result);
                    resultList.put(aoId, newList);
                }
            }

            if (!resultList.isEmpty()) {
                EntityDefaultQueryResults results = getInstructorsInfoFromKim(new ArrayList<String>(principalId2aoIdMap.keySet()));

                for (EntityDefault entity : results.getResults()) {
                    for (Principal principal : entity.getPrincipals()) {
                        InstructorSearchResult instructor = principalId2aoIdMap.get(principal.getPrincipalId());
                        if (instructor != null) {

                                if (entity.getName() != null) {
                                    instructor.setDisplayName(entity.getName().getCompositeName());
                                } else {
                                    instructor.setDisplayName(principal.getPrincipalId());
                                }
                        }
                    }
                }

            }
        }
        return resultList;
    }

    class RegResultComparator implements Comparator<RegGroupSearchResult> {
        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(RegGroupSearchResult a, RegGroupSearchResult b) {
            return a.getRegGroupName().compareToIgnoreCase(b.getRegGroupName());
        }
    }

    protected EntityDefaultQueryResults getInstructorsInfoFromKim(List<String> principalIds) {
        QueryByCriteria.Builder qbcBuilder = QueryByCriteria.Builder.create();
        qbcBuilder.setPredicates(
                PredicateFactory.in("principals.principalId", principalIds.toArray())
        );

        QueryByCriteria criteria = qbcBuilder.build();

        EntityDefaultQueryResults entityResults = getIdentityService().findEntityDefaults(criteria);

        return entityResults;
    }

    private SearchRequestInfo createRegGroupSearchRequest(String courseOfferingId) {
        SearchRequestInfo searchRequest = new SearchRequestInfo(ActivityOfferingSearchServiceImpl.REG_GROUPS_BY_CO_ID_SEARCH_KEY);

        //List<String> filterCOStates = new ArrayList<String>(1);
        //filterCOStates.add(LuiServiceConstants.LUI_CO_STATE_OFFERED_KEY);
        searchRequest.addParam(ActivityOfferingSearchServiceImpl.SearchParameters.CO_ID, courseOfferingId);

        return searchRequest;
    }

    /**
     * This is the method that should be called when you want a FULLY populated ActivityOfferingSearchResult object.
     * That means that the activityOfferingSearchResult objects will be populated with schedule, and instructor data.
     *
     * @param courseOfferingId
     * @param contextInfo
     * @return
     * @throws Exception
     */
    private List<ActivityOfferingSearchResult> loadPopulatedActivityOfferingsByCourseOfferingId(String courseOfferingId, ContextInfo contextInfo) throws Exception {
        List<ActivityOfferingSearchResult> retList = searchForRawActivities(courseOfferingId);
        populateActivityOfferingsWithScheduleData(retList, contextInfo);
        populateActivityOfferingsWithInstructorData(retList, contextInfo);
        return retList;
    }

    private SearchRequestInfo createSearchRequest(String termId, String courseCode) {
        SearchRequestInfo searchRequest = new SearchRequestInfo(CourseOfferingManagementSearchImpl.CO_MANAGEMENT_SEARCH.getKey());

        List<String> filterCOStates = new ArrayList<String>(1);
        filterCOStates.add(LuiServiceConstants.LUI_CO_STATE_OFFERED_KEY);
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.COURSE_CODE, courseCode);
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.FILTER_CO_STATES, filterCOStates);
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.ATP_ID, termId);
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.CROSS_LIST_SEARCH_ENABLED, BooleanUtils.toStringTrueFalse(false));
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.IS_EXACT_MATCH_CO_CODE_SEARCH, BooleanUtils.toStringTrueFalse(false));
        searchRequest.addParam(CourseOfferingManagementSearchImpl.SearchParameters.INCLUDE_PASSFAIL_AUDIT_HONORS_RESULTS, BooleanUtils.toStringTrueFalse(true));
        return searchRequest;
    }

    private SearchService getSearchService() {
        if (searchService == null) {
            searchService = (SearchService) GlobalResourceLoader.getService(new QName(CommonServiceConstants.REF_OBJECT_URI_GLOBAL_PREFIX + "search", SearchService.class.getSimpleName()));
        }
        return searchService;
    }

    public LprService getLprService() {
        if (lprService == null){
            lprService = (LprService) GlobalResourceLoader.getService(new QName(LprServiceConstants.NAMESPACE, LprServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return lprService;
    }

    public void setLprService(LprService lprService) {
        this.lprService = lprService;
    }

    public IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public AtpService getAtpService() {
        if (atpService == null){
            atpService = (AtpService) GlobalResourceLoader.getService(new QName(AtpServiceConstants.NAMESPACE, AtpServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return atpService;
    }

    public void setAtpService(AtpService atpService) {
        this.atpService = atpService;
    }

    public CourseService getCourseService() {
        if (courseService == null) {
            QName qname = new QName(CourseServiceConstants.NAMESPACE,
                    CourseServiceConstants.SERVICE_NAME_LOCAL_PART);
            courseService = GlobalResourceLoader.getService(qname);
        }
        return courseService;
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public CourseOfferingService getCourseOfferingService() {
        if(courseOfferingService == null) {
            courseOfferingService =  GlobalResourceLoader.getService(new QName(CourseOfferingServiceConstants.NAMESPACE,
                    CourseOfferingServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return courseOfferingService;
    }

    public void setCourseOfferingService(CourseOfferingService courseOfferingService) {
        this.courseOfferingService = courseOfferingService;
    }
}
