package org.kuali.student.ap.coursesearch.controller;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.academicplan.constants.AcademicPlanServiceConstants;
import org.kuali.student.ap.academicplan.dto.LearningPlanInfo;
import org.kuali.student.ap.academicplan.dto.PlanItemInfo;
import org.kuali.student.ap.academicplan.infc.LearningPlan;
import org.kuali.student.ap.academicplan.infc.PlanItem;
import org.kuali.student.ap.academicplan.service.AcademicPlanService;
import org.kuali.student.ap.coursesearch.CourseSearchForm;
import org.kuali.student.ap.coursesearch.CourseSearchItem;
import org.kuali.student.ap.coursesearch.CourseSearchStrategy;
import org.kuali.student.ap.coursesearch.Credit;
import org.kuali.student.ap.coursesearch.CreditsFormatter;
import org.kuali.student.ap.coursesearch.Hit;
import org.kuali.student.ap.coursesearch.dataobject.CourseSearchItemImpl;
import org.kuali.student.ap.coursesearch.form.CourseSearchFormImpl;
import org.kuali.student.ap.coursesearch.util.CourseLevelFacet;
import org.kuali.student.ap.coursesearch.util.CreditsFacet;
import org.kuali.student.ap.coursesearch.util.CurriculumFacet;
import org.kuali.student.ap.coursesearch.util.GenEduReqFacet;
import org.kuali.student.ap.coursesearch.util.TermsFacet;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.CourseSearchConstants;
import org.kuali.student.ap.framework.context.TermHelper;
import org.kuali.student.ap.framework.util.KsapHelperUtil;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.core.acal.infc.Term;
import org.kuali.student.r2.core.enumerationmanagement.dto.EnumeratedValueInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.search.dto.SearchParamInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.infc.SearchResult;
import org.kuali.student.r2.core.search.infc.SearchResultCell;
import org.kuali.student.r2.core.search.infc.SearchResultRow;
import org.kuali.student.r2.lum.clu.dto.CluInfo;
import org.kuali.student.r2.lum.clu.service.CluService;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.or;

public class CourseSearchStrategyImpl implements CourseSearchStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CourseSearchStrategyImpl.class);

    private static final Map<String, Comparator<String>> FACET_SORT;

    private static WeakReference<Map<String, Credit>> creditMapRef;
    private static WeakReference<Map<String, String>> genEdMapRef;
    private static WeakReference<Map<String, String>> curriculumMapRef = null;

    private static Map<String, String> subjectAreaMap = null;

    private boolean limitExceeded;

    private final String NONE = "none";
    private final String SAVED = "saved";
    private final String PLANNED = "planned";
    private final String SAVED_AND_PLANNED = "saved_and_planned";

    public static final String DIVISIONS_COMPONENTS = "divisions";
    public static final String KEYWORDDIVISIONS_COMPONENTS = "keywordDivisions";
    public static final String CODES_COMPONENTS = "codes";
    public static final String LEVELS_COMPONENTS = "levels";
    public static final String INCOMPLETEDCODES_COMPONENTS = "incompleteCodes";
    public static final String COMPLETEDCODES_COMPONENTS = "completedCodes";
    public static final String COMPLETEDLEVELS_COMPONENTS = "completedLevels";

    static {
        // Related to CourseSearchUI.xml definitions
        Map<String, Comparator<String>> l = new java.util.LinkedHashMap<String, Comparator<String>>(
                5);
        l.put("facet_quarter", TermHelper.TERMS);
        l.put("facet_genedureq", KsapHelperUtil.ALPHA);
        l.put("facet_credits", CreditsFormatter.CREDIT);
        l.put("facet_level", KsapHelperUtil.NUMERIC);
        l.put("facet_curriculum", KsapHelperUtil.ALPHA);
        FACET_SORT = Collections
                .unmodifiableMap(Collections.synchronizedMap(l));
    }

    @Override
    public CourseSearchForm createInitialSearchForm() {
        CourseSearchFormImpl rv = new CourseSearchFormImpl();
        return rv;
    }

    @Override
    public List<CourseSearchItem> courseSearch(CourseSearchForm form, String studentId) {
        String maxCountProp = ConfigContext.getCurrentContextConfig()
                .getProperty("ksap.search.results.max");
        int maxCount = maxCountProp != null && !"".equals(maxCountProp.trim()) ? Integer
                .valueOf(maxCountProp) : MAX_HITS;
        this.limitExceeded = false;
        List<SearchRequestInfo> requests = buildSearchRequests(form);
        List<Hit> hits = preformSearch(requests);
        List<CourseSearchItem> courseList = new ArrayList<CourseSearchItem>();
        Map<String, String> courseStatusMap = getCourseStatusMap(studentId);
        List<String> courseIDs = new ArrayList<String>();
        for (Hit hit : hits) {
            courseIDs.add(hit.courseID);
        }

        List<CourseSearchItemImpl> courses = new ArrayList<CourseSearchItemImpl>();
        if (!courseIDs.isEmpty()) {
            courseIDs = termfilterCourseIds(courseIDs, form.getSearchTerm());
            if (courseIDs.size() > maxCount) {
                List<String> temp = new ArrayList<String>();
                for (String id : courseIDs) {
                    if (temp.size() >= maxCount) {
                        this.limitExceeded = true;
                        break;
                    }
                    temp.add(id);
                }
                courseIDs = temp;
            }

            if (!courseIDs.isEmpty()) {
                courses = getCoursesInfo(courseIDs);
            }
        }
        Set<String> orgIds = new HashSet<String>();
        for (CourseSearchItemImpl course : courses) {

            String courseId = course.getCourseId();
            String orgId = "ORGID-" + course.getSubject();
            orgIds.add(orgId);
            if (courseStatusMap.containsKey(courseId)) {

                String status = courseStatusMap.get(courseId);
                if (status.equals(NONE)) {
                    course.setSaved(false);
                    course.setPlanned(false);
                } else if (status.equals(SAVED)) {
                    course.setSaved(true);
                    course.setPlanned(false);
                } else if (status.equals(PLANNED)) {
                    course.setPlanned(true);
                    course.setSaved(false);
                } else if (status.equals(SAVED_AND_PLANNED)) {
                    course.setPlanned(true);
                    course.setSaved(true);
                } else {
                    LOG.debug("Unknown status in map. Unable to set status of course with ID: {}", courseId);
                }
            }
            course.setSessionid(form.getSessionId());
            courseList.add(course);
        }
        getCurriculumMap(orgIds);
        populateFacets(form, courseList);

        return courseList;
    }

    /**
     * Creates a list of search requests from the search information supplied by the user
     *
     * @param form - Page form with search information
     * @return The list of search requests to be ran.
     */
    @Override
    public List<SearchRequestInfo> buildSearchRequests(CourseSearchForm form) {
        LOG.info("Start Of Method buildSearchRequests in CourseSearchStrategy: {}",
                System.currentTimeMillis());

        // To keep search from being case specific all text is uppercased
        String query = form.getSearchQuery().toUpperCase();

        // Unchanging query for full text search
        String pureQuery = query;

        //Search Components
        List<String> divisions = new ArrayList<String>();
        List<String> codes;
        List<String> levels;
        List<String> incompleteCodes;
        List<String> completedCodes;
        List<String> completedLevels;
        List<String> keywordDivisions;

        //Search queries
        List<SearchRequestInfo> requests = new ArrayList<SearchRequestInfo>();

        // Extract components from query
        levels = QueryTokenizer.extractCourseLevels(query);
        codes = QueryTokenizer.extractCourseCodes(query);
        QueryTokenizer.extractDivisions(fetchCourseDivisions(), query, divisions, Boolean.parseBoolean(
                ConfigContext.getCurrentContextConfig().getProperty(CourseSearchConstants.COURSE_SEARCH_DIVISION_SPACEALLOWED)));

        // Extract divisions using their keywords.
        if (subjectAreaMap == null || subjectAreaMap.size() == 0) {
            subjectAreaMap = KsapFrameworkServiceLocator.getOrgHelper().getSubjectAreas();
        }
        keywordDivisions = QueryTokenizer.extractDivisionsFromSubjectKeywords(query, subjectAreaMap);

        // remove found levels and codes to find incomplete code components
        for (String level : levels) query = query.replace(level, "");
        for (String code : codes) query = query.replace(code, "");
        incompleteCodes = QueryTokenizer.extractIncompleteCourseCodes(query, divisions);


        completedCodes = QueryTokenizer.extractCompleteCourseCodes(pureQuery, divisions, codes);
        completedLevels = QueryTokenizer.extractCompleteCourseLevels(pureQuery, divisions, levels);

        // Remove found completed levels to not make full text for them
        for (String completedLevel : completedLevels) {
            String division = completedLevel.substring(0, completedLevel.indexOf(','));
            String level = completedLevel.substring(completedLevel.indexOf(',') + 1);
            pureQuery = pureQuery.replace(division + level, "");
        }

        // Remove found completed codes to not make full text for them
        for (String completedCode : completedCodes) {
            String division = completedCode.substring(0, completedCode.indexOf(','));
            String code = completedCode.substring(completedCode.indexOf(',') + 1);
            pureQuery = pureQuery.replace(division + code, "");
        }

        LOG.info("Start of method addComponentRequests of CourseSearchStrategy: {}",
                System.currentTimeMillis());
        Map<String,List<String>> componentMap = new HashMap<String,List<String>>();
        componentMap.put(DIVISIONS_COMPONENTS, divisions);
        componentMap.put(KEYWORDDIVISIONS_COMPONENTS, keywordDivisions);
        componentMap.put(CODES_COMPONENTS, codes);
        componentMap.put(LEVELS_COMPONENTS, levels);
        componentMap.put(INCOMPLETEDCODES_COMPONENTS, incompleteCodes);
        componentMap.put(COMPLETEDCODES_COMPONENTS, completedCodes);
        componentMap.put(COMPLETEDLEVELS_COMPONENTS, completedLevels);
        addComponentRequests(componentMap, requests);
        LOG.info("End of method addComponentRequests of CourseSearchStrategy: {}",
                System.currentTimeMillis());

        LOG.info("Start of method addFullTextSearches of CourseSearchStrategy: {}",
                System.currentTimeMillis());
        addFullTextSearches(pureQuery, requests, form.getSearchTerm());
        LOG.info("End of method addFullTextSearches of CourseSearchStrategy: {}",
                System.currentTimeMillis());

        // Process Current list of Requests into direct search queries
        LOG.info("Count of No of Query Tokens: {}", requests.size());
        requests = adjustSearchRequests(requests, form);
        LOG.info("No of Requests after adjustSearchRequests method: {}",
                requests.size());

        LOG.info("End Of Method buildSearchRequests in CourseSearchStrategy: {}",
                System.currentTimeMillis());

        return requests;
    }

    /**
     * Process the Request adding any additional values or checks
     *
     * @param requests - The list of requests.
     * @param form     - The search form.
     */
    @Override
    public List<SearchRequestInfo> adjustSearchRequests(List<SearchRequestInfo> requests, CourseSearchForm form) {
        LOG.info("Start of method adjustSearchRequests in CourseSearchStrategy: {}",
                System.currentTimeMillis());
        // Process search requests

        // Remove Duplicates
        List<SearchRequestInfo> prunedRequests = new ArrayList<SearchRequestInfo>();
        for (SearchRequestInfo request : requests) {
            if (!prunedRequests.contains(request)) prunedRequests.add(request);
        }
        requests = prunedRequests;

        LOG.info("End of adjustSearchRequests method in CourseSearchStrategy: {}",
                System.currentTimeMillis());

        return requests;
    }

    @Override
    public List<Hit> preformSearch(List<SearchRequestInfo> requests) {
        LOG.info("Start of preformSearch of CourseSearchController: {}",
                System.currentTimeMillis());
        List<Hit> hits = new java.util.LinkedList<Hit>();
        Set<String> seen = new java.util.HashSet<String>();
        String id;
        for (SearchRequestInfo request : requests)
            try {
                for (SearchResultRow row : KsapFrameworkServiceLocator
                        .getCluService()
                        .search(request,
                                KsapFrameworkServiceLocator.getContext()
                                        .getContextInfo()).getRows())
                    if (seen.add(id = KsapHelperUtil.getCellValue(row, "lu.resultColumn.cluId")))
                        hits.add(new Hit(id));
            } catch (MissingParameterException e) {
                throw new IllegalArgumentException(
                        "Invalid course ID or CLU lookup error", e);
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException(
                        "Invalid course ID or CLU lookup error", e);
            } catch (OperationFailedException e) {
                throw new IllegalStateException("CLU lookup error", e);
            } catch (PermissionDeniedException e) {
                throw new IllegalArgumentException("CLU lookup error", e);
            }
        LOG.info("End of preformSearch of CourseSearchController: {}",
                System.currentTimeMillis());
        return hits;
    }

    @Override
    public List<String> getTermsToFilterOn(String termFilter) {
        List<String> termsToFilterOn = new ArrayList<String>();

        if (termFilter.equals(CourseSearchForm.SEARCH_TERM_ANY_ITEM) || termFilter.equals(CourseSearchForm.SEARCH_TERM_SCHEDULED)) {
            // Any Term or Any Scheduled term selected
            List<Term> terms = new ArrayList<Term>();
            List<Term> currentScheduled = KsapFrameworkServiceLocator.getTermHelper().getCurrentTermsWithPublishedSOC();
            List<Term> futureScheduled = KsapFrameworkServiceLocator.getTermHelper().getFutureTermsWithPublishedSOC();
            if (currentScheduled != null) terms.addAll(currentScheduled);
            if (futureScheduled != null) terms.addAll(futureScheduled);
            for (int i = 0; i < terms.size(); i++) {
                termsToFilterOn.add(terms.get(i).getId());
            }
        } else {
            // Single Term selected
            termsToFilterOn.add(termFilter);
        }
        return termsToFilterOn;
    }

    /**
     * Add searches based on the components found in the query string
     *
     * @param componentMap    - A map of the different components to make requests on
     * @param requests        - The list of search requests to be ran
     */
    @Override
    public void addComponentRequests(Map<String, List<String>> componentMap, List<SearchRequestInfo> requests) {

        // Separate components from the map
        List<String> divisions = componentMap.get(DIVISIONS_COMPONENTS);
        List<String> keywordDivisions = componentMap.get(KEYWORDDIVISIONS_COMPONENTS);
        List<String> codes = componentMap.get(CODES_COMPONENTS);
        List<String> levels = componentMap.get(LEVELS_COMPONENTS);
        List<String> incompleteCodes = componentMap.get(INCOMPLETEDCODES_COMPONENTS);
        List<String> completeCodes = componentMap.get(COMPLETEDCODES_COMPONENTS);
        List<String> completeLevels = componentMap.get(COMPLETEDLEVELS_COMPONENTS);

        // Insure no null lists
        if(divisions == null) divisions = Collections.EMPTY_LIST;
        if(keywordDivisions == null) keywordDivisions = Collections.EMPTY_LIST;
        if(codes == null) codes = Collections.EMPTY_LIST;
        if(levels == null) levels = Collections.EMPTY_LIST;
        if(incompleteCodes == null) incompleteCodes = Collections.EMPTY_LIST;
        if(completeCodes == null) completeCodes = Collections.EMPTY_LIST;
        if(completeLevels == null) completeLevels = Collections.EMPTY_LIST;

        // Sort Components
        Collections.sort(divisions);
        Collections.sort(codes);
        Collections.sort(levels);
        Collections.sort(incompleteCodes);

        // Add keyword divisions after sort to maintain order of keyword divisions
        divisions.addAll(keywordDivisions);

        // Combine search requests in execution order
        requests.addAll(addCompletedCodeSearches(completeCodes, divisions, codes));
        requests.addAll(addDivisionAndCodeSearches(divisions, codes));
        requests.addAll(addCompletedLevelSearches(completeLevels, divisions, levels));
        requests.addAll(addDivisionAndLevelSearches(divisions, levels));
        requests.addAll(addIncompleteCodeSearches(incompleteCodes, divisions));
        requests.addAll(addDivisionSearches(divisions));
        requests.addAll(addCodeSearches(codes));
        requests.addAll(addLevelSearches(levels));

    }

    @Override
    public boolean isLimitExceeded() {
        return this.limitExceeded;
    }

    @Override
    public Map<String, String> getCurriculumMap(Set<String> orgIds) {
        Map<String, String> rv = curriculumMapRef == null ? null : curriculumMapRef
                .get();
        if (rv == null) {
            Map<String, String> curriculumMap = new java.util.LinkedHashMap<String, String>();
            ContextInfo contextInfo = KsapFrameworkServiceLocator.getContext()
                    .getContextInfo();
            List<OrgInfo> orgs = new ArrayList<OrgInfo>();

            try {
                if (orgIds != null && orgIds.size() > 0) {
                    orgs = KsapFrameworkServiceLocator.getOrganizationService().getOrgsByIds(new ArrayList<String>(orgIds),
                            contextInfo);
                }
            } catch (DoesNotExistException e) {
                throw new IllegalArgumentException("Org lookup error", e);
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("Org lookup error", e);
            } catch (MissingParameterException e) {
                throw new IllegalArgumentException("Org lookup error", e);
            } catch (OperationFailedException e) {
                throw new IllegalStateException("Org lookup error", e);
            } catch (PermissionDeniedException e) {
                throw new IllegalStateException("Org lookup error", e);
            }

            for (OrgInfo org : orgs) {
                curriculumMap.put(org.getShortName(), org.getLongName());
            }

            curriculumMapRef = new WeakReference<Map<String, String>>(
                    rv = Collections.unmodifiableMap(Collections
                            .synchronizedMap(curriculumMap)));
        }
        return rv;
    }

    @Override
    public Map<String, String> getGenEdMap() {
        Map<String, String> rv = genEdMapRef == null ? null : genEdMapRef
                .get();
        if (rv == null) {
            Map<String, String> genEdMap = new java.util.LinkedHashMap<String, String>();
            genEdMapRef = new WeakReference<Map<String, String>>(
                    rv = Collections.synchronizedMap(genEdMap));
        }
        return rv;
    }

    @Override
    public Map<String, Credit> getCreditMap() {
        Map<String, Credit> rv = creditMapRef == null ? null : creditMapRef
                .get();
        if (rv == null) {
            Map<String, Credit> creditMap = new java.util.LinkedHashMap<String, Credit>();
            String resultScaleKey = CourseSearchConstants.COURSE_SEARCH_SCALE_CREDIT_DEGREE;
            ContextInfo contextInfo = KsapFrameworkServiceLocator.getContext()
                    .getContextInfo();
            List<ResultValuesGroupInfo> resultValuesGroupInfos = null;

            try {
                resultValuesGroupInfos = KsapFrameworkServiceLocator
                        .getLrcService().getResultValuesGroupsByResultScale(
                                resultScaleKey, contextInfo);
            } catch (DoesNotExistException e) {
                throw new IllegalArgumentException("LRC lookup error", e);
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("LRC lookup error", e);
            } catch (MissingParameterException e) {
                throw new IllegalArgumentException("LRC lookup error", e);
            } catch (OperationFailedException e) {
                throw new IllegalStateException("LRC lookup error", e);
            } catch (PermissionDeniedException e) {
                throw new IllegalStateException("LRC lookup error", e);
            }

            Map<String, String> types = CreditsFormatter.getCreditType(resultValuesGroupInfos);

            if ((resultValuesGroupInfos != null)
                    && (resultValuesGroupInfos.size() > 0)) {
                for (ResultValuesGroupInfo resultValuesGroupInfo : resultValuesGroupInfos) {
                    CreditsFormatter.Range range = CreditsFormatter.getRange(resultValuesGroupInfo);
                    CreditImpl credit = new CreditImpl();
                    credit.setId(resultValuesGroupInfo.getKey());
                    credit.setType(CourseSearchItem.CreditType.valueOf(types.get(resultValuesGroupInfo.getTypeKey())));
                    Float tempVlaueHolder = 0F;
                    credit.setMin(tempVlaueHolder);
                    credit.setMax(tempVlaueHolder);
                    if (range.getMin() != null && range.getMax() != null) {
                        credit.setMin(range.getMin().floatValue());
                        credit.setMax(range.getMax().floatValue());
                    }
                    if (range.getMultiple() != null && range.getMultiple().size() > 0) {
                        credit.setMultiple(new float[range.getMultiple().size()]);
                        for (int i = 0; i < range.getMultiple().size(); i++) {
                            credit.setMultiple(i,range.getMultiple().get(i).floatValue());
                        }
                    }
                    credit.setDisplay(CreditsFormatter.formatCredits(range));
                    creditMap.put(credit.getId(), credit);
                }
            }
            creditMapRef = new WeakReference<Map<String, Credit>>(
                    rv = Collections.unmodifiableMap(Collections
                            .synchronizedMap(creditMap)));
        }
        return rv;
    }

    @Override
    public void populateFacets(CourseSearchForm form, List<CourseSearchItem> courses) {
        LOG.info("Start of method populateFacets of CourseSearchController: {}",
                System.currentTimeMillis());
        // Initialize facets.
        CurriculumFacet curriculumFacet = new CurriculumFacet();
        CreditsFacet creditsFacet = new CreditsFacet();
        CourseLevelFacet courseLevelFacet = new CourseLevelFacet();
        GenEduReqFacet genEduReqFacet = new GenEduReqFacet();
        TermsFacet termsFacet = new TermsFacet();

        // Update facet info and code the item.
        for (CourseSearchItem course : courses) {
            curriculumFacet.process(course);
            courseLevelFacet.process(course);
            genEduReqFacet.process(course);
            creditsFacet.process(course);
            termsFacet.process(course);
        }
    }

    @Override
    public Map<String, Comparator<String>> getFacetSort() {
        return FACET_SORT;
    }

    private List<SearchRequestInfo> addLevelSearches(List<String> levels) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        // Create level only search
        for (String level : levels) {
            // Converts "1XX" to "100"
            level = level.substring(0, 1) + "00";
            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_EXACTLEVEL);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_LEVEL, level);
            searches.add(request);
        }

        return searches;
    }

    private List<SearchRequestInfo> addCodeSearches(List<String> codes) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        // Create course code only search
        for (String code : codes) {
            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_EXACTCODE);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_CODE, code);
            searches.add(request);
        }

        return searches;
    }

    private List<SearchRequestInfo> addDivisionSearches(List<String> divisions) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        for (String division : divisions) {
            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_DIVISION);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_DIVISION, division);
            searches.add(request);
        }

        return searches;
    }

    private List<SearchRequestInfo> addDivisionAndCodeSearches(List<String> divisions, List<String> codes) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        List<String> seenDivisions = new ArrayList<String>();
        for (String division : divisions) {
            // Skip if already seen
            if (seenDivisions.contains(division)) continue;
            seenDivisions.add(division);
            List<String> seenCodes = new ArrayList<String>();
            for (String code : codes) {
                // Skip if already seen
                if (seenCodes.contains(code)) continue;
                seenCodes.add(code);
                SearchRequestInfo request = new SearchRequestInfo(
                        CourseSearchConstants.COURSE_SEARCH_TYPE_DIVISIONANDCODE);
                request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_DIVISION, division);
                request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_CODE, code);
                searches.add(request);
            }
        }

        return searches;
    }

    private List<SearchRequestInfo> addDivisionAndLevelSearches(List<String> divisions, List<String> levels) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        List<String> seenDivisions = new ArrayList<String>();
        for (String division : divisions) {
            // Skip if already seen
            if (seenDivisions.contains(division)) continue;
            seenDivisions.add(division);
            List<String> seenLevels = new ArrayList<String>();
            for (String level : levels) {
                // Skip if already seen
                if (seenLevels.contains(level)) continue;
                seenLevels.add(level);

                // Converts "1XX" to "100"
                level = level.substring(0, 1) + "00";

                SearchRequestInfo request = new SearchRequestInfo(
                        CourseSearchConstants.COURSE_SEARCH_TYPE_DIVISIONANDLEVEL);
                request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_DIVISION, division);
                request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_LEVEL, level);
                searches.add(request);
            }
        }

        return searches;
    }

    private List<SearchRequestInfo> addCompletedLevelSearches(List<String> completeLevels, List<String> divisions, List<String> levels) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        // Complete Level searches
        for (String completedLevel : completeLevels) {
            // Break into pieces
            String division = completedLevel.substring(0, completedLevel.indexOf(","));
            String level = completedLevel.substring(completedLevel.indexOf(",") + 1);

            // Remove an entry from the lists of pieces since were using one
            levels.remove(level);
            divisions.remove(division);

            // Create Search
            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_DIVISIONANDLEVEL);
            // Converts "1XX" to "100"
            level = level.substring(0, 1) + "00";
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_DIVISION, division);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_LEVEL, level);
            searches.add(request);

        }

        return searches;
    }

    private List<SearchRequestInfo> addCompletedCodeSearches(List<String> completeCodes, List<String> divisions, List<String> codes) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        // Complete Code searches
        for (String completedCode : completeCodes) {
            // Break into pieces
            String division = completedCode.substring(0, completedCode.indexOf(','));
            String code = completedCode.substring(completedCode.indexOf(',') + 1);

            // Remove an entry from the lists of pieces since were using one
            codes.remove(code);
            divisions.remove(division);

            //Create search
            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_DIVISIONANDCODE);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_DIVISION, division);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_CODE, code);
            searches.add(request);
        }

        return searches;
    }

    private List<SearchRequestInfo> addIncompleteCodeSearches(List<String> incompleteCodes, List<String> divisions) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        // Create course code only search
        List<String> seenIncompleteCodes = new ArrayList<String>();
        for (String incompleteCode : incompleteCodes) {
            // Skip if already seen
            if (seenIncompleteCodes.contains(incompleteCode)) continue;
            seenIncompleteCodes.add(incompleteCode);

            // Remove an entry from the lists of pieces since were using one
            for (int i = 0; i < divisions.size(); i++) {
                String division = divisions.get(i);
                if (incompleteCode.matches(division + "[0-9]+")) {
                    divisions.remove(i);
                    break;
                }
            }

            SearchRequestInfo request = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_COURSECODE);
            request.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_CODE, incompleteCode);
            searches.add(request);
        }

        return searches;
    }

    /**
     * Add the full text searches to the search requests
     *
     * @param query      - The query string
     * @param requests   - The list of search requests to be ran
     * @param searchTerm - The term filter for the search (used for CO searches)
     */
    public void addFullTextSearches(String query, List<SearchRequestInfo> requests, String searchTerm) {
        //find all tokens in the query string
        List<QueryTokenizer.Token> tokens = QueryTokenizer.tokenize(query);

        List<SearchRequestInfo> titleSearches = addTitleSearches(tokens, searchTerm);
        List<SearchRequestInfo> descriptionSearches = addDescriptionSearches(tokens, searchTerm);

        requests.addAll(titleSearches);
        requests.addAll(descriptionSearches);
    }

    private List<SearchRequestInfo> addTitleSearches(List<QueryTokenizer.Token> tokens, String searchTerm) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        for (QueryTokenizer.Token token : tokens) {
            // Convert token to its correct text
            String queryText = QueryTokenizer.cleanToken(token);

            // Skip if query is less than 3 characters
            if (queryText != null && queryText.length() < 3) continue;

            // Add course title search
            SearchRequestInfo requestTitle = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_TITLE);
            requestTitle.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_QUERYTEXT, queryText.trim());
            searches.add(requestTitle);

            // Add course offering title search
            SearchRequestInfo requestOffering = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_CO_TITLE);
            requestOffering.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_QUERYTEXT, queryText.trim());
            requestOffering.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_TERMLIST, getTermsToFilterOn(searchTerm));
            searches.add(requestOffering);
        }

        return searches;
    }

    private List<SearchRequestInfo> addDescriptionSearches(List<QueryTokenizer.Token> tokens, String searchTerm) {
        List<SearchRequestInfo> searches = new ArrayList<SearchRequestInfo>();

        for (QueryTokenizer.Token token : tokens) {
            // Convert token to its correct text
            String queryText = QueryTokenizer.cleanToken(token);

            // Skip if query is less than 3 characters
            if (queryText != null && queryText.length() < 3) continue;

            // Add course description search
            SearchRequestInfo requestDescription = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_DESCRIPTION);
            requestDescription.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_QUERYTEXT, queryText.trim());
            searches.add(requestDescription);

            // Add course offering description search
            SearchRequestInfo requestOfferingDescr = new SearchRequestInfo(
                    CourseSearchConstants.COURSE_SEARCH_TYPE_CO_DESCRIPTION);
            requestOfferingDescr.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_QUERYTEXT, queryText.trim());
            requestOfferingDescr.addParam(CourseSearchConstants.COURSE_SEARCH_PARAM_TERMLIST, getTermsToFilterOn(searchTerm));
            searches.add(requestOfferingDescr);
        }

        return searches;
    }

    private List<CourseSearchItemImpl> getCoursesInfo(List<String> courseIDs) {
        LOG.info("Start of method getCourseInfo of CourseSearchController: {}",
                System.currentTimeMillis());
        List<CourseSearchItemImpl> listOfCourses = new ArrayList<CourseSearchItemImpl>();
        SearchRequestInfo request = new SearchRequestInfo("ksap.course.info");
        request.addParam("courseIDs", courseIDs);
        SearchResult result;
        try {
            result = KsapFrameworkServiceLocator.getCluService().search(
                    request,
                    KsapFrameworkServiceLocator.getContext().getContextInfo());
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("CLU lookup error", e);
        } catch (PermissionDeniedException e) {
            throw new IllegalArgumentException("CLU lookup error", e);
        }
        if ((result != null) && (!result.getRows().isEmpty())) {
            for (String courseId : courseIDs) {
                for (SearchResultRow row : result.getRows()) {
                    String id = KsapHelperUtil.getCellValue(row, "course.id");
                    if (id.equals(courseId)) {
                        CourseSearchItemImpl course = new CourseSearchItemImpl();
                        course.setCourseId(id);
                        course.setSubject(KsapHelperUtil.getCellValue(row, "course.subject"));
                        course.setNumber(KsapHelperUtil.getCellValue(row, "course.number"));
                        course.setLevel(KsapHelperUtil.getCellValue(row, "course.level"));
                        course.setCourseName(KsapHelperUtil.getCellValue(row, "course.name"));
                        course.setCode(KsapHelperUtil.getCellValue(row, "course.code"));
                        course.setVersionIndependentId(KsapHelperUtil.getCellValue(row, "course.versionIndId"));
                        String cellValue = KsapHelperUtil.getCellValue(row, "course.credits");
                        Credit credit = getCreditByID(cellValue);
                        if (credit != null) {
                            course.setCreditMin(credit.getMin());
                            course.setCreditMax(credit.getMax());
                            course.setCreditType(credit.getType());
                            course.setMultipleCredits(credit.getMultiple());
                            course.setCredit(credit.getDisplay());
                        }
                        listOfCourses.add(course);
                        break;
                    }
                }
            }
        }

        loadScheduledTerms(listOfCourses);
        loadTermsOffered(listOfCourses, courseIDs);
        loadGenEduReqs(listOfCourses);

        LOG.info("End of method getCourseInfo of CourseSearchController: {}",
                System.currentTimeMillis());
        return listOfCourses;
    }

    /**
     * Filters the result course ids from the search based on the term filter
     *
     * @param courseIds  - Full list of course ids found by the search
     * @param termFilter - Term to filter by
     * @return A list of course ids with offerings matching the selected filter
     */
    private List<String> termfilterCourseIds(List<String> courseIds, String termFilter) {
        LOG.info("Start of method termfilterCourseIds of CourseSearchController: {}",
                System.currentTimeMillis());

        // If any term option is select return list as is, no filtering needed.
        if (termFilter.equals(CourseSearchForm.SEARCH_TERM_ANY_ITEM)) {
            return courseIds;
        }

        // Build list of valid terms based on the filter
        List<Term> terms = new ArrayList<Term>();
        if (termFilter.equals(CourseSearchForm.SEARCH_TERM_SCHEDULED)) {
            // Any Scheduled term selected
            List<Term> currentScheduled = KsapFrameworkServiceLocator.getTermHelper().getCurrentTermsWithPublishedSOC();
            List<Term> futureScheduled = KsapFrameworkServiceLocator.getTermHelper().getFutureTermsWithPublishedSOC();
            if (currentScheduled != null) terms.addAll(currentScheduled);
            if (futureScheduled != null) terms.addAll(futureScheduled);
        } else {
            // Single Term selected
            terms.add(KsapFrameworkServiceLocator.getTermHelper().getTerm(termFilter));
        }
        List<String> filteredIds = new ArrayList<String>();
        try {

            // Search for all course offerings of search results in terms
            Predicate termPredicates[] = KsapHelperUtil.getTermPredicates(terms);
            Predicate coursePredicates[] = KsapHelperUtil.getCourseIdPredicates(courseIds);
            QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(or(coursePredicates),
                    or(termPredicates), equal("luiType", LuiServiceConstants.COURSE_OFFERING_TYPE_KEY));
            List<CourseOfferingInfo> offerings = KsapFrameworkServiceLocator.getCourseOfferingService()
                    .searchForCourseOfferings(query, KsapFrameworkServiceLocator.getContext().getContextInfo());

            // Fill filtered id list
            for (String courseId : courseIds) {
                for (CourseOfferingInfo offering : offerings) {
                    if (courseId.equals(offering.getCourseId())) {
                        filteredIds.add(courseId);
                        break;
                    }
                }
            }
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("ATP lookup failed", e);
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException("ATP lookup failed", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("ATP lookup failed", e);
        } catch (PermissionDeniedException e) {
            throw new IllegalStateException("ATP lookup failed", e);
        }
        LOG.info("End of method termfilterCourseIds of CourseSearchController: {}",
                System.currentTimeMillis());
        return filteredIds;

    }

    /**
     * Load scheduling information for courses based on their course offerings
     *
     * @param courses - List of courses to load information for.
     */
    private void loadScheduledTerms(List<CourseSearchItemImpl> courses) {
        LOG.info("Start of method loadScheduledTerms of CourseSearchController: {}",
                System.currentTimeMillis());

        List<CourseOfferingInfo> offerings = KsapFrameworkServiceLocator.getCourseHelper()
                .getCourseOfferingsForCourses(new ArrayList<CourseSearchItem>(courses));

        // Load scheduling data into course results from there course offerings
        for (CourseOfferingInfo offering : offerings) {
            for (CourseSearchItemImpl course : courses) {
                if (course.getCourseId().equals(offering.getCourseId())) {
                    // Avoid Duplicates
                    if (!course.getScheduledTermsList().contains(offering.getTermId())) {
                        course.addScheduledTerm(offering.getTermId());
                    }
                }
            }
        }

        LOG.info("End of method loadScheduledTerms of CourseSearchController: {}",
                System.currentTimeMillis());
    }

    /**
     * Loads projected term information for the courses.
     * This information is found in the KSLU_CLU_ATP_TYPE_KEY table
     *
     * @param courses   - The list of course information for the courses
     * @param courseIDs - The list of course ids for the courses.
     */
    private void loadTermsOffered(List<CourseSearchItemImpl> courses, final List<String> courseIDs) {
        LOG.info("Start of method loadTermsOffered of CourseSearchController: {}",
                System.currentTimeMillis());

        // Search for projected offered terms for the courses
        SearchRequestInfo request = new SearchRequestInfo(
                "ksap.course.info.atp");
        request.addParam("courseIDs", courseIDs);
        SearchResult result;
        try {
            result = KsapFrameworkServiceLocator.getCluService().search(
                    request,
                    KsapFrameworkServiceLocator.getContext().getContextInfo());
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("CLU lookup error", e);
        } catch (PermissionDeniedException e) {
            throw new IllegalArgumentException("CLU lookup error", e);
        }

        // Process the found term information
        if (result == null) {
            return;
        }

        Map<String, List<String>> offeredMap = new HashMap<String, List<String>>();

        for (SearchResultRow row : result.getRows()) {
            String courseId = KsapHelperUtil.getCellValue(row, "course.key");
            String type = KsapHelperUtil.getCellValue(row, "atp.id");
            if (offeredMap.containsKey(courseId)) {
                offeredMap.get(courseId).add(type);
            } else {
                offeredMap.put(courseId, new ArrayList<String>());
                offeredMap.get(courseId).add(type);
            }
        }

        for (CourseSearchItemImpl course : courses) {
            if (offeredMap.containsKey(course.getCourseId())) {
                List<String> termsOffered = offeredMap.get(course.getCourseId());
                Collections.sort(termsOffered);
                course.setTermInfoList(termsOffered);
            }
        }

        LOG.info("End of method loadTermsOffered of CourseSearchController: {}",
                System.currentTimeMillis());
    }

    /**
     * Loads the gen ed information for the courses.
     * Gen Ed information is store as course sets that can be returned using the independent version id
     *
     * @param courses - The list of course inforamtion for the courses
     */
    private void loadGenEduReqs(List<CourseSearchItemImpl> courses) {
        LOG.info("Start of method loadGenEduReqs of CourseSearchController: {}",
                System.currentTimeMillis());

        // Search for gen ed requirements
        SearchRequestInfo request = new SearchRequestInfo(
                "ksap.course.info.gened");

        // Create a list of version Ids for the search
        List<String> versionIndIds = new ArrayList<String>();
        for (CourseSearchItemImpl course : courses) {
            versionIndIds.add(course.getVersionIndependentId());
        }
        request.addParam("courseIDs", versionIndIds);

        // Search for the requirements
        SearchResult result;
        try {
            result = KsapFrameworkServiceLocator.getCluService().search(
                    request,
                    KsapFrameworkServiceLocator.getContext().getContextInfo());
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException(
                    "Invalid course ID or CLU lookup error", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("CLU lookup error", e);
        } catch (PermissionDeniedException e) {
            throw new IllegalArgumentException("CLU lookup error", e);
        }

        // Return if no entries found
        if (result == null) {
            return;
        }

        // Create a map of the gen ed entries to its related course
        Map<String, List<String>> genEdResults = new HashMap<String, List<String>>();
        for (SearchResultRow row : result.getRows()) {
            String genEd = KsapHelperUtil.getCellValue(row, "gened.code");
            String id = KsapHelperUtil.getCellValue(row, "course.owner");
            String genEdName = KsapHelperUtil.getCellValue(row, "gened.name");
            if (genEdResults.containsKey(id)) {
                genEdResults.get(id).add(genEd);
            } else {
                List<String> newEntry = new ArrayList<String>();
                newEntry.add(genEd);
                genEdResults.put(id, newEntry);
            }
            getGenEdMap().put(genEd, genEdName);
        }

        // Fill in the course information
        for (CourseSearchItemImpl course : courses) {
            if (genEdResults.containsKey(course.getVersionIndependentId())) {
                List<String> reqs = genEdResults.get(course.getVersionIndependentId());
                course.setGenEduReqs(reqs);
            }
        }

        LOG.info("End of method loadGenEduReqs of CourseSearchController: {}",
                System.currentTimeMillis());
    }

    /**
     * Creates a map relating the course to its status in the plan.
     *
     * @param studentID - Id of the user running the search
     * @return A map of the plan state for a course.
     */
    private Map<String, String> getCourseStatusMap(String studentID) {
        LOG.info("Start of method getCourseStatusMap of CourseSearchController: {}",
                System.currentTimeMillis());
        AcademicPlanService academicPlanService = KsapFrameworkServiceLocator
                .getAcademicPlanService();

        ContextInfo context = KsapFrameworkServiceLocator.getContext().getContextInfo();

        String planTypeKey = AcademicPlanServiceConstants.LEARNING_PLAN_TYPE_PLAN;

        Map<String, String> savedCourseSet = new HashMap<String, String>();

        /*
		 * For each plan item in each plan set the state based on the type.
		 */
        // Find list of learning plans
        List<LearningPlanInfo> learningPlanList;
        try {
            learningPlanList = academicPlanService
                    .getLearningPlansForStudentByType(studentID, planTypeKey,
                            context);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("LP lookup error", e);
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException("LP lookup error", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("LP lookup error", e);
        } catch (PermissionDeniedException e) {
            throw new IllegalStateException("LP lookup permission error", e);
        }

        // Process list of learning plan's entries
        for (LearningPlan learningPlan : learningPlanList) {
            String learningPlanID = learningPlan.getId();
            List<PlanItemInfo> planItemList;
            try {
                planItemList = academicPlanService.getPlanItemsInPlan(
                        learningPlanID, context);
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("LP lookup error", e);
            } catch (MissingParameterException e) {
                throw new IllegalArgumentException("LP lookup error", e);
            } catch (OperationFailedException e) {
                throw new IllegalStateException("LP lookup error", e);
            } catch (PermissionDeniedException e) {
                throw new IllegalStateException("LP lookup permission error", e);
            }

            // Process plan items in learning plan
            for (PlanItem planItem : planItemList) {
                String courseID = planItem.getRefObjectId();

                //4 possible states: none, planned, saved, or both

                String state = NONE; //initial default state

                if (planItem.getCategory().equals(AcademicPlanServiceConstants.ItemCategory.WISHLIST)) {
                    state = SAVED;
                } else if (planItem.getCategory().equals(
                        AcademicPlanServiceConstants.ItemCategory.PLANNED)
                        || planItem.getCategory().equals(
                        AcademicPlanServiceConstants.ItemCategory.BACKUP)
                        || planItem.getCategory().equals(
                        AcademicPlanServiceConstants.ItemCategory.CART)) {
                    state = PLANNED;
                } else {
                    throw new RuntimeException("Unknown plan item type.");
                }

                if (!savedCourseSet.containsKey(courseID)) {
                    // First time through the loop, add state from above directly
                    savedCourseSet.put(courseID, state);
                } else if (savedCourseSet.get(courseID).equals(NONE)) {
                    // Was through once already, didn't get saved or planned
                    savedCourseSet.put(courseID, state);
                } else if ((savedCourseSet.get(courseID).equals(SAVED) && state.equals(PLANNED))
                        || (savedCourseSet.get(courseID).equals(PLANNED) && state.equals(SAVED))) {
                    //previously had saved OR planned... now it must have both
                    savedCourseSet.put(courseID, SAVED_AND_PLANNED);
                }
            }
        }
        LOG.info("End of method getCourseStatusMap of CourseSearchController: {}",
                System.currentTimeMillis());
        return savedCourseSet;
    }

    /**
     * Retrieves the credit for s specific id
     *
     * @param id - Id of the credit value in the map
     * @return - Credit object for the specific id in the map or a unknown default
     */
    private Credit getCreditByID(String id) {
        Map<String, Credit> creditMap = getCreditMap();
        Credit credit = creditMap.get(id);
        return credit == null ? creditMap.get("u") : credit;
    }

    /**
     * Creates a map of divisions values with spaces removed to the value with spaces.
     * Store both trimmed and original, because source data is sometimes space padded.
     *
     * @return Map Division w/out spaces to Divsion w/ spaces
     */
    private Map<String, String> fetchCourseDivisions() {
        Map<String, String> map = new java.util.LinkedHashMap<String, String>();
        for (String div : getDivisionCodes())
            map.put(div.trim().replaceAll("\\s+", ""), div);
        return map;
    }

    private List<String> getDivisionCodes() {
        ContextInfo context = KsapFrameworkServiceLocator.getContext()
                .getContextInfo();
        CluService cluService = KsapFrameworkServiceLocator.getCluService();
        SearchRequestInfo request = new SearchRequestInfo(
                "ksap.distinct.clu.divisions");
        SearchResult result;
        try {
            result = cluService.search(request, context);
        } catch (MissingParameterException e) {
            throw new IllegalArgumentException("Error in CLU division search",
                    e);
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("Error in CLU division search",
                    e);
        } catch (OperationFailedException e) {
            throw new IllegalArgumentException("Error in CLU division search",
                    e);
        } catch (PermissionDeniedException e) {
            throw new IllegalArgumentException("Error in CLU division search",
                    e);
        }
        List<? extends SearchResultRow> rr = result.getRows();
        List<String> rv = new java.util.ArrayList<String>(rr.size());
        for (SearchResultRow row : rr)
            for (SearchResultCell cell : row.getCells())
                rv.add(cell.getValue());
        return rv;
    }
}
