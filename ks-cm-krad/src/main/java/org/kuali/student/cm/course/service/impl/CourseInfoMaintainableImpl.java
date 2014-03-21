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
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
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
import org.kuali.student.cm.common.util.CurriculumManagementConstants;
import org.kuali.student.cm.course.controller.CourseController;
import org.kuali.student.cm.course.form.CluInstructorInfoWrapper;
import org.kuali.student.cm.course.form.CollaboratorWrapper;
import org.kuali.student.cm.course.form.CourseInfoWrapper;
import org.kuali.student.cm.course.form.CourseJointInfoWrapper;
import org.kuali.student.cm.course.form.CourseRuleManagementWrapper;
import org.kuali.student.cm.course.form.LoCategoryInfoWrapper;
import org.kuali.student.cm.course.form.LoDisplayInfoWrapper;
import org.kuali.student.cm.course.form.LoDisplayWrapperModel;
import org.kuali.student.cm.course.form.OrganizationInfoWrapper;
import org.kuali.student.cm.course.form.ResultValueKeysWrapper;
import org.kuali.student.cm.course.form.ResultValuesGroupInfoWrapper;
import org.kuali.student.cm.course.form.ReviewProposalDisplay;
import org.kuali.student.cm.course.form.SubjectCodeWrapper;
import org.kuali.student.cm.course.service.CourseInfoMaintainable;
import org.kuali.student.cm.course.service.util.CourseCodeSearchUtil;
import org.kuali.student.cm.course.service.util.LoCategorySearchUtil;
import org.kuali.student.cm.course.service.util.OrganizationSearchUtil;
import org.kuali.student.cm.maintenance.CMMaintainable;
import org.kuali.student.common.util.security.ContextUtils;
import org.kuali.student.core.krms.tree.KSRuleViewTreeBuilder;
import org.kuali.student.core.organization.ui.client.mvc.model.MembershipInfo;
import org.kuali.student.core.workflow.ui.client.widgets.WorkflowUtilities;
import org.kuali.student.enrollment.class2.courseoffering.util.CourseOfferingConstants;
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
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.class1.type.service.TypeService;
import org.kuali.student.r2.core.comment.dto.CommentInfo;
import org.kuali.student.r2.core.comment.dto.DecisionInfo;
import org.kuali.student.r2.core.comment.service.CommentService;
import org.kuali.student.r2.core.constants.CommentServiceConstants;
import org.kuali.student.r2.core.constants.KSKRMSServiceConstants;
import org.kuali.student.r2.core.constants.ProposalServiceConstants;
import org.kuali.student.r2.core.constants.TypeServiceConstants;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.kuali.student.r2.core.proposal.dto.ProposalInfo;
import org.kuali.student.r2.core.proposal.service.ProposalService;
import org.kuali.student.r2.core.search.dto.SearchParamInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultCellInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.dto.SearchResultRowInfo;
import org.kuali.student.r2.core.search.service.SearchService;
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
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.kuali.student.r2.lum.lrc.infc.ResultValuesGroup;
import org.kuali.student.r2.lum.lrc.service.LRCService;
import org.kuali.student.r2.lum.util.constants.CluServiceConstants;
import org.kuali.student.r2.lum.util.constants.CourseServiceConstants;
import org.kuali.student.r2.lum.util.constants.LrcServiceConstants;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.student.logging.FormattedLogger.error;
import static org.kuali.student.logging.FormattedLogger.info;

/**
 *
 *
 * Base view helper service for both create and edit course info presentations.
 *
 * @author OpenCollab/rSmart KRAD CM Conversion Alliance!
 */
public class CourseInfoMaintainableImpl extends RuleEditorMaintainableImpl implements CourseInfoMaintainable, RuleViewHelperService,CMMaintainable {


    protected transient static final String DEFAULT_REQUIRED_WORKFLOW_MODE = "Submit";

    protected transient static final String CREDIT_COURSE_CLU_TYPE_KEY  = "kuali.lu.typeKey.CreditCourse";

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

    @Transient
    private ProposalService proposalService;

    private TypeService typeService;

    private LRCService lrcService;


    /**
     * Method called when queryMethodToCall is executed for Administering Organizations in order to suggest back to the user an Administering Organization
     *
     * @param organizationName
     * @see CourseInfoMaintainable#getOrganizationsForSuggest(String)
     */
    public List<OrganizationInfoWrapper> getOrganizationsForSuggest(final String organizationName) {
        return OrganizationSearchUtil.searchForOrganizations(organizationName, getOrganizationService());
    }

    /**
     * @see CourseInfoMaintainable#getInstructorsForSuggest(String)
     */
    public List<CluInstructorInfoWrapper> getInstructorsForSuggest(
        String instructorName) {
        List<CluInstructorInfoWrapper> cluInstructorInfoDisplays = new ArrayList<CluInstructorInfoWrapper>();

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo displayNameParam = new SearchParamInfo();
        displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
        displayNameParam.getValues().add(instructorName);
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
        } catch (Exception e) {
            error("An error occurred in the getInstructorsForSuggest method. %s", e.getMessage());
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
     * @see CourseInfoMaintainable#getInstructor(String)
     */
    public CluInstructorInfoWrapper getInstructor(String instructorName) {
        CluInstructorInfoWrapper instructor = null;

        List<SearchParamInfo> queryParamValueList = new ArrayList<SearchParamInfo>();

        SearchParamInfo displayNameParam = new SearchParamInfo();
        displayNameParam.setKey(QuickViewByGivenName.NAME_PARAM);
        displayNameParam.getValues().add(instructorName);
        queryParamValueList.add(displayNameParam);

        SearchRequestInfo searchRequest = new SearchRequestInfo();
        searchRequest.setSearchKey(QuickViewByGivenName.SEARCH_TYPE);
        searchRequest.setParams(queryParamValueList);
        searchRequest.setStartAt(0);
        searchRequest.setNeededTotalResults(false);
        searchRequest.setSortColumn(QuickViewByGivenName.DISPLAY_NAME_RESULT);

        SearchResultInfo searchResult = null;
        try {
            searchResult = getSearchService().search(searchRequest,
                                                     ContextUtils.getContextInfo());
            if (searchResult.getRows().size() == 1) {
                SearchResultRowInfo result = searchResult.getRows().get(0);
                List<SearchResultCellInfo> cells = result.getCells();
                instructor = new CluInstructorInfoWrapper();
                for (SearchResultCellInfo cell : cells) {
                    if (QuickViewByGivenName.GIVEN_NAME_RESULT.equals(cell.getKey())) {
                        instructor.setGivenName(cell.getValue());
                    } else if (QuickViewByGivenName.PERSON_ID_RESULT.equals(cell.getKey())) {
                        instructor.setPersonId(cell.getValue());
                    } else if (QuickViewByGivenName.ENTITY_ID_RESULT.equals(cell.getKey())) {
                        instructor.setId(cell.getValue());
                    } else if (QuickViewByGivenName.PRINCIPAL_NAME_RESULT.equals(cell.getKey())) {
                        instructor.setPrincipalName(cell.getValue());
                    } else if (QuickViewByGivenName.DISPLAY_NAME_RESULT.equals(cell.getKey())) {
                        instructor.setDisplayName(cell.getValue());
                    }
                }
            } else {
                error(CurriculumManagementConstants.MessageKeys.ERROR_GET_INSTRUCTOR_RETURN_MORE_THAN_ONE_RESULT);
            }
        } catch (Exception e) {
            error("An error occurred in the getInstructor method. %s", e.getMessage());
        }

        return instructor;
    }

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
            error("An error occurred retrieving the SubjectCodeDisplay: %s", e);
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


    public CourseInfo getCourse() {
        return ((CourseInfoWrapper) getDataObject()).getCourseInfo();
    }

    public void setCourse(final CourseInfo course) {
        setDataObject(course);
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
            error("Error retrieving Personel search List %s", e);
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

        // Initialize formats
        if (courseInfoWrapper.getCourseInfo().getFormats().isEmpty()) {
            courseInfoWrapper.getCourseInfo().getFormats().add(new FormatInfo());
        }

        if (requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW) != null || requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW).length != 0){
            Boolean isUseReviewProcess = BooleanUtils.toBoolean(requestParameters.get(CourseController.URL_PARAM_USE_CURRICULUM_REVIEW)[0]);
            courseInfoWrapper.getUiHelper().setUseReviewProcess(isUseReviewProcess);
        }


    }

    public void updateReview() {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper)getDataObject();
        CourseInfo savedCourseInfo = courseInfoWrapper.getCourseInfo();
        ProposalInfo proposalInfo = courseInfoWrapper.getProposalInfo();

        // Update course section
        ReviewProposalDisplay reviewData = courseInfoWrapper.getReviewProposalDisplay();
        if (reviewData == null){
            reviewData = new ReviewProposalDisplay();
            courseInfoWrapper.setReviewProposalDisplay(reviewData);
        }

        reviewData.getcourseSection().setProposalName(courseInfoWrapper.getProposalInfo().getName());
//        maintenanceDocForm.setProposalName(courseInfoWrapper.getProposalInfo().getName());
        reviewData.getcourseSection().setCourseTitle(savedCourseInfo.getCourseTitle());
        reviewData.getcourseSection().setTranscriptTitle(savedCourseInfo.getTranscriptTitle());
        reviewData.getcourseSection().setSubjectArea(savedCourseInfo.getSubjectArea());
        reviewData.getcourseSection().setCourseNumberSuffix(savedCourseInfo.getCourseNumberSuffix());
        reviewData.getcourseSection().setDescription(savedCourseInfo.getDescr().getPlain());
        if (proposalInfo.getRationale() != null){
            reviewData.getcourseSection().setRationale(proposalInfo.getRationale().getPlain());
        }

        // Update governance section
        reviewData.getgovernanceSection().getCampusLocations().clear();
        reviewData.getgovernanceSection().getCampusLocations().addAll(savedCourseInfo.getCampusLocations());
        reviewData.getgovernanceSection().getCurriculumOversight().clear();
        reviewData.getgovernanceSection().getCurriculumOversight().addAll(savedCourseInfo.getUnitsContentOwner());

        // update course logistics section
        reviewData.getcourseLogisticsSection().getTerms().clear();
        try {
            for(String termType : savedCourseInfo.getTermsOffered())  {
                TypeInfo term = getTypeService().getType(termType, ContextUtils.getContextInfo());
                reviewData.getcourseLogisticsSection().getTerms().add(term.getName());
            }
        } catch (Exception e) {
            throw new RiceIllegalStateException(e);
        }

        if (savedCourseInfo.getDuration() != null && StringUtils.isNotBlank(savedCourseInfo.getDuration().getAtpDurationTypeKey())) {
            try {
                TypeInfo term = getTypeService().getType(savedCourseInfo.getDuration().getAtpDurationTypeKey(), ContextUtils.getContextInfo());
                reviewData.getcourseLogisticsSection().setAtpDurationType(term.getName());
            } catch (Exception e) {
                throw new RiceIllegalStateException(e);
            }
        }

        if (savedCourseInfo.getDuration() != null){
            reviewData.getcourseLogisticsSection().setTimeQuantity(savedCourseInfo.getDuration().getTimeQuantity());
        }

        reviewData.getcourseLogisticsSection().setGradingOptions(getAssessementScaleString());

        // update learning Objectives Section;
        // update  course Requisites Section;
        // update  active Dates Section;
        // update  financials Section;
        // update  collaborator Section;
        // update  supporting Documents Section;
    }

    @Override
    public void saveDataObject(){

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

        if (courseInfoWrapper.getInstructorWrappers() != null) {
            for (final CluInstructorInfoWrapper instructorDisplay : courseInfoWrapper.getInstructorWrappers()) {
                final CluInstructorInfoWrapper retrievedInstructor = getInstructor(getInstructorSearchString(instructorDisplay.getDisplayName()));
                courseInfoWrapper.getCourseInfo().getInstructors().add(retrievedInstructor);
            }
        }

        if (courseInfoWrapper.getAdministeringOrganizations() != null) {
            for (final OrganizationInfoWrapper org : courseInfoWrapper.getAdministeringOrganizations()) {
                courseInfoWrapper.getCourseInfo().getUnitsDeployment().add(org.getOrganizationName());
            }
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

        courseInfoWrapper.getCourseInfo().setUnitsContentOwner(new ArrayList<String>());
        for (final KeyValue wrapper : courseInfoWrapper.getUnitsContentOwner()) {
            courseInfoWrapper.getCourseInfo().getUnitsContentOwner().add(wrapper.getValue());
        }

        //Formats
        for (FormatInfo format : courseInfoWrapper.getCourseInfo().getFormats()){
            if (StringUtils.isBlank(format.getId())){ // If it's new
                format.setState(DtoConstants.STATE_DRAFT);
                if (StringUtils.isBlank(format.getTypeKey())){
                    format.setTypeKey(CluServiceConstants.COURSE_FORMAT_TYPE_KEY);
                }
            }
            for (ActivityInfo activity : format.getActivities()){
                if (StringUtils.isBlank(activity.getId())){ // If it's new
                    activity.setState(DtoConstants.STATE_DRAFT);
                }
            }
        }

        courseInfoWrapper.getCourseInfo().getCreditOptions().clear();

        //Credit Options
        if (courseInfoWrapper.isAudit()){
            ResultValuesGroupInfo resultValuesGroupInfo = new ResultValuesGroupInfo();
            resultValuesGroupInfo.setName("Audit");
            resultValuesGroupInfo.setTypeKey(LrcServiceConstants.RESULT_GROUP_KEY_GRADE_AUDIT);
            //This should be based on the course state. But for now, it's draft
            resultValuesGroupInfo.setStateKey(LrcServiceConstants.RESULT_GROUPS_STATE_DRAFT);
            courseInfoWrapper.getCourseInfo().getCreditOptions().add(resultValuesGroupInfo);
        }

        populateAuditOnDTO();

        populatePassFailOnDTO();

        populateFinalExamStatusOnDTO();

        populateOutComesOnDTO();


        try {
            handleFirstTimeSave();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        super.saveDataObject();

    }


    protected void populateOutComesOnDTO(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        courseInfoWrapper.getCourseInfo().getCreditOptions().clear();

        for (ResultValuesGroupInfoWrapper rvgWrapper : courseInfoWrapper.getCreditOptionWrappers()){

            ResultValuesGroupInfo rvg = rvgWrapper.getResultValuesGroupInfo();

            if (rvg == null){
                rvg = new ResultValuesGroupInfo();
                courseInfoWrapper.getCourseInfo().getCreditOptions().add(rvg);
                rvg.setTypeKey(rvgWrapper.getTypeKey());
                rvg.setStateKey(LrcServiceConstants.RESULT_GROUPS_STATE_DRAFT);
            }

            if (StringUtils.equals(rvgWrapper.getTypeKey(),LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_FIXED)){
                rvg.setResultValueRange(rvgWrapper.getResultValueRange());
            } else if (StringUtils.equals(rvgWrapper.getTypeKey(),LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)){
                for (ResultValueKeysWrapper rvKeys : rvgWrapper.getResultValueKeysDisplay()){
                    rvg.getResultValueKeys().add(rvKeys.getCreditValueDisplay());
                }
            } else if (StringUtils.equals(rvgWrapper.getTypeKey(),LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_RANGE)){
                rvg.setResultValueRange(rvgWrapper.getResultValueRange());
            }
        }

    }


    protected void populateOutComesOnWrapper(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (ResultValuesGroupInfo rvg : courseInfoWrapper.getCourseInfo().getCreditOptions()){
            ResultValuesGroupInfoWrapper rvgWrapper = new ResultValuesGroupInfoWrapper();
            BeanUtils.copyProperties(rvg,rvgWrapper);
            rvgWrapper.setResultValuesGroupInfo(rvg);
            if (StringUtils.equals(rvg.getTypeKey(),LrcServiceConstants.RESULT_VALUES_GROUP_TYPE_KEY_MULTIPLE)){
                for (String rvKey : rvg.getResultValueKeys()){
                    ResultValueKeysWrapper keysWrapper = new ResultValueKeysWrapper();
                    keysWrapper.setCreditValueDisplay(rvKey);
                    rvgWrapper.getResultValueKeysDisplay().add(keysWrapper);
                }
            }
            courseInfoWrapper.getCreditOptionWrappers().add(rvgWrapper);
        }

    }

    protected void populatePassFailOnDTO(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo passFailAttr = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL)){
                passFailAttr = attr;
                break;
            }
        }

        if (passFailAttr == null){
            passFailAttr = new AttributeInfo();
            passFailAttr.setKey(CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL);
            courseInfoWrapper.getCourseInfo().getAttributes().add(passFailAttr);
        }

        passFailAttr.setValue(BooleanUtils.toStringTrueFalse(courseInfoWrapper.isPassFail()));
    }

    protected void populatePassFailOnWrapper(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_PASSFAIL)){
                courseInfoWrapper.setPassFail(BooleanUtils.toBoolean(attr.getValue()));
                break;
            }
        }

    }

    protected void populateAuditOnDTO(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo auditAttr = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT)){
                auditAttr = attr;
                break;
            }
        }

        if (auditAttr == null){
            auditAttr = new AttributeInfo();
            auditAttr.setKey(CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT);
            courseInfoWrapper.getCourseInfo().getAttributes().add(auditAttr);
        }

        auditAttr.setValue(BooleanUtils.toStringTrueFalse(courseInfoWrapper.isAudit()));
    }

    protected void populateAuditOnWrapper(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseAssemblerConstants.COURSE_RESULT_COMP_ATTR_AUDIT)){
                courseInfoWrapper.setAudit(BooleanUtils.toBoolean(attr.getValue()));
                break;
            }
        }

    }

    protected void populateFinalExamStatusOnDTO(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        AttributeInfo finalExamDetail = null;
        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseOfferingConstants.COURSEOFFERING_FINAL_EXAM_TYPE_KEY)){
                finalExamDetail = attr;
                break;
            }
        }

        if (finalExamDetail == null){
            finalExamDetail = new AttributeInfo();
            courseInfoWrapper.getCourseInfo().getAttributes().add(finalExamDetail);
        }

        finalExamDetail.setValue(courseInfoWrapper.getFinalExamStatus());
    }


    protected void populateFinalExamStatusOnWrapper(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        for (AttributeInfo attr : courseInfoWrapper.getCourseInfo().getAttributes()){
            if (StringUtils.equals(attr.getKey(), CourseOfferingConstants.COURSEOFFERING_FINAL_EXAM_TYPE_KEY)){
                courseInfoWrapper.setFinalExamStatus(attr.getValue());
                break;
            }
        }

    }

    protected String getAssessementScaleString(){

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        if (!courseInfoWrapper.getCourseInfo().getGradingOptions().isEmpty()){
            try {
                List<ResultValuesGroupInfo> resultValuesGroupInfos = getLRCService().getResultValuesGroupsByKeys(courseInfoWrapper.getCourseInfo().getGradingOptions(),createContextInfo());
                StringBuilder builder = new StringBuilder();
                for (ResultValuesGroupInfo info : resultValuesGroupInfos){
                    builder.append(info.getName() + ",");
                }
                return StringUtils.removeEnd(builder.toString(), ",");
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
        String searchString = null;
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

        CourseInfoWrapper dataObject = (CourseInfoWrapper)getDataObject();

        try {
            ProposalInfo proposal = getProposalService().getProposalByWorkflowId(getDocumentNumber(), ContextUtils.getContextInfo());
            dataObject.setProposalInfo(proposal);

            CourseInfo course = getCourseService().getCourse(proposal.getProposalReference().get(0),createContextInfo());
            dataObject.setCourseInfo(course);

            populateAuditOnWrapper();
            populateFinalExamStatusOnWrapper();
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
     *
     */
    protected void handleFirstTimeSave() throws Exception {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper)getDataObject();

        final CourseInfo course = courseInfoWrapper.getCourseInfo();
        for (final CourseVariationInfo variation : course.getVariations()) {
            variation.setTypeKey(ProgramConstants.VARIATION_TYPE_KEY);
        }
        if (StringUtils.isBlank(course.getId())){
            courseInfoWrapper.setCourseInfo(getCourseService().createCourse(course, ContextUtils.getContextInfo()));
        } else {
            courseInfoWrapper.setCourseInfo(getCourseService().updateCourse(course.getId(), course, ContextUtils.getContextInfo()));
        }

        info("Saving Proposal for course %s", courseInfoWrapper.getCourseInfo().getId());

        ProposalInfo proposal = courseInfoWrapper.getProposalInfo();
        proposal.setWorkflowId(getDocumentNumber());
        if (StringUtils.isBlank(proposal.getId())){
            proposal.setState(ProposalConstants.PROPOSAL_STATE_SAVED);     // remove proposal constant, try to use KualiStudentPostProcessorBase
            proposal.setType(ProposalServiceConstants.PROPOSAL_TYPE_COURSE_CREATE_KEY);
            proposal.setProposalReferenceType(ProposalServiceConstants.PROPOSAL_DOC_RELATION_TYPE_CLU_KEY);
            proposal.getProposalReference().add(courseInfoWrapper.getCourseInfo().getId());
            proposal.getProposerOrg().clear();
            proposal.getProposerPerson().clear();
        }

        if (StringUtils.isBlank(proposal.getId())){
            proposal = getProposalService().createProposal(ProposalServiceConstants.PROPOSAL_TYPE_COURSE_CREATE_KEY, proposal, ContextUtils.getContextInfo());
        } else {
            proposal = getProposalService().updateProposal(proposal.getId(), proposal, ContextUtils.getContextInfo());
        }
        courseInfoWrapper.setProposalInfo(proposal);
    }

    /**
     *
     * @throws InvalidParameterException when incorrect parameters are used for looking up the comments made
     * @throws MissingParameterException when null or empty parameters are used for looking up the comments made
     * @throws OperationFailedException when it cannot be determined what comments were made.
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
        }
        catch (DoesNotExistException e) {
            // Add a dummy row
            // form.getDecisions().add(new DecisionInfo());

            // If there are no comments, don't go any further
            return;
        }

        // Collect person ids to search
        final List<String> personIds = new ArrayList<String>();
        for (CommentInfo comment : commentInfos) {
            if(comment.getMeta().getCreateId()!=null){
                personIds.add(comment.getMeta().getCreateId());
            }
            else{
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
     * These are comments for decisions that represent our rationale
     * @param members {@link Map} of {@link MembershipInfo} instances mapped by commenter ids.
     * Commenter ids are supplied in the {@link CommentInfo} instances. The {@link MembershipInfo}
     * is used to determine who is responsible for the decision.
     * @see {@link #getNamesForPersonIds(List)} for the method that typically populates the members parameter.
     */
    protected void redrawDecisionTable(final List<CommentInfo> commentInfos,
                                       final Map<String, MembershipInfo> members) {

        CourseInfoWrapper courseInfoWrapper = (CourseInfoWrapper) getDataObject();

        //final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
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
     * @param {@link Map} of {@link MembershipInfo} instances with first and last names mapped to each personId
     */
    protected Map<String, MembershipInfo> getNamesForPersonIds(final List<String> personIds) {
        final Map<String, MembershipInfo> identities = new HashMap<String, MembershipInfo>();
        for (String pId : personIds ){
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
        if(typeService == null) {
            typeService =  (TypeService) GlobalResourceLoader.getService(new QName(TypeServiceConstants.NAMESPACE, TypeServiceConstants.SERVICE_NAME_LOCAL_PART));
        }

        return typeService;
    }

    private LRCService getLRCService() {
        if (lrcService == null)
        {
            QName qname = new QName(LrcServiceConstants.NAMESPACE, LrcServiceConstants.SERVICE_NAME_LOCAL_PART);
            lrcService = (LRCService) GlobalResourceLoader.getService(qname);
        }
        return lrcService;
    }
}
