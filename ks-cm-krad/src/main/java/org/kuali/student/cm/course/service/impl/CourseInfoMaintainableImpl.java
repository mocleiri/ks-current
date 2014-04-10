/**
 * Copyright 2005-2013 The Kuali Foundation Licensed under the
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
 */
package org.kuali.student.cm.course.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.dto.AgendaEditor;
import org.kuali.rice.krms.dto.AgendaTypeInfo;
import org.kuali.rice.krms.dto.PropositionEditor;
import org.kuali.rice.krms.dto.RuleEditor;
import org.kuali.rice.krms.dto.TemplateInfo;
import org.kuali.rice.krms.dto.TermParameterEditor;
import org.kuali.rice.krms.service.RuleViewHelperService;
import org.kuali.rice.krms.service.impl.RuleEditorMaintainableImpl;
import org.kuali.rice.krms.tree.RuleViewTreeBuilder;
import org.kuali.rice.krms.tree.node.CompareTreeNode;
import org.kuali.rice.krms.tree.node.RuleEditorTreeNode;
import org.kuali.rice.krms.tree.node.TreeNode;
import org.kuali.rice.krms.util.NaturalLanguageHelper;
import org.kuali.student.cm.course.controller.CourseController;
import org.kuali.student.cm.course.form.ActivityInfoWrapper;
import org.kuali.student.cm.course.form.CluInstructorInfoWrapper;
import org.kuali.student.cm.course.form.CollaboratorWrapper;
import org.kuali.student.cm.course.form.CourseCreateUnitsContentOwner;
import org.kuali.student.cm.course.form.CourseInfoWrapper;
import org.kuali.student.cm.course.form.CourseJointInfoWrapper;
import org.kuali.student.cm.course.form.CourseRuleManagementWrapper;
import org.kuali.student.cm.course.form.FormatInfoWrapper;
import org.kuali.student.cm.course.form.LoCategoryInfoWrapper;
import org.kuali.student.cm.course.form.LoDisplayInfoWrapper;
import org.kuali.student.cm.course.form.LoDisplayWrapperModel;
import org.kuali.student.cm.course.form.OrganizationInfoWrapper;
import org.kuali.student.cm.course.form.OutcomeReviewSection;
import org.kuali.student.cm.course.form.ResultValueKeysWrapper;
import org.kuali.student.cm.course.form.ResultValuesGroupInfoWrapper;
import org.kuali.student.cm.course.form.ReviewProposalDisplay;
import org.kuali.student.cm.course.form.SubjectCodeWrapper;
import org.kuali.student.cm.course.service.CourseInfoMaintainable;
import org.kuali.student.cm.course.service.util.CourseCodeSearchUtil;
import org.kuali.student.cm.course.service.util.LoCategorySearchUtil;
import org.kuali.student.cm.course.service.util.OrganizationSearchUtil;
import org.kuali.student.cm.maintenance.CMMaintainable;
import org.kuali.student.common.collection.KSCollectionUtils;
import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.core.krms.tree.KSRuleViewTreeBuilder;
import org.kuali.student.core.organization.ui.client.mvc.model.MembershipInfo;
import org.kuali.student.core.workflow.ui.client.widgets.WorkflowUtilities;
import org.kuali.student.lum.lu.ui.course.keyvalues.OrgsBySubjectCodeValuesFinder;
import org.kuali.student.lum.lu.ui.krms.dto.LUAgendaEditor;
import org.kuali.student.lum.lu.ui.krms.dto.LURuleEditor;
import org.kuali.student.lum.lu.ui.krms.tree.LURuleViewTreeBuilder;
import org.kuali.student.lum.program.client.ProgramConstants;
import org.kuali.student.r1.core.personsearch.service.impl.QuickViewByGivenName;
import org.kuali.student.r1.core.proposal.ProposalConstants;
import org.kuali.student.r1.core.subjectcode.service.SubjectCodeService;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.DtoConstants;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.constants.LearningObjectiveServiceConstants;
import org.kuali.student.r2.common.util.date.DateFormatters;
import org.kuali.student.r2.common.util.date.KSDateTimeFormatter;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.service.AtpService;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.class1.type.service.TypeService;
import org.kuali.student.r2.core.comment.dto.CommentInfo;
import org.kuali.student.r2.core.comment.dto.DecisionInfo;
import org.kuali.student.r2.core.comment.service.CommentService;
import org.kuali.student.r2.core.constants.AtpServiceConstants;
import org.kuali.student.r2.core.constants.CommentServiceConstants;
import org.kuali.student.r2.core.constants.EnumerationManagementServiceConstants;
import org.kuali.student.r2.core.constants.KSKRMSServiceConstants;
import org.kuali.student.r2.core.constants.ProposalServiceConstants;
import org.kuali.student.r2.core.constants.TypeServiceConstants;
import org.kuali.student.r2.core.enumerationmanagement.dto.EnumeratedValueInfo;
import org.kuali.student.r2.core.enumerationmanagement.service.EnumerationManagementService;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.proposal.dto.ProposalInfo;
import org.kuali.student.r2.core.proposal.service.ProposalService;
import org.kuali.student.r2.core.search.dto.SearchParamInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultCellInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.service.SearchService;
import org.kuali.student.r2.lum.clu.dto.CluInstructorInfo;
import org.kuali.student.r2.lum.clu.service.CluService;
import org.kuali.student.r2.lum.course.dto.ActivityInfo;
import org.kuali.student.r2.lum.course.dto.CourseCrossListingInfo;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.course.dto.CourseVariationInfo;
import org.kuali.student.r2.lum.course.dto.FormatInfo;
import org.kuali.student.r2.lum.course.dto.LoDisplayInfo;
import org.kuali.student.r2.lum.course.service.CourseService;
import org.kuali.student.r2.lum.course.service.assembler.CourseAssemblerConstants;
import org.kuali.student.r2.lum.lo.service.LearningObjectiveService;
import org.kuali.student.r2.lum.lrc.dto.ResultValueRangeInfo;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.kuali.student.r2.lum.lrc.service.LRCService;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;
import org.kuali.student.r2.lum.util.constants.CourseServiceConstants;
import org.kuali.student.r2.lum.util.constants.LrcServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base view helper service for both create and edit course info presentations.
 *
 * @author OpenCollab/rSmart KRAD CM Conversion Alliance!
 */
public class CourseInfoMaintainableImpl extends RuleEditorMaintainableImpl implements CourseInfoMaintainable, RuleViewHelperService, CMMaintainable {


    private static final Logger LOG = LoggerFactory.getLogger(CourseInfoMaintainableImpl.class);

    protected transient static final String DEFAULT_REQUIRED_WORKFLOW_MODE = "Submit";

    protected transient static final String CREDIT_COURSE_CLU_TYPE_KEY = "kuali.lu.typeKey.CreditCourse";

    private static final long serialVersionUID = 1338662637708570500L;

    private RuleViewHelperService ruleViewHelperService = new CourseRuleViewHelperServiceImpl();

    private transient OrganizationService organizationService;

    private transient SearchService searchService;

    private transient SubjectCodeService subjectCodeService;

    private transient CluService cluService;

    private transient LearningObjectiveService learningObjectiveService;

    private transient CourseService courseService;

    private transient KSRuleViewTreeBuilder viewTreeBuilder;

    private CourseRuleManagementWrapper courseRuleManagementWrapper;

    private transient NaturalLanguageHelper nlHelper;

    private transient CommentService commentService;

    private transient IdentityService identityService;

    private transient ProposalService proposalService;

    private transient TypeService typeService;

    private transient LRCService lrcService;

    private transient EnumerationManagementService enumerationManagementService;

    private transient AtpService atpService;

    /**
     * Method called when queryMethodToCall is executed for Administering Organizations in order to suggest back to the user an Administering Organization
     *
     * @param organizationName
     * @see CourseInfoMaintainable#getOrganizationsForSuggest(String)
     */
    public List<OrganizationInfoWrapper> getOrganizationsForSuggest(final String organizationName) {
        return OrganizationSearchUtil.searchForOrganizations(organizationName, getOrganizationService());
    }

    private SearchResultInfo getInstructorsSearchResult(String nameOrId, Boolean isName) throws Exception {

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();
        SearchParamInfo displayNameParam = new SearchParamInfo();
        if (isName)
            displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
        else
            displayNameParam.setKey(QuickViewByGivenName.ID_PARAM);
        displayNameParam.getValues().add(nameOrId);
        queryParamValueList.add(displayNameParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setNeededTotalResults(false);
        searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);

        SearchResultInfo searchResult = null;
        searchResult = getSearchService().search(searchRequest, ContextUtils.getContextInfo());

        return searchResult;
    }

    private List<CluInstructorInfoWrapper> getInstructorsFromSearchResult(SearchResultInfo searchResult) {

        List<CluInstructorInfoWrapper> cluInstructorInfoDisplays = new ArrayList<CluInstructorInfoWrapper>();
        for (SearchResultRowInfo result : searchResult.getRows()) {
            List<SearchResultCellInfo> cells = result.getCells();
            CluInstructorInfoWrapper cluInstructorInfoDisplay = new CluInstructorInfoWrapper();
            for (SearchResultCellInfo cell : cells) {
                if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
                    cluInstructorInfoDisplay.setGivenName(cell.getValue());
                } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                    cluInstructorInfoDisplay.setPersonId(cell.getValue());
                } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                    cluInstructorInfoDisplay.setId(cell.getValue());
                } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                    cluInstructorInfoDisplay.setPrincipalName(cell.getValue());
                } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                    cluInstructorInfoDisplay.setDisplayName(cell.getValue());
                }
            }
            cluInstructorInfoDisplays.add(cluInstructorInfoDisplay);
        }
        return cluInstructorInfoDisplays;
    }

    public List<CluInstructorInfoWrapper> getInstructorsbyId(String id) {
        List<CluInstructorInfoWrapper> cluInstructorInfoDisplays = new ArrayList<CluInstructorInfoWrapper>();
        SearchResultInfo searchResult = null;
        try {
            searchResult = getInstructorsSearchResult(id, false);
            cluInstructorInfoDisplays = getInstructorsFromSearchResult(searchResult);
        } catch (Exception e) {
            LOG.error("An error occurred in the getInstructorsForSuggest method", e);
        }
        return cluInstructorInfoDisplays;
    }


    /**
     * @see CourseInfoMaintainable#getInstructorsForSuggest(String)
     */
    public List<CluInstructorInfoWrapper> getInstructorsForSuggest(
            String instructorName) {
        List<CluInstructorInfoWrapper> cluInstructorInfoDisplays = new ArrayList<CluInstructorInfoWrapper>();

        SearchResultInfo searchResult = null;
        try {
            searchResult = getInstructorsSearchResult(instructorName, true);
            cluInstructorInfoDisplays = getInstructorsFromSearchResult(searchResult);
        } catch (Exception e) {
            LOG.error("An error occurred in the getInstructorsForSuggest method", e);
        }
        return cluInstructorInfoDisplays;
    }


    public LoDisplayWrapperModel getLoDisplayWrapperModel() {
        if (loDisplayWrapperModel == null) {
            loDisplayWrapperModel = new LoDisplayWrapperModel();
        }
        return loDisplayWrapperModel;
    }

    public void setLoDisplayWrapperModel(LoDisplayWrapperModel loDisplayWrapperModel) {
        this.loDisplayWrapperModel = loDisplayWrapperModel;
    }

    private LoDisplayWrapperModel loDisplayWrapperModel;

    /**
     * @see CourseInfoMaintainable#getSubjectCodesForSuggest(String)
     */
    public List<SubjectCodeWrapper> getSubjectCodesForSuggest(String subjectCode) {
        List<SubjectCodeWrapper> retrievedCodes = new ArrayList<SubjectCodeWrapper>();

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo codeParam = new SearchParamInfo();
        codeParam.setKey(CourseServiceConstants.SUBJECTCODE_CODE_PARAM);
        List<String> codeValues = new ArrayList<String>();
        codeValues.add(subjectCode);
        codeParam.setValues(codeValues);

        queryParamValueList.add(codeParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(CourseServiceConstants.SUBJECTCODE_GENERIC_SEARCH);
        searchRequest.setParams(queryParamValueList);

        SearchResultInfo searchResult = null;
        try {
            searchResult = getSubjectCodeService().search(searchRequest, ContextUtils.getContextInfo());
            for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                String id = "";
                String code = "";
                for (SearchResultCellInfo cell : cells) {
                    if (CourseServiceConstants.SUBJECTCODE_ID_RESULT.equals(cell.getKey())) {
                        id = cell.getValue();
                    } else if (CourseServiceConstants.SUBJECTCODE_CODE_RESULT.equals(cell.getKey())) {
                        code = cell.getValue();
                    }
                }
                retrievedCodes.add(new SubjectCodeWrapper(id, code));
            }
        } catch (Exception e) {
            LOG.error("An error occurred retrieving the SubjectCodeDisplay", e);
        }

        return retrievedCodes;
    }

    @Override
    public List<CourseJointInfoWrapper> searchForJointOfferingCourses(String courseNumber) {
        return CourseCodeSearchUtil.searchForCourseJointInfos(courseNumber, getCluService());
    }

    @Override
    public List<LoCategoryInfoWrapper> searchForLoCategories(String categoryName) {
        return LoCategorySearchUtil.searchForLoCategories(categoryName, getLearningObjectiveService());
    }

    /**
     */
    public List<CollaboratorWrapper> getCollaboratorWrappersSuggest(
            String principalId) {
        List<CollaboratorWrapper> listCollaboratorWrappers = new ArrayList<CollaboratorWrapper>();

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo displayNameParam = new SearchParamInfo();
        displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
        displayNameParam.getValues().add(principalId);
        queryParamValueList.add(displayNameParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setNeededTotalResults(false);
        searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);

        SearchResultInfo searchResult = null;
        try {
            searchResult = getSearchService().search(searchRequest, ContextUtils.getContextInfo());
            for (SearchResultRowInfo result : searchResult.getRows()) {
                List<SearchResultCellInfo> cells = result.getCells();
                CollaboratorWrapper theCollaboratorWrapper = new CollaboratorWrapper();
                for (SearchResultCellInfo cell : cells) {
                    if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
                        theCollaboratorWrapper.setGivenName(cell.getValue());
                    } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                        theCollaboratorWrapper.setPersonID(cell.getValue());
                    } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                        theCollaboratorWrapper.setPrincipalId(cell.getValue());
                    } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                        theCollaboratorWrapper.setPrincipalName(cell.getValue());
                    } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                        theCollaboratorWrapper.setDisplayName(cell.getValue());
                    }
                }
                listCollaboratorWrappers.add(theCollaboratorWrapper);
            }
        } catch (Exception e) {
            LOG.error("Error retrieving Personel search List", e);
            //throw new RuntimeException();
        }

        return listCollaboratorWrappers;
    }


    @Override
    public String getDocumentTitle(MaintenanceDocument document) {
        return document.getDocumentHeader().getDocumentDescription();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public Tree<RuleEditorTreeNode, String> getEditTree() {
        return getCourseRuleManagementWrapper().getEditTree();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public Tree<TreeNode, String> getPreviewTree() {
        return getCourseRuleManagementWrapper().getPreviewTree();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public Tree<TreeNode, String> getViewTree() {
        return getCourseRuleManagementWrapper().getViewTree();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public String getSelectedKey() {
        return getCourseRuleManagementWrapper().getSelectedKey();
    }


    public CourseRuleManagementWrapper getCourseRuleManagementWrapper() {
        if (courseRuleManagementWrapper == null) {
            courseRuleManagementWrapper = new CourseRuleManagementWrapper();
        }
        return courseRuleManagementWrapper;
    }

    public void setCourseRuleManagementWrapper(CourseRuleManagementWrapper courseRuleManagementWrapper) {
        this.courseRuleManagementWrapper = courseRuleManagementWrapper;
    }


    /**
     * Specifically created for KRMS purposes.
     */
    public void setSelectedKey(String selectedKey) {
        getCourseRuleManagementWrapper().setSelectedKey(selectedKey);
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public String getCutKey() {
        return getCourseRuleManagementWrapper().getCutKey();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public void setCutKey(String cutKey) {
        getCourseRuleManagementWrapper().setCutKey(cutKey);
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public String getCopyKey() {
        return getCourseRuleManagementWrapper().getCopyKey();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public void setCopyKey(String copyKey) {
        getCourseRuleManagementWrapper().setCopyKey(copyKey);
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public String getLogicArea() {
        return getCourseRuleManagementWrapper().getLogicArea();
    }

    /**
     * Specifically created for KRMS purposes.
     */
    public void setLogicArea(String logicArea) {
        getCourseRuleManagementWrapper().setLogicArea(logicArea);
    }

    @Override
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        if (addLine instanceof CluInstructorInfoWrapper) {
            CluInstructorInfoWrapper instructorWrapper = (CluInstructorInfoWrapper) addLine;

            if (model instanceof MaintenanceDocumentForm) {
                MaintenanceDocumentForm modelForm = (MaintenanceDocumentForm) model;
                CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) modelForm.getDocument().getNewMaintainableObject().getDataObject();
                CourseInfoMaintainable courseInfoMaintainable = (CourseInfoMaintainable) modelForm.getDocument().getNewMaintainableObject();
                for (CluInstructorInfoWrapper instructor : courseInfoWrapper.getInstructorWrappers()) {
                    if (instructor.getDisplayName().equals(instructorWrapper.getDisplayName())) {
                        return false; //already in the list
                    }
                }
            }
            return StringUtils.isNotEmpty(instructorWrapper.getDisplayName()) ? true : false;
        }
        if (addLine instanceof CollaboratorWrapper) {
            CollaboratorWrapper collaboratorWrapper = (CollaboratorWrapper) addLine;

            if (model instanceof MaintenanceDocumentForm) {
                MaintenanceDocumentForm modelForm = (MaintenanceDocumentForm) model;
                CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) modelForm.getDocument().getNewMaintainableObject().getDataObject();
                CourseInfoMaintainable courseInfoMaintainable = (CourseInfoMaintainable) modelForm.getDocument().getNewMaintainableObject();
                for (CollaboratorWrapper collaboratorAuthor : courseInfoWrapper.getCollaboratorWrappers()) {
                    if (collaboratorAuthor.getDisplayName().equals(collaboratorWrapper.getDisplayName())) {
                        return false; //already in the list
                    }
                }
            }
            return StringUtils.isNotEmpty(collaboratorWrapper.getDisplayName()) ? true : false;
        } else if (addLine instanceof CourseCreateUnitsContentOwner) {
            MaintenanceDocumentForm modelForm = (MaintenanceDocumentForm) model;
            CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) modelForm.getDocument().getNewMaintainableObject().getDataObject();

            for (CourseCreateUnitsContentOwner unitsContentOwner : courseInfoWrapper.getUnitsContentOwner()) {
                if (StringUtils.isBlank(unitsContentOwner.getOrgId())) {
                    return false;
                }
            }
        }
        return ((CourseRuleViewHelperServiceImpl) getRuleViewHelperService()).performAddLineValidation(view, collectionGroup, model, addLine);
    }

    @Override
    protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine,
                                       boolean isValidLine) {
        ((CourseRuleViewHelperServiceImpl) getRuleViewHelperService()).processAfterAddLine(view, collectionGroup, model, addLine, isValidLine);
    }


    @Override
    protected void addCustomContainerComponents(View view, Object model, Container container) {
        ((CourseRuleViewHelperServiceImpl) getRuleViewHelperService()).addCustomContainerComponents(view, model, container);
    }

    @Override
    public Boolean validateProposition(PropositionEditor proposition) {
        return getRuleViewHelperService().validateProposition(proposition);
    }

    @Override
    public void resetDescription(PropositionEditor proposition) {
        getRuleViewHelperService().resetDescription(proposition);
    }

    @Override
    public void configurePropositionForType(PropositionEditor proposition) {
        getRuleViewHelperService().configurePropositionForType(proposition);
    }

    @Override
    public TemplateInfo getTemplateForType(String type) {
        return getRuleViewHelperService().getTemplateForType(type);
    }

    @Override
    public void refreshInitTrees(RuleEditor rule) {
        getRuleViewHelperService().refreshInitTrees(rule);
    }

    @Override
    public void refreshViewTree(RuleEditor rule) {
        getRuleViewHelperService().refreshViewTree(rule);
    }

    @Override
    public Tree<CompareTreeNode, String> buildCompareTree(RuleEditor original, RuleEditor compare) {
        return getRuleViewHelperService().buildCompareTree(original, compare);
    }

    @Override
    public Tree<CompareTreeNode, String> buildMultiViewTree(RuleEditor coRuleEditor, RuleEditor cluRuleEditor) {
        return getRuleViewHelperService().buildMultiViewTree(coRuleEditor, cluRuleEditor);
    }

    @Override
    public Boolean compareRules(RuleEditor original) {
        return getRuleViewHelperService().compareRules(original);
    }

    @Override
    public void finPropositionEditor(PropositionEditor propositionEditor) {
        throw new RuntimeException("Implement me");
    }

    @Override
    public PropositionEditor copyProposition(PropositionEditor proposition) {
        return getRuleViewHelperService().copyProposition(proposition);
    }

    @Override
    public PropositionEditor createCompoundPropositionBoStub(PropositionEditor existing, boolean addNewChild) {
        return getRuleViewHelperService().createCompoundPropositionBoStub(existing, addNewChild);
    }

    @Override
    public void setTypeForCompoundOpCode(PropositionEditor proposition, String compoundOpCode) {
        getRuleViewHelperService().setTypeForCompoundOpCode(proposition, compoundOpCode);
    }

    @Override
    public PropositionEditor createSimplePropositionBoStub(PropositionEditor sibling) {
        return getRuleViewHelperService().createSimplePropositionBoStub(sibling);
    }

    @Override
    public Boolean compareProposition(PropositionEditor original, PropositionEditor compare) {
        return getRuleViewHelperService().compareProposition(original, compare);
    }

    @Override
    public Boolean compareCompoundProposition(List<PropositionEditor> original, List<PropositionEditor> compare) {
        return getRuleViewHelperService().compareCompoundProposition(original, compare);
    }

    @Override
    public Boolean compareTerm(List<TermParameterEditor> original, List<TermParameterEditor> compare) {
        return getRuleViewHelperService().compareTerm(original, compare);
    }

    @Override
    public void buildActions(final RuleEditor arg0) {
        getRuleViewHelperService().buildActions(arg0);
    }

    @Override
    public Boolean validateRule(final RuleEditor arg0) {
        return getRuleViewHelperService().validateRule(arg0);
    }

    @Override
    public String getViewTypeName() {
        return KSKRMSServiceConstants.AGENDA_TYPE_COURSE;
    }

    @Override
    public List<AgendaEditor> getAgendasForRef(String discriminatorType, String refObjectId) {
        // Initialize new array lists.
        List<AgendaEditor> agendas = new ArrayList<AgendaEditor>();
        List<AgendaEditor> sortedAgendas = new ArrayList<AgendaEditor>();

        if (refObjectId != null) {
            // Get the list of existing agendas
            List<ReferenceObjectBinding> refObjectsBindings = this.getRuleManagementService().findReferenceObjectBindingsByReferenceObject(discriminatorType, refObjectId);
            for (ReferenceObjectBinding referenceObjectBinding : refObjectsBindings) {
                agendas.add(this.getAgendaEditor(referenceObjectBinding.getKrmsObjectId()));
            }
        }

        // Lookup existing agenda by type
        for (AgendaTypeInfo agendaTypeInfo : this.getTypeRelationships()) {
            AgendaEditor agenda = null;
            for (AgendaEditor existingAgenda : agendas) {
                if (existingAgenda.getTypeId().equals(agendaTypeInfo.getId())) {
                    agenda = existingAgenda;
                    break;
                }
            }
            if (agenda == null) {
                agenda = new AgendaEditor();
                agenda.setTypeId(agendaTypeInfo.getId());
            }

            agenda.setAgendaTypeInfo(agendaTypeInfo);
            agenda.setRuleEditors(this.getRulesForAgendas(agenda));
            sortedAgendas.add(agenda);
        }

        return sortedAgendas;
    }

    /**
     * This method was overriden from the RuleEditorMaintainableImpl to create an EnrolAgendaEditor instead of
     * an AgendaEditor.
     *
     * @param agendaId
     * @return EnrolAgendaEditor.
     */
    @Override
    protected AgendaEditor getAgendaEditor(String agendaId) {
        AgendaDefinition agenda = this.getRuleManagementService().getAgenda(agendaId);
        return new LUAgendaEditor(agenda);
    }

    /**
     * Retrieves all the rules from the agenda tree and create a list of ruleeditor objects.
     * <p/>
     * Also initialize the proposition editors for each rule recursively and set natural language for the view trees.
     *
     * @param agendaItem
     * @return
     */
    @Override
    protected List<RuleEditor> getRuleEditorsFromTree(AgendaItemDefinition agendaItem, boolean initProps) {

        List<RuleEditor> rules = new ArrayList<RuleEditor>();
        if (agendaItem.getRule() != null) {

            //Build the ruleEditor
            RuleEditor ruleEditor = new LURuleEditor(agendaItem.getRule());

            //Initialize the Proposition tree
            if (initProps) {
                this.initPropositionEditor(ruleEditor.getPropositionEditor());
                ruleEditor.setViewTree(this.getViewTreeBuilder().buildTree(ruleEditor));
            }

            //Add rule to list on agenda
            rules.add(ruleEditor);
        }

        if (agendaItem.getWhenTrue() != null) {
            rules.addAll(getRuleEditorsFromTree(agendaItem.getWhenTrue(), initProps));
        }

        return rules;
    }

    /**
     * Return the clu id from the canonical course that is linked to the given course offering id.
     *
     * @param refObjectId - the course offering id.
     * @return
     * @throws Exception
     */
    @Override
    public List<ReferenceObjectBinding> getParentRefOjbects(String refObjectId) {
        return this.getRuleManagementService().findReferenceObjectBindingsByReferenceObject(CourseServiceConstants.REF_OBJECT_URI_COURSE, refObjectId);
    }


    protected RuleViewTreeBuilder getViewTreeBuilder() {
        if (this.viewTreeBuilder == null) {
            viewTreeBuilder = new LURuleViewTreeBuilder();
            viewTreeBuilder.setNlHelper(this.getNLHelper());
        }
        return viewTreeBuilder;
    }

    protected NaturalLanguageHelper getNLHelper() {
        if (this.nlHelper == null) {
            nlHelper = new NaturalLanguageHelper();
            nlHelper.setRuleManagementService(this.getRuleManagementService());
        }
        return nlHelper;
    }

    protected RuleViewHelperService getRuleViewHelperService() {
        return ruleViewHelperService;
    }

    protected CourseService getCourseService() {
        if (courseService == null) {
            courseService = (CourseService) GlobalResourceLoader.getService(new QName(CourseServiceConstants.COURSE_NAMESPACE, CourseServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return courseService;
    }

    protected SearchService getSearchService() {
        if (searchService == null) {
            searchService = GlobalResourceLoader.getService(new QName(CourseServiceConstants.NAMESPACE_PERSONSEACH, CourseServiceConstants.PERSONSEACH_SERVICE_NAME_LOCAL_PART));
        }
        return searchService;
    }

    protected SubjectCodeService getSubjectCodeService() {
        if (subjectCodeService == null) {
            subjectCodeService = GlobalResourceLoader.getService(new QName(CourseServiceConstants.NAMESPACE_SUBJECTCODE, SubjectCodeService.class.getSimpleName()));
        }
        return subjectCodeService;
    }

    protected CluService getCluService() {
        if (cluService == null) {
            cluService = GlobalResourceLoader.getService(new QName(CluServiceConstants.CLU_NAMESPACE, CluService.class.getSimpleName()));
        }
        return cluService;
    }

    protected LearningObjectiveService getLearningObjectiveService() {
        if (learningObjectiveService == null) {
            learningObjectiveService = GlobalResourceLoader.getService(new QName(
                    LearningObjectiveServiceConstants.NAMESPACE, LearningObjectiveService.class.getSimpleName()));
        }
        return learningObjectiveService;
    }

    protected OrganizationService getOrganizationService() {
        if (organizationService == null) {
            organizationService = (OrganizationService) GlobalResourceLoader
                    .getService(new QName("http://student.kuali.org/wsdl/organization", "OrganizationService"));
        }
        return organizationService;
    }

    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        // We can actually get this from the workflow document initiator id. It doesn't need to be stored in the form.
        courseInfoWrapper.setUserId(ContextUtils.getContextInfo().getPrincipalId());

        // Initialize Course Requisites
        final CourseRuleManagementWrapper ruleWrapper = getCourseRuleManagementWrapper();
        ruleWrapper.setNamespace(KSKRMSServiceConstants.NAMESPACE_CODE);

        ruleWrapper.setRefDiscriminatorType(CourseServiceConstants.REF_OBJECT_URI_COURSE);
        ruleWrapper.setRefObjectId(courseInfoWrapper.getCourseInfo().getId());

        ruleWrapper.setAgendas(getAgendasForRef(ruleWrapper.getRefDiscriminatorType(), ruleWrapper.getRefObjectId()));

        courseInfoWrapper.getCourseInfo().setStateKey(DtoConstants.STATE_DRAFT);
        courseInfoWrapper.setLastUpdated(DateFormatters.SIMPLE_TIMESTAMP_FORMATTER.format(new DateTime()));
        courseInfoWrapper.getCourseInfo().setEffectiveDate(new java.util.Date());

        courseInfoWrapper.getCourseInfo().setTypeKey(CREDIT_COURSE_CLU_TYPE_KEY);

        // Initialize Curriculum Oversight if it hasn't already been.
        if (courseInfoWrapper.getCourseInfo().getUnitsContentOwner() == null) {
            courseInfoWrapper.getCourseInfo().setUnitsContentOwner(new ArrayList<String>());
        }

        CourseCreateUnitsContentOwner newCourseCreateUnitsContentOwner = new CourseCreateUnitsContentOwner();
        newCourseCreateUnitsContentOwner.getRenderHelper().setNewRow(true);
        courseInfoWrapper.getUnitsContentOwner().add(newCourseCreateUnitsContentOwner);

        // Initialize formats
        if (courseInfoWrapper.getCourseInfo().getFormats().isEmpty()) {
            courseInfoWrapper.getCourseInfo().getFormats().add(new FormatInfo());
        }

        // Administering Organizations
        if (courseInfoWrapper.getAdministeringOrganizations().isEmpty()) {
            courseInfoWrapper.getAdministeringOrganizations().add(new OrganizationInfoWrapper());
        }

        // Initialize Instructors
        if (courseInfoWrapper.getInstructorWrappers().isEmpty()) {
            courseInfoWrapper.getInstructorWrappers().add(new CluInstructorInfoWrapper());
        }

        // Initialize Author & Collaborator
        if (courseInfoWrapper.getCollaboratorWrappers().isEmpty()) {
            courseInfoWrapper.getCollaboratorWrappers().add(new CollaboratorWrapper());
        }

        if (requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW) != null &&
                requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW).length != 0) {
            Boolean isUseReviewProcess = BooleanUtils.toBoolean(requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW)[0]);
            courseInfoWrapper.getUiHelper().setUseReviewProcess(isUseReviewProcess);
        }


    }

    @Override
    public void processCollectionAddBlankLine(View view, Object model, String collectionPath) {

        if (StringUtils.endsWith(collectionPath, "unitsContentOwner")) {
            MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) document.getNewMaintainableObject().getDataObject();

            //Before adding a new row, just make sure all the existing rows are not editable.
            for (CourseCreateUnitsContentOwner existing : courseInfoWrapper.getUnitsContentOwner()) {
                existing.getRenderHelper().setNewRow(false);
                if (StringUtils.isBlank(existing.getRenderHelper().getOrgLongName())) {
                    populateOrgName(courseInfoWrapper.getCourseInfo().getSubjectArea(), existing);
                }
            }

            OrgsBySubjectCodeValuesFinder optionsFinder = new OrgsBySubjectCodeValuesFinder();
            List<KeyValue> availableOptions = optionsFinder.getAvailableOrgs(courseInfoWrapper);

            if (!availableOptions.isEmpty()) {
                CourseCreateUnitsContentOwner newCourseCreateUnitsContentOwner = new CourseCreateUnitsContentOwner();
                newCourseCreateUnitsContentOwner.getRenderHelper().setNewRow(true);

                courseInfoWrapper.getUnitsContentOwner().add(newCourseCreateUnitsContentOwner);
            }

            return;
        }

        super.processCollectionAddBlankLine(view, model, collectionPath);
    }

    protected String populateOrgName(String subjectArea, CourseCreateUnitsContentOwner unitsContentOwner) {

        if (StringUtils.isBlank(unitsContentOwner.getOrgId())) {
            return StringUtils.EMPTY;
        }

        final SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey("subjectCode.search.orgsForSubjectCode");

        searchRequest.addParam("subjectCode.queryParam.code", subjectArea);
        searchRequest.addParam("subjectCode.queryParam.optionalOrgId", unitsContentOwner.getOrgId());

        List<KeyValue> departments = new ArrayList<KeyValue>();

        try {

            SearchResultInfo result = getSubjectCodeService().search(searchRequest, ContextUtils.getContextInfo());

            if (result.getRows().isEmpty()) {
                throw new RuntimeException("Invalid Org Id");
            }

            SearchResultRowInfo row = KSCollectionUtils.getOptionalZeroElement(result.getRows(), true);

            for (final SearchResultCellInfo resultCell : row.getCells()) {
                if ("subjectCode.resultColumn.orgLongName".equals(resultCell.getKey())) {
                    unitsContentOwner.getRenderHelper().setOrgLongName(resultCell.getValue());
                    break;
                }
            }

            if (LOG.isDebugEnabled()){
                LOG.debug("Returning {}", departments);
            }

            return StringUtils.EMPTY;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex) {
        if (StringUtils.endsWith(collectionGroup.getPropertyName(), "unitsContentOwner")) {
            MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) document.getNewMaintainableObject().getDataObject();

            for (CourseCreateUnitsContentOwner existing : courseInfoWrapper.getUnitsContentOwner()) {
                existing.getRenderHelper().setNewRow(false);
            }

        }

        super.processAfterDeleteLine(view, collectionGroup, model, lineIndex);
    }

    public void updateReview() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();
        CourseInfo savedCourseInfo = courseInfoWrapper.getCourseInfo();
        ProposalInfo proposalInfo = courseInfoWrapper.getProposalInfo();

        // Update course section
        ReviewProposalDisplay reviewData = courseInfoWrapper.getReviewProposalDisplay();
        if (reviewData == null) {
            reviewData = new ReviewProposalDisplay();
            courseInfoWrapper.setReviewProposalDisplay(reviewData);
        }
        // add logic to set the missing required element correctly. The default is false
        reviewData.setMissingRequiredElement(false);
        reviewData.getCourseSection().setProposalName(courseInfoWrapper.getProposalInfo().getName());
        reviewData.getCourseSection().setCourseTitle(savedCourseInfo.getCourseTitle());
        reviewData.getCourseSection().setTranscriptTitle(savedCourseInfo.getTranscriptTitle());
        reviewData.getCourseSection().setSubjectArea(savedCourseInfo.getSubjectArea());
        reviewData.getCourseSection().setCourseNumberSuffix(savedCourseInfo.getCourseNumberSuffix());
        reviewData.getCourseSection().setDescription(savedCourseInfo.getDescr().getPlain());
        if (proposalInfo.getRationale() != null) {
            reviewData.getCourseSection().setRationale(proposalInfo.getRationale().getPlain());
        }

        reviewData.getCourseSection().getInstructors().clear();
        for (CluInstructorInfoWrapper insturctorWrappers : courseInfoWrapper.getInstructorWrappers()) {
            reviewData.getCourseSection().getInstructors().add(insturctorWrappers.getDisplayName());
        }

        // Update governance section
        reviewData.getGovernanceSection().getCampusLocations().clear();
        reviewData.getGovernanceSection().getCampusLocations().addAll(updateCampusLocations(savedCourseInfo.getCampusLocations()));
        reviewData.getGovernanceSection().setCurriculumOversight(buildCurriculumOversightList());

        reviewData.getGovernanceSection().getAdministeringOrganization().clear();
        for (OrganizationInfoWrapper organizationInfoWrapper : courseInfoWrapper.getAdministeringOrganizations()) {
            reviewData.getGovernanceSection().getAdministeringOrganization().add(organizationInfoWrapper.getOrganizationName());
        }

        // update course logistics section
        reviewData.getCourseLogisticsSection().getTerms().clear();
        try {
            for (String termType : savedCourseInfo.getTermsOffered()) {
                TypeInfo term = getTypeService().getType(termType, ContextUtils.getContextInfo());
                reviewData.getCourseLogisticsSection().getTerms().add(term.getName());
            }
        } catch (Exception e) {
            throw new RiceIllegalStateException(e);
        }

        if (savedCourseInfo.getDuration() != null && StringUtils.isNotBlank(savedCourseInfo.getDuration().getAtpDurationTypeKey())) {
            try {
                TypeInfo type = getTypeService().getType(savedCourseInfo.getDuration().getAtpDurationTypeKey(), ContextUtils.getContextInfo());
                reviewData.getCourseLogisticsSection().setAtpDurationType(type.getName());
            } catch (Exception e) {
                throw new RiceIllegalStateException(e);
            }
        }

        if (savedCourseInfo.getDuration() != null) {
            reviewData.getCourseLogisticsSection().setTimeQuantity(savedCourseInfo.getDuration().getTimeQuantity());
        }

        reviewData.getCourseLogisticsSection().setAudit(BooleanUtils.toStringYesNo(courseInfoWrapper.isAudit()));
        reviewData.getCourseLogisticsSection().setPassFail(BooleanUtils.toStringYesNo(courseInfoWrapper.isPassFail()));
        reviewData.getCourseLogisticsSection().setGradingOptions(buildGradingOptionsList());
        reviewData.getCourseLogisticsSection().setFinalExamStatus(getFinalExamString());
        reviewData.getCourseLogisticsSection().setFinalExamStatusRationale(courseInfoWrapper.getFinalExamRationale());

        reviewData.getCourseLogisticsSection().getOutComes().clear();

        for (ResultValuesGroupInfoWrapper rvg : courseInfoWrapper.getCreditOptionWrappers()) {
            String creditOptionType = "";
            String creditOptionValue = "";
            if (StringUtils.equals(rvg.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED)) {
                creditOptionType = "Fixed";
                if (StringUtils.contains(rvg.getResultValueRange().getMinValue(), "degree.")) {
                    creditOptionValue = StringUtils.substringAfterLast(rvg.getResultValueRange().getMinValue(), "degree.");
                } else {
                    creditOptionValue = rvg.getResultValueRange().getMinValue();
                }
            } else if (StringUtils.equals(rvg.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)) {
                creditOptionType = "Multiple";
                StringBuilder builder = new StringBuilder();
                for (ResultValueKeysWrapper rvWrapper : rvg.getResultValueKeysDisplay()) {
                    builder.append(Float.valueOf(rvWrapper.getCreditValueDisplay()) + ", ");
                }
                creditOptionValue = StringUtils.removeEnd(builder.toString(), ", ");
            } else if (StringUtils.equals(rvg.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)) {
                creditOptionType = "Range";
                ResultValueRangeInfo range = rvg.getResultValueRange();
                creditOptionValue = range.getMinValue() + " - " + range.getMaxValue();
            }
            reviewData.getCourseLogisticsSection().getOutComes().add(new OutcomeReviewSection(creditOptionType, creditOptionValue));
        }

        List<FormatInfoWrapper> formatInfoWrappers = new ArrayList<FormatInfoWrapper>();
        for (FormatInfo formatInfo : savedCourseInfo.getFormats()) {

            List<ActivityInfoWrapper> activityInfoWrapperList = new ArrayList<ActivityInfoWrapper>();
            for (ActivityInfo activityInfo : formatInfo.getActivities()) {
                String durationTypeKey = activityInfo.getContactHours().getUnitTypeKey();
                Integer anticipatedClassSize = activityInfo.getDefaultEnrollmentEstimate();
                String activityType = activityInfo.getTypeKey();

                String durationCount = "";
                if (activityInfo.getDuration() != null && activityInfo.getDuration().getTimeQuantity() != null){
                    durationCount = activityInfo.getDuration().getTimeQuantity().toString();
                }

                String contactHours = "";
                if (activityInfo.getContactHours() != null && activityInfo.getContactHours().getUnitQuantity() != null){
                    contactHours = activityInfo.getContactHours().getUnitQuantity();
                }

                ActivityInfoWrapper activityInfoWrapper = new ActivityInfoWrapper(durationTypeKey, anticipatedClassSize, activityType, durationCount, contactHours);
                activityInfoWrapperList.add(activityInfoWrapper);
            }
            FormatInfoWrapper formatInfoWrapper = new FormatInfoWrapper(activityInfoWrapperList);
            formatInfoWrappers.add(formatInfoWrapper);
        }
        if (!formatInfoWrappers.isEmpty())
            reviewData.getCourseLogisticsSection().setFormatInfoWrappers(formatInfoWrappers);

        /**
         * Active Dates section
         */
        reviewData.getActiveDatesSection().setStartTerm(getTermDesc(courseInfoWrapper.getCourseInfo().getStartTerm()));
        reviewData.getActiveDatesSection().setEndTerm(getTermDesc(courseInfoWrapper.getCourseInfo().getEndTerm()));
        reviewData.getActiveDatesSection().setPilotCourse(BooleanUtils.toStringYesNo(courseInfoWrapper.getCourseInfo().isPilotCourse()));

        reviewData.getFinancialsSection().setJustificationOfFees(savedCourseInfo.getFeeJustification().getPlain());

        // update learning Objectives Section;
        // update  course Requisites Section;
        // update  financials Section;
        // update  collaborator Section;
        // update  supporting Documents Section;
    }


    private String getTermDesc(String term) {

        String result = "";

        if (StringUtils.isNotEmpty(term)) {

            QueryByCriteria.Builder qbcBuilder = QueryByCriteria.Builder.create();
            qbcBuilder.setPredicates(PredicateFactory.in("id", term));

            QueryByCriteria qbc = qbcBuilder.build();
            try {

                List<AtpInfo> searchResult = this.getAtpService().searchForAtps(qbc, ContextUtils.createDefaultContextInfo());

                AtpInfo atpInfo = KSCollectionUtils.getOptionalZeroElement(searchResult);

                if (atpInfo != null) {
                    result = atpInfo.getName();
                }

            } catch (Exception ex) {
                throw new RuntimeException("Could not retrieve description of Term \"" + term + "\" : " + ex);
            }
        }

        return result;
    }

    /**
     * Creates a List of curriculum oversight strings.
     */
    protected List<String> buildCurriculumOversightList() {
        List<String> oversights = new ArrayList<String>();
        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (CourseCreateUnitsContentOwner existing : courseInfoWrapper.getUnitsContentOwner()) {
            if (StringUtils.isBlank(existing.getRenderHelper().getOrgLongName())) {
                populateOrgName(courseInfoWrapper.getCourseInfo().getSubjectArea(), existing);
            }
            oversights.add(existing.getRenderHelper().getOrgLongName());
        }
        return oversights;
    }

    protected List<String> updateCampusLocations(List<String> campusLocations) {

        final List<KeyValue> keyValues = new ArrayList<KeyValue>();
        try {
            final List<EnumeratedValueInfo> enumerationInfos =
                    getEnumerationManagementService().getEnumeratedValues
                            (CluServiceConstants.CAMPUS_LOCATION_ENUM_KEY, null, null, null, ContextUtils.createDefaultContextInfo());

            Collections.sort(enumerationInfos, new Comparator<EnumeratedValueInfo>() {
                @Override
                public int compare(EnumeratedValueInfo o1, EnumeratedValueInfo o2) {
                    int result = o1.getSortKey().compareToIgnoreCase(o2.getSortKey());
                    return result;
                }
            });

            for (final EnumeratedValueInfo enumerationInfo : enumerationInfos) {
                keyValues.add(new ConcreteKeyValue(enumerationInfo.getCode(), enumerationInfo.getValue()));
            }

        } catch (DoesNotExistException e) {
            throw new RuntimeException("No subject areas found! There should be some in the database", e);
        } catch (Exception e) {
            throw new RuntimeException("Error looking up Campus Locations", e);
        }

        List<String> newCampusLocationsName = new ArrayList<String>();
        for (KeyValue kval : keyValues) {
            if (campusLocations.contains(kval.getKey())) {
                newCampusLocationsName.add(kval.getValue());
            }
        }

        return newCampusLocationsName;
    }

    @Override
    public void saveDataObject() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        //Clear collection fields (those with matching 'wrapper' collections)
        courseInfoWrapper.getCourseInfo().getJoints().clear();
        courseInfoWrapper.getCourseInfo().getInstructors().clear();
        courseInfoWrapper.getCourseInfo().getUnitsDeployment().clear();
        courseInfoWrapper.getCourseInfo().getCourseSpecificLOs().clear();

        //Retrieve the collection display values and get the fully loaded object (containing all the IDs and related IDs)
        if (courseInfoWrapper.getCourseJointWrappers() != null) {
            for (final CourseJointInfoWrapper jointInfoDisplay : courseInfoWrapper.getCourseJointWrappers()) {
                courseInfoWrapper.getCourseInfo().getJoints().add(CourseCodeSearchUtil.getCourseJointInfoWrapper(jointInfoDisplay.getCourseCode(), getCluService()));
            }
        }

        courseInfoWrapper.getCourseInfo().getInstructors().clear();

        if (courseInfoWrapper.getInstructorWrappers() != null) {
            for (final CluInstructorInfoWrapper instructorDisplay : courseInfoWrapper.getInstructorWrappers()) {
                courseInfoWrapper.getCourseInfo().getInstructors().add(instructorDisplay);
            }
        }

        for(OrganizationInfoWrapper organizationInfoWrapper :courseInfoWrapper.getAdministeringOrganizations()) {
            courseInfoWrapper.getCourseInfo().getUnitsDeployment().add(organizationInfoWrapper.getOrganizationName());
        }

        if (courseInfoWrapper.getLoDisplayWrapperModel() != null && courseInfoWrapper.getLoDisplayWrapperModel().getLoWrappers() != null) {
            List<LoDisplayInfoWrapper> loWrappers = courseInfoWrapper.getLoDisplayWrapperModel().getLoWrappers();
            List<LoDisplayInfo> courseLos = courseInfoWrapper.getCourseInfo().getCourseSpecificLOs();
            for (int i = 0; i < loWrappers.size(); i++) {

                LoDisplayInfoWrapper currentLo = loWrappers.get(i);

                boolean rootLevel = true;
                int parentIndex = i - 1;
                while (parentIndex >= 0) {
                    LoDisplayInfoWrapper potentialParent = loWrappers.get(parentIndex);
                    boolean parentMatch = currentLo.getIndentLevel() > potentialParent.getIndentLevel();
                    if (parentMatch) {
                        //currentLo.setParentLoRelationid(potentialParent.getLoInfo().getId());
                        //currentLo.setParentRelType(CourseAssemblerConstants.COURSE_LO_RELATION_INCLUDES);
                        potentialParent.getLoDisplayInfoList().add(currentLo);

                        rootLevel = false;
                        break;
                    } else {
                        parentIndex--;
                    }
                }

                if (rootLevel) {
                    courseLos.add(currentLo);
                }
            }
        }

        // Set derived course fields before saving/updating
        courseInfoWrapper.setCourseInfo(calculateCourseDerivedFields(courseInfoWrapper.getCourseInfo()));
        courseInfoWrapper.setLastUpdated(DateFormatters.SIMPLE_TIMESTAMP_FORMATTER.format(new DateTime()));

        courseInfoWrapper.getCourseInfo().getUnitsContentOwner().clear();
        for (CourseCreateUnitsContentOwner wrapper : courseInfoWrapper.getUnitsContentOwner()) {
            courseInfoWrapper.getCourseInfo().getUnitsContentOwner().add(wrapper.getOrgId());
            wrapper.getRenderHelper().setNewRow(false);
            if (StringUtils.isBlank(wrapper.getRenderHelper().getOrgLongName())) {
                populateOrgName(courseInfoWrapper.getCourseInfo().getSubjectArea(), wrapper);
            }
        }

        //Formats
        for (FormatInfo format : courseInfoWrapper.getCourseInfo().getFormats()) {
            if (StringUtils.isBlank(format.getId())) { // If it's new
                format.setState(DtoConstants.STATE_DRAFT);
                if (StringUtils.isBlank(format.getTypeKey())) {
                    format.setTypeKey(CluServiceConstants.COURSE_FORMAT_TYPE_KEY);
                }
            }
            for (ActivityInfo activity : format.getActivities()) {
                if (StringUtils.isBlank(activity.getId())) { // If it's new
                    activity.setState(DtoConstants.STATE_DRAFT);
                }
            }
        }

        courseInfoWrapper.getCourseInfo().getCreditOptions().clear();

        //Credit Options
        if (courseInfoWrapper.isAudit()) {
            ResultValuesGroupInfo resultValuesGroupInfo = new ResultValuesGroupInfo();
            resultValuesGroupInfo.setName("Audit");
            resultValuesGroupInfo.setTypeKey(LrcServiceConstants.RESULT_GROUP_KEY_GRADE_AUDIT);
            //This should be based on the course state. But for now, it's draft
            resultValuesGroupInfo.setStateKey(LrcServiceConstants.RESULT_GROUPS_STATE_DRAFT);
            courseInfoWrapper.getCourseInfo().getCreditOptions().add(resultValuesGroupInfo);
        }

        populateAuditOnDTO();

        populatePassFailOnDTO();

        populateFinalExamOnDTO();

        populateOutComesOnDTO();

        courseInfoWrapper.getCourseInfo().setStartTerm(courseInfoWrapper.getCourseInfo().getStartTerm());
        courseInfoWrapper.getCourseInfo().setEndTerm(courseInfoWrapper.getCourseInfo().getEndTerm());
        courseInfoWrapper.getCourseInfo().setPilotCourse(courseInfoWrapper.getCourseInfo().isPilotCourse());

        try {
            handleFirstTimeSave();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        super.saveDataObject();

    }


    protected void populateOutComesOnDTO() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        courseInfoWrapper.getCourseInfo().getCreditOptions().clear();

        for (ResultValuesGroupInfoWrapper rvgWrapper : courseInfoWrapper.getCreditOptionWrappers()) {

            ResultValuesGroupInfo rvg = rvgWrapper.getResultValuesGroupInfo();

            if (rvg == null) {
                rvg = new ResultValuesGroupInfo();
                courseInfoWrapper.getCourseInfo().getCreditOptions().add(rvg);
                rvg.setTypeKey(rvgWrapper.getTypeKey());
                rvg.setStateKey(LrcServiceConstants.RESULT_GROUPS_STATE_DRAFT);
            }

            if (StringUtils.equals(rvgWrapper.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED)) {
                rvg.setResultValueRange(rvgWrapper.getResultValueRange());
            } else if (StringUtils.equals(rvgWrapper.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)) {
                for (ResultValueKeysWrapper rvKeys : rvgWrapper.getResultValueKeysDisplay()) {
                    StringBuilder builder = new StringBuilder(LrcServiceConstants.RESULT_VALUE_KEY_CREDIT_DEGREE_PREFIX);
                    float floatValue = Float.valueOf(rvKeys.getCreditValueDisplay());
                    builder.append(floatValue);
                    rvg.getResultValueKeys().add(builder.toString());
                }
            } else if (StringUtils.equals(rvgWrapper.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)) {
                rvg.setResultValueRange(rvgWrapper.getResultValueRange());
            }
        }

    }


    protected void populateOutComesOnWrapper() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (ResultValuesGroupInfo rvg : courseInfoWrapper.getCourseInfo().getCreditOptions()) {
            ResultValuesGroupInfoWrapper rvgWrapper = new ResultValuesGroupInfoWrapper();
            BeanUtils.copyProperties(rvg, rvgWrapper);
            rvgWrapper.setResultValuesGroupInfo(rvg);
            if (StringUtils.equals(rvg.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)) {
                for (String rvKey : rvg.getResultValueKeys()) {
                    ResultValueKeysWrapper keysWrapper = new ResultValueKeysWrapper();
                    String value = StringUtils.strip(rvKey, LrcServiceConstants.RESULT_VALUE_KEY_CREDIT_DEGREE_PREFIX);
                    value = StringUtils.strip(value, ".0"); // This can be only be integer at ui.
                    keysWrapper.setCreditValueDisplay(value);
                    rvgWrapper.getResultValueKeysDisplay().add(keysWrapper);
                }
            } else if (StringUtils.equals(rvg.getTypeKey(), LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)) {
                rvg.getResultValueRange().setMinValue(StringUtils.strip(rvg.getResultValueRange().getMinValue(), ".0")); // This can be only be integer at ui.
                rvg.getResultValueRange().setMaxValue(StringUtils.strip(rvg.getResultValueRange().getMaxValue(), ".0")); // This can be only be integer at ui.
            }
            courseInfoWrapper.getCreditOptionWrappers().add(rvgWrapper);
        }

    }

    protected void populatePassFailOnDTO() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo passFailAttr = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL)) {
                passFailAttr = attr;
                break;
            }
        }

        if (passFailAttr == null) {
            passFailAttr = new AttributeInfo();
            passFailAttr.setKey(CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL);
            courseInfoWrapper.getCourseInfo().getAttributes().add(passFailAttr);
        }

        passFailAttr.setValue(BooleanUtils.toStringTrueFalse(courseInfoWrapper.isPassFail()));
    }

    protected void populatePassFailOnWrapper() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL)) {
                courseInfoWrapper.setPassFail(BooleanUtils.toBoolean(attr.getValue()));
                break;
            }
        }

    }

    protected void populateAuditOnDTO() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo auditAttr = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT)) {
                auditAttr = attr;
                break;
            }
        }

        if (auditAttr == null) {
            auditAttr = new AttributeInfo();
            auditAttr.setKey(CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT);
            courseInfoWrapper.getCourseInfo().getAttributes().add(auditAttr);
        }

        auditAttr.setValue(BooleanUtils.toStringTrueFalse(courseInfoWrapper.isAudit()));
    }

    protected void populateAuditOnWrapper() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT)) {
                courseInfoWrapper.setAudit(BooleanUtils.toBoolean(attr.getValue()));
                break;
            }
        }

    }

    protected void populateFinalExamOnDTO() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo finalExamStatusDetail = null;
        AttributeInfo finalExamRationalDetail = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseServiceConstants.FINAL_EXAM)) {
                finalExamStatusDetail = attr;
            } else if (StringUtils.equals(attr.getKey(), CourseServiceConstants.FINAL_EXAM_RATIONALE)) {
                finalExamRationalDetail = attr;
            }
        }

        if (finalExamStatusDetail == null) {
            finalExamStatusDetail = new AttributeInfo();
            finalExamStatusDetail.setKey(CourseServiceConstants.FINAL_EXAM);
            courseInfoWrapper.getCourseInfo().getAttributes().add(finalExamStatusDetail);
        }

        if (StringUtils.isNotBlank(courseInfoWrapper.getFinalExamStatus()) &&
                !StringUtils.equals(courseInfoWrapper.getFinalExamStatus(), CourseServiceConstants.STD_EXAM_FINAL_ENUM_KEY) &&
                finalExamRationalDetail == null) {
            finalExamRationalDetail = new AttributeInfo();
            finalExamRationalDetail.setKey(CourseServiceConstants.FINAL_EXAM_RATIONALE);
            courseInfoWrapper.getCourseInfo().getAttributes().add(finalExamRationalDetail);
        }

        if (finalExamRationalDetail != null) {
            finalExamRationalDetail.setValue(courseInfoWrapper.getFinalExamRationale());
        }

        finalExamStatusDetail.setValue(courseInfoWrapper.getFinalExamStatus());
    }


    protected void populateFinalExamOnWrapper() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()) {
            if (StringUtils.equals(attr.getKey(), CourseServiceConstants.FINAL_EXAM)) {
                courseInfoWrapper.setFinalExamStatus(attr.getValue());
            } else if (StringUtils.equals(attr.getKey(), CourseServiceConstants.FINAL_EXAM_RATIONALE)) {
                courseInfoWrapper.setFinalExamRationale(attr.getValue());
            }
        }

    }

    /**
     * @return  List list of grading options.
     */
    protected List<String> buildGradingOptionsList() {
        List<String> gradingOptions = new ArrayList<String>();

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        if (!courseInfoWrapper.getCourseInfo().getGradingOptions().isEmpty()) {
           try {
                List<ResultValuesGroupInfo> rvgs = getLRCService().getResultValuesGroupsByKeys(courseInfoWrapper.getCourseInfo().getGradingOptions(), createContextInfo());
                for (ResultValuesGroupInfo rvg : rvgs) {
                    gradingOptions.add(rvg.getName());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return gradingOptions;
    }

    protected String getFinalExamString() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        if (StringUtils.isNotBlank(courseInfoWrapper.getFinalExamStatus())) {
            List<EnumeratedValueInfo> enumerationInfos = null;
            try {
                enumerationInfos = getEnumerationManagementService().getEnumeratedValues(
                        CluServiceConstants.FINAL_EXAM_STATUS_ENUM_KEY, null, null, null, ContextUtils.getContextInfo());

                for (EnumeratedValueInfo enumerationInfo : enumerationInfos) {
                    if (StringUtils.equals(courseInfoWrapper.getFinalExamStatus(), enumerationInfo.getCode())) {
                        return enumerationInfo.getValue();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return "";
    }

    /**
     * Converts the display name of the instructor into the plain user name (for use in a search query)
     *
     * @param displayName The display name of the instructor.
     * @return The user name of the instructor.
     */
    protected String getInstructorSearchString(String displayName) {
        String searchString = "";
        if (displayName.contains("(") && displayName.contains(")")) {
            searchString = displayName.substring(displayName.lastIndexOf('(') + 1, displayName.lastIndexOf(')'));
        }
        return searchString;
    }

    /**
     * Copied this method from CourseDataService.
     * This calculates and sets fields on course object that are derived from other course object fields.
     */
    protected CourseInfo calculateCourseDerivedFields(CourseInfo courseInfo) {
        // Course code is not populated in UI, need to derive them from the subject area and suffix fields
        if (StringUtils.isNotBlank(courseInfo.getCourseNumberSuffix()) && StringUtils.isNotBlank(courseInfo.getSubjectArea())) {
            courseInfo.setCode(calculateCourseCode(courseInfo.getSubjectArea(), courseInfo.getCourseNumberSuffix()));
        }

        // Derive course code for crosslistings
        for (CourseCrossListingInfo crossListing : courseInfo.getCrossListings()) {
            if (StringUtils.isNotBlank(crossListing.getCourseNumberSuffix()) && StringUtils.isNotBlank(crossListing.getSubjectArea())) {
                crossListing.setCode(calculateCourseCode(crossListing.getSubjectArea(), crossListing.getCourseNumberSuffix()));
            }
        }

        return courseInfo;
    }


    /**
     * Copied this method from CourseDataService
     * This method calculates code for course and cross listed course.
     *
     * @param subjectArea
     * @param suffixNumber
     * @return
     */
    protected String calculateCourseCode(final String subjectArea, final String suffixNumber) {
        return subjectArea + suffixNumber;
    }

    public void retrieveDataObject() {

        CourseInfoWrapper dataObject = (CourseInfoWrapper) getDataObject();

        try {
            ProposalInfo proposal = getProposalService().getProposalByWorkflowId(getDocumentNumber(), ContextUtils.getContextInfo());
            dataObject.setProposalInfo(proposal);

            CourseInfo course = getCourseService().getCourse(proposal.getProposalReference().get(0), createContextInfo());
            dataObject.setCourseInfo(course);

            // dataObject.getCourseInfo().setStartTerm(course.getStartTerm());
            //dataObject.getCourseInfo().setEndTerm(course.getEndTerm());
            // dataObject.getCourseInfo().setPilotCourse(course.isPilotCourse());

            for (String orgId : course.getUnitsContentOwner()) {
                CourseCreateUnitsContentOwner orgWrapper = new CourseCreateUnitsContentOwner();
                orgWrapper.setOrgId(orgId);
                populateOrgName(course.getSubjectArea(), orgWrapper);
                dataObject.getUnitsContentOwner().add(orgWrapper);
            }

            for (CluInstructorInfo instructorInfo : course.getInstructors()) {
                CluInstructorInfoWrapper instructorInfoWrapper = new CluInstructorInfoWrapper();
                dataObject.getInstructorWrappers().addAll(getInstructorsbyId(instructorInfo.getPersonId()));
            }

            for(String unitDeployment : course.getUnitsDeployment()) {
                OrganizationInfoWrapper organizationInfoWrapper = new OrganizationInfoWrapper();
                organizationInfoWrapper.setOrganizationName(unitDeployment);
                dataObject.getAdministeringOrganizations().add(organizationInfoWrapper) ;
            }

            populateAuditOnWrapper();
            populateFinalExamOnWrapper();
            populatePassFailOnWrapper();
            populateOutComesOnWrapper();

            redrawDecisionTable();
            updateReview();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Handles functionality that should only happen when the document is first saved.
     */
    protected void handleFirstTimeSave() throws Exception {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        final CourseInfo course = courseInfoWrapper.getCourseInfo();
        for (final CourseVariationInfo variation : course.getVariations()) {
            variation.setTypeKey(ProgramConstants.VARIATION_TYPE_KEY);
        }
        if (StringUtils.isBlank(course.getId())) {
            courseInfoWrapper.setCourseInfo(getCourseService().createCourse(course, ContextUtils.getContextInfo()));
        } else {
            courseInfoWrapper.setCourseInfo(getCourseService().updateCourse(course.getId(), course, ContextUtils.getContextInfo()));
        }

        LOG.info("Saving Proposal for course {}", courseInfoWrapper.getCourseInfo().getId());

        ProposalInfo proposal = courseInfoWrapper.getProposalInfo();
        proposal.setWorkflowId(getDocumentNumber());
        if (StringUtils.isBlank(proposal.getId())) {
            proposal.setState(ProposalConstants.PROPOSAL_STATE_SAVED);     // remove proposal constant, try to use KualiStudentPostProcessorBase
            proposal.setType(ProposalServiceConstants.PROPOSAL_TYPE_COURSE_CREATE_KEY);
            proposal.setProposalReferenceType(ProposalServiceConstants.PROPOSAL_DOC_RELATION_TYPE_CLU_KEY);
            proposal.getProposalReference().add(courseInfoWrapper.getCourseInfo().getId());
            proposal.getProposerOrg().clear();
            proposal.getProposerPerson().clear();
        }

        if (StringUtils.isBlank(proposal.getId())) {
            proposal = getProposalService().createProposal(ProposalServiceConstants.PROPOSAL_TYPE_COURSE_CREATE_KEY, proposal, ContextUtils.getContextInfo());
        } else {
            proposal = getProposalService().updateProposal(proposal.getId(), proposal, ContextUtils.getContextInfo());
        }
        courseInfoWrapper.setProposalInfo(proposal);
    }

    /**
     * @throws InvalidParameterException when incorrect parameters are used for looking up the comments made
     * @throws MissingParameterException when null or empty parameters are used for looking up the comments made
     * @throws OperationFailedException  when it cannot be determined what comments were made.
     * @throws PermissionDeniedException when the user doesn't have rights to look up comments.
     */
    protected void redrawDecisionTable()
            throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<CommentInfo> commentInfos = null;
        try {
            commentInfos = getCommentService()
                    .getCommentsByReferenceAndType("temp_reference_id",
                            "referenceType.clu.proposal",
                            ContextUtils.getContextInfo());
        } catch (DoesNotExistException e) {
            // Add a dummy row
            // form.getDecisions().add(new DecisionInfo());

            // If there are no comments, don't go any further
            return;
        }

        // Collect person ids to search
        final List<String> personIds = new ArrayList<String>();
        for (CommentInfo comment : commentInfos) {
            if (comment.getMeta().getCreateId() != null) {
                personIds.add(comment.getMeta().getCreateId());
            } else {
                personIds.add("");
            }
        }

        final Map<String, MembershipInfo> members = getNamesForPersonIds(personIds);

        redrawDecisionTable(commentInfos, members);
    }

    /**
     * Responsible for rebuilding/reloading content within the decision view's HTML table.
     *
     * @param commentInfos {@link List} of {@link CommentInfo} instances that currently exist.
     *                     These are comments for decisions that represent our rationale
     * @param members      {@link Map} of {@link MembershipInfo} instances mapped by commenter ids.
     *                     Commenter ids are supplied in the {@link CommentInfo} instances. The {@link MembershipInfo}
     *                     is used to determine who is responsible for the decision.
     * @see {@link #getNamesForPersonIds(List)} for the method that typically populates the members parameter.
     */
    protected void redrawDecisionTable(final List<CommentInfo> commentInfos,
                                       final Map<String, MembershipInfo> members) {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        final KSDateTimeFormatter dateFormat = DateFormatters.MONTH_DAY_YEAR_DATE_FORMATTER;

        if (commentInfos != null) {
            for (final CommentInfo commentInfo : commentInfos) {
                /* we only want decision rationale comments so if no DecisionRationaleDetail is returned for comment
                 * type then don't add that comment to the table
                 */
                final WorkflowUtilities.DecisionRationaleDetail drDetails = WorkflowUtilities.DecisionRationaleDetail.getByType(commentInfo.getTypeKey());
                if (drDetails != null) {
                    final DecisionInfo decision = new DecisionInfo();
                    decision.setDecision(drDetails.getLabel());
                    decision.setId(commentInfo.getId());

                    final String rationaleDate = dateFormat.format(new DateTime(commentInfo.getMeta().getCreateTime().getTime()));
                    decision.setDate(rationaleDate);

                    if (members.get(commentInfo.getMeta().getCreateId()) != null) {
                        final MembershipInfo memberInfo = members.get(commentInfo.getMeta().getCreateId());
                        final StringBuilder memberName = new StringBuilder();
                        memberName.append(memberInfo.getFirstName());
                        memberName.append(" ");
                        memberName.append(memberInfo.getLastName());

                        decision.setActor(memberName.toString());
                    }
                    decision.setRationale(commentInfo.getCommentText().getPlain());
                    courseInfoWrapper.getDecisions().add(decision);
                }
            }
        }
    }

    /**
     * Retrieve {@link MembershipInfo} instances populated with first and last names using {@link org.kuali.rice.kim.api.identity.entity.EntityDefault}
     * instances of the personIds
     *
     * @param personIds {@link List} of ids used to get {@link org.kuali.rice.kim.api.identity.entity.EntityDefault}s with first and last names
     * @param {@link    Map} of {@link MembershipInfo} instances with first and last names mapped to each personId
     */
    protected Map<String, MembershipInfo> getNamesForPersonIds(final List<String> personIds) {
        final Map<String, MembershipInfo> identities = new HashMap<String, MembershipInfo>();
        for (String pId : personIds) {
            final EntityDefault entity = getIdentityService().getEntityDefaultByPrincipalId(pId);
            final MembershipInfo memeberEntity = new MembershipInfo();
            memeberEntity.setFirstName(entity.getName().getFirstName());
            memeberEntity.setLastName(entity.getName().getLastName());
            identities.put(pId, memeberEntity);
        }
        return identities;
    }

    /*@Override
    public Map<String,String> prepareDataObjectKeys() {
        Map<String,String> dataObjectDetails = new HashMap<String, String>();
        String courseId = ((CourseInfoWrapper)getDataObject()).getCourseInfo().getId();
        dataObjectDetails.put("id",courseId);
        return dataObjectDetails;
    }*/

    protected CommentService getCommentService() {
        if (commentService == null) {
            commentService = (CommentService) GlobalResourceLoader.getService(new QName(CommentServiceConstants.NAMESPACE, CommentService.class.getSimpleName()));
        }
        return commentService;
    }

    protected ProposalService getProposalService() {
        if (proposalService == null) {
            proposalService = (ProposalService) GlobalResourceLoader.getService(new QName(ProposalServiceConstants.NAMESPACE, ProposalServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return proposalService;
    }

    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = GlobalResourceLoader.getService(new QName(KimConstants.Namespaces.KIM_NAMESPACE_2_0, "identityService"));
        }
        return identityService;
    }

    protected TypeService getTypeService() {
        if (typeService == null) {
            typeService = (TypeService) GlobalResourceLoader.getService(new QName(TypeServiceConstants.NAMESPACE, TypeServiceConstants.SERVICE_NAME_LOCAL_PART));
        }

        return typeService;
    }

    private LRCService getLRCService() {
        if (lrcService == null) {
            QName qname = new QName(LrcServiceConstants.NAMESPACE, LrcServiceConstants.SERVICE_NAME_LOCAL_PART);
            lrcService = (LRCService) GlobalResourceLoader.getService(qname);
        }
        return lrcService;
    }

    protected EnumerationManagementService getEnumerationManagementService() {
        if (enumerationManagementService == null) {
            enumerationManagementService = (EnumerationManagementService) GlobalResourceLoader.getService(new QName(
                    EnumerationManagementServiceConstants.NAMESPACE, EnumerationManagementServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return this.enumerationManagementService;
    }

    protected AtpService getAtpService() {
        if (atpService == null) {
            QName qname = new QName(AtpServiceConstants.NAMESPACE, AtpServiceConstants.SERVICE_NAME_LOCAL_PART);
            atpService = (AtpService) GlobalResourceLoader.getService(qname);
        }
        return atpService;
    }

}
