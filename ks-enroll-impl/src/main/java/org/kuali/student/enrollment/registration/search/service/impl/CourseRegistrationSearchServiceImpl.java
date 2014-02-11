/**
 * Copyright 2014 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * Created by vgadiyak on 1/8/14
 */
package org.kuali.student.enrollment.registration.search.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.student.r2.common.class1.search.SearchServiceAbstractHardwiredImplBase;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.common.util.constants.CourseOfferingServiceConstants;
import org.kuali.student.r2.common.util.date.DateFormatters;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.util.SearchRequestHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to do custom searches for Course Registration
 *
 * @author Kuali Student Team
 */
public class CourseRegistrationSearchServiceImpl extends SearchServiceAbstractHardwiredImplBase {

    @Resource
    private EntityManager entityManager;

    public static final Map<String, TypeInfo> searchKeyToSearchTypeMap;
    public static final String REG_INFO_BY_PERSON_TERM_SEARCH_KEY =
            "kuali.search.type.lui.searchForCourseRegistrationByStudentAndTerm ";
    public static final String AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_KEY =
            "kuali.search.type.lui.searchForAOSchedulesAndCOCreditAndGradingOptionsByIds";
    public static final String LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_SEARCH_KEY =
            "kuali.search.type.lpr.searchForLprTransIdsByAtpAndPersonAndTypeKey";
    public static final String REG_CART_BY_PERSON_TERM_SEARCH_KEY = 
            "kuali.search.type.lui.searchForCourseRegistrationCartByStudentAndTerm";
    public static final String RVGS_BY_LUI_IDS_SEARCH_KEY =
            "kuali.search.type.lui.searchForRVGsByLuiIds";
    
    public static final TypeInfo REG_INFO_BY_PERSON_TERM_SEARCH_TYPE;
    public static final TypeInfo REG_CART_BY_PERSON_TERM_SEARCH_TYPE;
    public static final TypeInfo AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_TYPE;
    public static final TypeInfo LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_KEY_SEARCH_TYPE;
    public static final TypeInfo RVGS_BY_LUI_IDS_SEARCH_TYPE;

    public static final String DEFAULT_EFFECTIVE_DATE = "01/01/2012";

    public static final class SearchParameters {
        public static final String AO_ID = "activityOfferingId";
        public static final String LUI_IDS = "luiIds";
        public static final String CO_ID = "courseOfferingId";
        public static final String RG_ID = "regGroupId";
        public static final String PERSON_ID = "personId";
        public static final String CART_ITEM_ID = "cartItemId";
        public static final String ATP_ID = "atpId";
        public static final String TYPE_KEY = "typeKey";
    }

    public static final class SearchResultColumns {
        public static final String LUI_ID = "luiId";
        public static final String MASTER_LUI_ID = "masterLuiId";
        public static final String PERSON_LUI_TYPE = "personLuiType";
        public static final String LUI_NAME = "luiName";
        public static final String LUI_LONG_NAME = "luiLongName";
        public static final String LUI_CODE = "luiCode";
        public static final String LUI_TYPE = "luiType";
        public static final String LUI_DESC = "luiDesc";
        public static final String RES_VAL_GROUP_KEY = "resultValuesGroupKey";
        public static final String CREDITS = "credits";
        public static final String ROOM_CODE = "roomCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String WEEKDAYS = "weekdays";
        public static final String START_TIME_MS = "startTimeMs";
        public static final String END_TIME_MS = "endTimeMs";
        public static final String ATP_ID = "atpId";
        public static final String ATP_CD = "atpCd";
        public static final String ATP_NAME = "atpName";
        public static final String LPR_TRANS_ID = "lprTransId";
        
        public static final String CART_ID = "cartId";
        public static final String CART_ITEM_ID = "cartItemId";
        public static final String COURSE_CODE = "courseCode";
        public static final String COURSE_ID = "courseId";
        public static final String RG_CODE = "regGroupCode";
        public static final String AO_NAME = "aoName";
        public static final String AO_TYPE = "aoType";
        public static final String GRADING = "grading";
        public static final String RVG_ID = "rvgId";
        public static final String RVG_NAME = "rvgName";
        public static final String RVG_VALUE = "rvgValue";
    }

    static {
        searchKeyToSearchTypeMap = new HashMap<String, TypeInfo>();

        TypeInfo info = new TypeInfo();
        info.setKey(REG_INFO_BY_PERSON_TERM_SEARCH_KEY);
        info.setName("Registraion info by person and term");
        info.setDescr(new RichTextHelper().fromPlain("Returns registraion info for given person and term"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));

        REG_INFO_BY_PERSON_TERM_SEARCH_TYPE = info;
        searchKeyToSearchTypeMap.put(info.getKey(), info);

        info = new TypeInfo();
        info.setKey(AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_KEY);
        info.setName("AO schedules by AO ids");
        info.setDescr(new RichTextHelper().fromPlain("Returns AO schedules for given aoID"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));

        AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_TYPE = info;
        searchKeyToSearchTypeMap.put(info.getKey(), info);

        // Search for LPR transactions by personId, atpId, and typeKey.  Can be used to fetch an ID for a
        // registration cart by looking for the registration cart ID.  It returns IDs only.
        info = new TypeInfo();
        String desc = "Lpr trans ids by person and term and typeKey";
        info.setKey(LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_SEARCH_KEY);
        info.setName(desc);
        info.setDescr(new RichTextHelper().fromPlain(desc));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));

        LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_KEY_SEARCH_TYPE = info;
        searchKeyToSearchTypeMap.put(info.getKey(), info);

        info = new TypeInfo();
        info.setKey(REG_CART_BY_PERSON_TERM_SEARCH_KEY);
        info.setName("Registraion Cart by person and term");
        info.setDescr(new RichTextHelper().fromPlain("Returns registraion cart for given person and term"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));

        REG_CART_BY_PERSON_TERM_SEARCH_TYPE = info;
        searchKeyToSearchTypeMap.put(info.getKey(), info);

        info = new TypeInfo();
        info.setKey(RVGS_BY_LUI_IDS_SEARCH_KEY);
        info.setName("RVG information by list of lui Ids");
        info.setDescr(new RichTextHelper().fromPlain("Returns RVG keys names, and result values for credit options"));
        info.setEffectiveDate(DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER.parse(DEFAULT_EFFECTIVE_DATE));

        RVGS_BY_LUI_IDS_SEARCH_TYPE = info;
        searchKeyToSearchTypeMap.put(info.getKey(), info);
    }

    @Override
    public TypeInfo getSearchType() {
        return REG_INFO_BY_PERSON_TERM_SEARCH_TYPE;
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        TypeInfo typeInfo = searchKeyToSearchTypeMap.get(searchTypeKey);
        if (typeInfo != null) {
            return typeInfo;
        }

        throw new DoesNotExistException("No Search Type Found for key: " + searchTypeKey);
    }

    @Override
    public List<TypeInfo> getSearchTypes(ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return Arrays.asList(REG_INFO_BY_PERSON_TERM_SEARCH_TYPE, AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_TYPE,
                LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_KEY_SEARCH_TYPE, REG_CART_BY_PERSON_TERM_SEARCH_TYPE, RVGS_BY_LUI_IDS_SEARCH_TYPE);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo) throws MissingParameterException, OperationFailedException, PermissionDeniedException {

        if (REG_INFO_BY_PERSON_TERM_SEARCH_TYPE.getKey().equals(searchRequestInfo.getSearchKey())) {
            return searchForCourseRegistrationByPersonAndTerm(searchRequestInfo);
        } else if (LPR_TRANS_IDS_BY_PERSON_TERM_TYPE_KEY_SEARCH_TYPE.getKey().equals(searchRequestInfo.getSearchKey())) {
            return searchForLprTransIdsByAtpAndPersonAndTypeKey(searchRequestInfo);
        } else if (AO_SCHEDULES_CO_CREDITS_GRADING_OPTIONS_BY_IDS_SEARCH_TYPE.getKey().equals(searchRequestInfo.getSearchKey())) {
            return searchForAOSchedulesAndCOCreditAndGradingOptionsByIds(searchRequestInfo);
        } else if (REG_CART_BY_PERSON_TERM_SEARCH_TYPE.getKey().equals(searchRequestInfo.getSearchKey())) {
            return searchForCourseRegistrationCartByPersonAndTerm(searchRequestInfo);
        } else if (RVGS_BY_LUI_IDS_SEARCH_TYPE.getKey().equals(searchRequestInfo.getSearchKey())) {
            return searchForRVGsByLuiIds(searchRequestInfo);
        } else {
            throw new OperationFailedException("Unsupported search type: " + searchRequestInfo.getSearchKey());
        }
    }

    private SearchResultInfo searchForRVGsByLuiIds(SearchRequestInfo searchRequestInfo) throws OperationFailedException {
        SearchResultInfo resultInfo = new SearchResultInfo();
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);
        List<String> luiIds = requestHelper.getParamAsList(SearchParameters.LUI_IDS);

        String queryStr =
                "SELECT " +
                        "    lrvg.LUI_ID luiId, " +
                        "    rvg.ID      rvgId, " +
                        "    rvg.name    rvgName, " +
                        "    rvgVal.RESULT_VALUE rvgValue " +
                        "FROM " +
                        "    KSEN_LUI_RESULT_VAL_GRP lrvg, " +
                        "    KSEN_LRC_RVG rvg " +
                        "LEFT OUTER JOIN " +
                        "    KSEN_LRC_RVG_RESULT_VALUE rvg2Val " +
                        "ON " +
                        "    rvg2Val.RVG_ID=rvg.id " +
                        "AND rvg.id LIKE 'kuali.creditType.credit.degree.%' " +
                        "LEFT OUTER JOIN " +
                        "    KSEN_LRC_RESULT_VALUE rvgVal " +
                        "ON " +
                        "    rvgVal.ID = rvg2Val.RESULT_VALUE_ID " +
                        "WHERE " +
                        "    lrvg.LUI_ID IN(:luiIds) " +
                        "AND lrvg.RESULT_VAL_GRP_ID=rvg.ID";


        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter(SearchParameters.LUI_IDS, luiIds);

        List<Object[]> results = query.getResultList();

        for (Object[] resultRow : results) {
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(SearchResultColumns.LUI_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.RVG_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.RVG_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.RVG_VALUE, (String) resultRow[i++]);

            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }

    private SearchResultInfo searchForCourseRegistrationCartByPersonAndTerm(SearchRequestInfo searchRequestInfo) throws OperationFailedException {
        SearchResultInfo resultInfo = new SearchResultInfo();
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);
        String atpId = requestHelper.getParamAsString(SearchParameters.ATP_ID);
        String personId = requestHelper.getParamAsString(SearchParameters.PERSON_ID);
        String cartItemId = requestHelper.getParamAsString(SearchParameters.CART_ITEM_ID);

        String queryStr =
                "SELECT " +
                        "    lprt.id     cartId, " +
                        "    lprti.ID    cartItemId, " +
                        "    coId.LUI_CD courseCode, " +
                        "    co.ID       courseId, " +
                        "    rg.NAME     rgName, " +
                        "    ao.NAME     aoName, " +
//                        "    co.DESCR_FORMATTED, " +
                        "    ao.LUI_TYPE luiType, " +
                        "    coId.LNG_NAME coTitle, " +
                        "    room.ROOM_CD room, " +
                        "    room2bldg.BUILDING_CD building, " +
                        "    schedTmslt.WEEKDAYS weekdays, " +
                        "    schedTmslt.START_TIME_MS startTime, " +
                        "    schedTmslt.END_TIME_MS endTime, " +
                        "    credits.RESULT_VAL_GRP_ID credits, " +
                        "    grading.RESULT_VAL_GRP_ID grading " +
                        "FROM " +
                        "    KSEN_LPR_TRANS lprt, " +
                        "    KSEN_LUI co, " +
                        "    KSEN_LUI ao, " +
                        "    KSEN_LUI rg, " +
                        "    KSEN_LUI_IDENT coId, " +
                        "    KSEN_LUILUI_RELTN rg2ao, " +
                        "    KSEN_LUILUI_RELTN fo2rg, " +
                        "    KSEN_LUILUI_RELTN co2fo, " +
                        "    KSEN_LUI_SCHEDULE sched, " +
                        "    KSEN_SCHED_CMP schedCmp, " +
                        "    KSEN_ROOM room, " +
                        "    KSEN_ROOM_BUILDING room2bldg, " +
                        "    KSEN_SCHED_CMP_TMSLOT schedCmpTmslt, " +
                        "    KSEN_SCHED_TMSLOT schedTmslt, " +
                        "    KSEN_LPR_TRANS_ITEM lprti " +
                        "LEFT OUTER JOIN " +
                        "    KSEN_LPR_TRANS_ITEM_RVG credits " +
                        "ON " +
                        "    credits.LPR_TRANS_ITEM_ID = lprti.id " +
                        "AND credits.RESULT_VAL_GRP_ID LIKE 'kuali.result.value.credit.degree.%' " +
                        "LEFT OUTER JOIN " +
                        "    KSEN_LPR_TRANS_ITEM_RVG grading " +
                        "ON " +
                        "    grading.LPR_TRANS_ITEM_ID = lprti.id " +
                        "AND grading.RESULT_VAL_GRP_ID LIKE 'kuali.resultComponent.grade.%' " +
                        "WHERE " +
                        "    lprt.REQUESTING_PERS_ID = :personId " +
                        "AND lprt.LPR_TRANS_TYPE='kuali.lpr.trans.type.registration.cart' " +
                        "AND lprt.ATP_ID = :atpId " +
                        "AND lprti.LPR_TRANS_ID=lprt.ID " +
                        "AND rg2ao.LUILUI_RELTN_TYPE='kuali.lui.lui.relation.type.registeredforvia.rg2ao' " +
                        "AND fo2rg.LUILUI_RELTN_TYPE='kuali.lui.lui.relation.type.deliveredvia.fo2rg' " +
                        "AND co2fo.LUILUI_RELTN_TYPE='kuali.lui.lui.relation.type.deliveredvia.co2fo' " +
                        "AND rg2ao.LUI_ID=lprti.NEW_LUI_ID " +
                        "AND fo2rg.RELATED_LUI_ID = lprti.NEW_LUI_ID " +
                        "AND co2fo.RELATED_LUI_ID = fo2rg.LUI_ID " +
                        "AND ao.id = rg2ao.RELATED_LUI_ID " +
                        "AND co.id = co2fo.LUI_ID " +
                        "AND rg.id = lprti.NEW_LUI_ID " +
                        "AND coId.LUI_ID = co.id " +
                        "AND sched.LUI_ID = ao.ID " +
                        "AND schedCmp.SCHED_ID = sched.SCHED_ID " +
                        "AND room.ID = schedCmp.ROOM_ID " +
                        "AND room2bldg.ID = room.BUILDING_ID " +
                        "AND schedCmpTmslt.SCHED_CMP_ID = schedCmp.ID " +
                        "AND schedTmslt.ID = schedCmpTmslt.TM_SLOT_ID " +
                        (StringUtils.isEmpty(cartItemId)?" ":"AND lprti.ID = :cartItemId ") +
                        "ORDER BY " +
                        "    lprt.ID, " +
                        "    ao.LUI_TYPE";


        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter(SearchParameters.PERSON_ID, personId);
        query.setParameter(SearchParameters.ATP_ID, atpId);
        if(!StringUtils.isEmpty(cartItemId)){
            query.setParameter(SearchParameters.CART_ITEM_ID, cartItemId);
        }

        List<Object[]> results = query.getResultList();

        for (Object[] resultRow : results) {
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(SearchResultColumns.CART_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.CART_ITEM_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.COURSE_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.COURSE_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.RG_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.AO_NAME, (String) resultRow[i++]);
//            row.addCell(SearchResultColumns.LUI_DESC, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.AO_TYPE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_LONG_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.ROOM_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.BUILDING_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.WEEKDAYS, (String) resultRow[i++]);
            BigDecimal startTimeMs = (BigDecimal) resultRow[i++];
            row.addCell(SearchResultColumns.START_TIME_MS, (startTimeMs == null) ? "" : startTimeMs.toString());
            BigDecimal endTimeMs = (BigDecimal) resultRow[i++];
            row.addCell(SearchResultColumns.END_TIME_MS, (endTimeMs == null) ? "" : endTimeMs.toString());
            row.addCell(SearchResultColumns.CREDITS, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.GRADING, (String) resultRow[i++]);

            resultInfo.getRows().add(row);
        }

        return resultInfo;

    }

    /**
     * Returns list of Registration Info for the person: CO, AO, Schedules, etc.
     *
     * @throws OperationFailedException
     */
    private SearchResultInfo searchForCourseRegistrationByPersonAndTerm(SearchRequestInfo searchRequestInfo) throws OperationFailedException {
        SearchResultInfo resultInfo = new SearchResultInfo();
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);
        String atpId = requestHelper.getParamAsString(SearchParameters.ATP_ID);
        String personId = requestHelper.getParamAsString(SearchParameters.PERSON_ID);

        String queryStr =
                "SELECT atp.ID, atp.ATP_CD, atp.NAME as atp_name, lpr.LUI_ID, lpr.MASTER_LUI_ID, lpr.LPR_TYPE, lpr.CREDITS, " +
                        "luiId.LUI_CD, lui.NAME as lui_name, lui.DESCR_FORMATTED, lui.LUI_TYPE, luiId.LNG_NAME, " +
                        "room.ROOM_CD, rBldg.BUILDING_CD, " +
                        "schedTmslt.WEEKDAYS, schedTmslt.START_TIME_MS, schedTmslt.END_TIME_MS " +
                        "FROM KSEN_ATP atp, " +
                        "     KSEN_LPR lpr, " +
                        "     KSEN_LUI lui, " +
                        "     KSEN_LUI_IDENT luiId " +
                        "LEFT OUTER JOIN KSEN_LUI_SCHEDULE aoSched " +
                        "ON aoSched.LUI_ID = luiId.LUI_ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_CMP schedCmp " +
                        "ON schedCmp.SCHED_ID = aoSched.SCHED_ID " +
                        "LEFT OUTER JOIN KSEN_ROOM room " +
                        "ON room.ID = schedCmp.ROOM_ID " +
                        "LEFT OUTER JOIN KSEN_ROOM_BUILDING rBldg " +
                        "ON rBldg.ID = room.BUILDING_ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_CMP_TMSLOT schedCmpTmslt " +
                        "ON schedCmpTmslt.SCHED_CMP_ID = schedCmp.ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_TMSLOT schedTmslt " +
                        "ON schedTmslt.ID = schedCmpTmslt.TM_SLOT_ID " +
                        "WHERE lpr.PERS_ID = :personId " +
                        "  AND atp.ID = lpr.ATP_ID " +
                        "  AND lui.ID = lpr.LUI_ID " +
                        "  AND lui.ATP_ID = lpr.ATP_ID " +
                        "  AND luiId.LUI_ID = lui.ID ";

        if (!StringUtils.isEmpty(atpId)) {
            queryStr = queryStr + " AND lpr.ATP_ID = :atpId ";
        }

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter(SearchParameters.PERSON_ID, personId);
        if (!StringUtils.isEmpty(atpId)) {
            query.setParameter(SearchParameters.ATP_ID, atpId);
        }
        List<Object[]> results = query.getResultList();

        for (Object[] resultRow : results) {
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(SearchResultColumns.ATP_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.ATP_CD, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.ATP_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.MASTER_LUI_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.PERSON_LUI_TYPE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.CREDITS, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_DESC, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_TYPE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_LONG_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.ROOM_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.BUILDING_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.WEEKDAYS, (String) resultRow[i++]);
            BigDecimal startTimeMs = (BigDecimal) resultRow[i++];
            row.addCell(SearchResultColumns.START_TIME_MS, (startTimeMs == null) ? "" : startTimeMs.toString());
            BigDecimal endTimeMs = (BigDecimal) resultRow[i];
            row.addCell(SearchResultColumns.END_TIME_MS, (endTimeMs == null) ? "" : endTimeMs.toString());
            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }

    /**
     *
     * @param searchRequestInfo Search request parameter
     * @return Search result with list of LPR Transaction IDs
     * @throws OperationFailedException
     */
    private SearchResultInfo searchForLprTransIdsByAtpAndPersonAndTypeKey(SearchRequestInfo searchRequestInfo)
            throws OperationFailedException {
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);
        String atpId = requestHelper.getParamAsString(SearchParameters.ATP_ID);
        String personId = requestHelper.getParamAsString(SearchParameters.PERSON_ID);
        String typeKey = requestHelper.getParamAsString(SearchParameters.TYPE_KEY);

        String queryStr = "SELECT lprTrans.ID lprtId " +
                "FROM KSEN_LPR_TRANS lprTrans WHERE " +
                " lprTrans.ATP_ID = :atpId AND " +
                " lprTrans.LPR_TRANS_TYPE = :typeKey AND " +
                " lprTrans.REQUESTING_PERS_ID = :personId";
        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter(SearchParameters.ATP_ID, atpId);
        query.setParameter(SearchParameters.PERSON_ID, personId);
        query.setParameter(SearchParameters.TYPE_KEY, typeKey);
        // For some reason, this only returns a list of strings (probably since only one item is being
        // queried for).
        List<String> results = query.getResultList();

        SearchResultInfo resultInfo = new SearchResultInfo();
        for (String result : results) {
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(SearchResultColumns.LPR_TRANS_ID, result);
            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }

    /**
     * Returns list of Registration Info for the person: CO, AO, Schedules, etc.
     *
     * @throws OperationFailedException
     */
    private SearchResultInfo searchForAOSchedulesAndCOCreditAndGradingOptionsByIds(SearchRequestInfo searchRequestInfo) throws OperationFailedException {
        SearchResultInfo resultInfo = new SearchResultInfo();
        SearchRequestHelper requestHelper = new SearchRequestHelper(searchRequestInfo);
        List<String> luiIdsList = requestHelper.getParamAsList(SearchParameters.LUI_IDS);
        String luiIds = commaString(luiIdsList);

        String queryStr =
                "SELECT lui.ID, lui.NAME, luiId.LNG_NAME, luiRes.RESULT_VAL_GRP_ID, " +
                        "room.ROOM_CD, rBldg.BUILDING_CD, " +
                        "schedTmslt.WEEKDAYS, schedTmslt.START_TIME_MS, schedTmslt.END_TIME_MS " +
                        "FROM KSEN_LUI_IDENT luiId, KSEN_LUI lui " +
                        "LEFT OUTER JOIN KSEN_LUI_RESULT_VAL_GRP luiRes " +
                        "ON luiRes.LUI_ID = lui.ID " +
                        "AND (luiRes.RESULT_VAL_GRP_ID in (" + getStudentRegGradingOptionsStr() + ")" +
                        "       OR luiRes.RESULT_VAL_GRP_ID like 'kuali.creditType.credit%') " +
                        "LEFT OUTER JOIN KSEN_LUI_SCHEDULE aoSched " +
                        "ON aoSched.LUI_ID = lui.ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_CMP schedCmp " +
                        "ON schedCmp.SCHED_ID = aoSched.SCHED_ID " +
                        "LEFT OUTER JOIN KSEN_ROOM room " +
                        "ON room.ID = schedCmp.ROOM_ID " +
                        "LEFT OUTER JOIN KSEN_ROOM_BUILDING rBldg " +
                        "ON rBldg.ID = room.BUILDING_ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_CMP_TMSLOT schedCmpTmslt " +
                        "ON schedCmpTmslt.SCHED_CMP_ID = schedCmp.ID " +
                        "LEFT OUTER JOIN KSEN_SCHED_TMSLOT schedTmslt " +
                        "ON schedTmslt.ID = schedCmpTmslt.TM_SLOT_ID " +
                        "WHERE lui.ID IN (:luiIds) " +
                        "  AND luiId.LUI_ID = lui.ID";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter(SearchParameters.LUI_IDS, luiIds);
        List<Object[]> results = query.getResultList();

        for (Object[] resultRow : results) {
            int i = 0;
            SearchResultRowInfo row = new SearchResultRowInfo();
            row.addCell(SearchResultColumns.LUI_ID, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.LUI_LONG_NAME, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.RES_VAL_GROUP_KEY, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.ROOM_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.BUILDING_CODE, (String) resultRow[i++]);
            row.addCell(SearchResultColumns.WEEKDAYS, (String) resultRow[i++]);
            BigDecimal startTimeMs = (BigDecimal) resultRow[i++];
            row.addCell(SearchResultColumns.START_TIME_MS, (startTimeMs == null) ? "" : startTimeMs.toString());
            BigDecimal endTimeMs = (BigDecimal) resultRow[i];
            row.addCell(SearchResultColumns.END_TIME_MS, (endTimeMs == null) ? "" : endTimeMs.toString());
            resultInfo.getRows().add(row);
        }

        return resultInfo;
    }

    private static String commaString(List<String> items){
        return items.toString().replace("[", "'").replace("]", "'").replace(", ", "','");
    }

    // getting all possible student registration grading options
    private String getStudentRegGradingOptionsStr() {
        String[] studentRegGradingOptions = CourseOfferingServiceConstants.ALL_STUDENT_REGISTRATION_OPTION_TYPE_KEYS;
        StringBuilder bld = new StringBuilder();
        for (String studentRegGradingOption : studentRegGradingOptions) {
            bld.append(",'" + studentRegGradingOption + "'");
        }
        return bld.toString().substring(1);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
