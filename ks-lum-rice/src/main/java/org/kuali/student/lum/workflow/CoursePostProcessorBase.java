/**
 * 
 */
package org.kuali.student.lum.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.framework.postprocessor.IDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.student.r1.core.statement.dto.ReqComponentInfo;
import org.kuali.student.r1.core.statement.dto.StatementTreeViewInfo;
import org.kuali.student.r1.lum.course.service.CourseServiceConstants;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.CurrencyAmountInfo;
import org.kuali.student.r2.common.dto.DtoConstants;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.util.AttributeHelper;
import org.kuali.student.r2.common.util.ContextUtils;
import org.kuali.student.r2.core.proposal.dto.ProposalInfo;
import org.kuali.student.r2.core.versionmanagement.dto.VersionDisplayInfo;
import org.kuali.student.r2.lum.clu.CLUConstants;
import org.kuali.student.r2.lum.clu.dto.AffiliatedOrgInfo;
import org.kuali.student.r2.lum.clu.dto.CluInstructorInfo;
import org.kuali.student.r2.lum.course.dto.ActivityInfo;
import org.kuali.student.r2.lum.course.dto.CourseCrossListingInfo;
import org.kuali.student.r2.lum.course.dto.CourseFeeInfo;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.course.dto.CourseJointInfo;
import org.kuali.student.r2.lum.course.dto.CourseRevenueInfo;
import org.kuali.student.r2.lum.course.dto.CourseVariationInfo;
import org.kuali.student.r2.lum.course.dto.FormatInfo;
import org.kuali.student.r2.lum.course.dto.LoDisplayInfo;
import org.kuali.student.r2.lum.course.service.CourseService;
import org.kuali.student.r2.lum.lrc.dto.ResultValuesGroupInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * A base post processor class for Course document types in Workflow.
 *
 */
@Transactional(readOnly=true, rollbackFor={Throwable.class})
public class CoursePostProcessorBase extends KualiStudentPostProcessorBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CoursePostProcessorBase.class);

    private CourseService courseService;
    private CourseStateChangeServiceImpl courseStateChangeService;

    /**
     *    This method changes the state of the course when a Withdraw action is processed on a proposal.
     *    For create and modify proposals, a new clu was created which needs to be cancelled via
     *    setting it to "not approved."
      *    
     *    For retirement proposals, a clu is never actually created, therefore we don't update the clu at
     *    all if it is withdrawn.
     *       
     *    @param actionTakenEvent - contains the docId, the action taken (code "d"), the principalId which submitted it, etc
     *    @param proposalInfo - The proposal object being withdrawn 
     */   
    @Override
    protected void processWithdrawActionTaken(ActionTakenEvent actionTakenEvent, ProposalInfo proposalInfo) throws Exception {
        
        if (proposalInfo != null){
            String proposalDocType=proposalInfo.getType();      
            // The current two proposal docTypes which being withdrawn will cause a course to be 
            // disapproved are Create and Modify (because a new DRAFT version is created when these 
            // proposals are submitted.)
            if ( CLUConstants.PROPOSAL_TYPE_COURSE_CREATE.equals(proposalDocType)
                    ||  CLUConstants.PROPOSAL_TYPE_COURSE_MODIFY.equals(proposalDocType)) {
                LOG.info("Will set CLU state to '"
                        + DtoConstants.STATE_NOT_APPROVED + "'");
                // Get Clu
                CourseInfo courseInfo = getCourseService().getCourse(
                        getCourseId(proposalInfo), ContextUtils.getContextInfo());
                // Update Clu
                updateCourse(actionTakenEvent, DtoConstants.STATE_NOT_APPROVED,
                        courseInfo, proposalInfo);
            } 
            // Retire proposal is the only proposal type at this time which will not require a 
            // change to the clu if withdrawn.
                        else if ( CLUConstants.PROPOSAL_TYPE_COURSE_RETIRE.equals(proposalDocType)) {
                LOG.info("Withdrawing a retire proposal with ID'" + proposalInfo.getId() 
                        + ", will not change any CLU state as there is no new CLU object to set.");
            }
        } else {
            LOG.info("Proposal Info is null when a withdraw proposal action was taken, doing nothing.");
        }
    }

    @Override
    protected boolean processCustomActionTaken(ActionTakenEvent actionTakenEvent, ActionTaken actionTaken, ProposalInfo proposalInfo) throws Exception {
        String cluId = getCourseId(proposalInfo);
        CourseInfo courseInfo = getCourseService().getCourse(cluId, ContextUtils.getContextInfo());
        // submit, blanket approve action taken comes through here.        
        updateCourse(actionTakenEvent, null, courseInfo, proposalInfo);
        return true;
    }

    /**
     * This method takes a clu proposal, determines what the "new state"
     * of the clu should be, then routes the clu I, and the new state
     * to CourseStateChangeServiceImpl.java
     */
     @Override   
     protected boolean processCustomRouteStatusChange(DocumentRouteStatusChange statusChangeEvent, ProposalInfo proposalInfo) throws Exception {

         String courseId = getCourseId(proposalInfo);        
         String prevEndTermAtpId = new AttributeHelper (proposalInfo.getAttributes()).get("prevEndTerm");
         
         // Get the current "existing" courseInfo
         CourseInfo courseInfo = getCourseService().getCourse(courseId, ContextUtils.getContextInfo());
         
         // Get the new state the course should now change to        
         String newCourseState = getCluStateForRouteStatus(courseInfo.getStateKey(), statusChangeEvent.getNewRouteStatus(), proposalInfo.getType());
         
         //Use the state change service to update to active and update preceding versions
         if (newCourseState != null){
             if(DtoConstants.STATE_ACTIVE.equals(newCourseState)){     
                 
                 // Change the state using the effective date as the version start date
                 // update course and save it for retire if state = retire           
                 getCourseStateChangeService().changeState(courseId, newCourseState, prevEndTermAtpId, ContextUtils.getContextInfo());
             } else {
                 
                 // Retire By Proposal will come through here, extra data will need 
                 // to be copied from the proposalInfo to the courseInfo fields before 
                 // the save happens.            
                 if(DtoConstants.STATE_RETIRED.equals(newCourseState)){
                     retireCourseByProposalCopyAndSave(newCourseState, courseInfo, proposalInfo);
                     getCourseStateChangeService().changeState(courseId, newCourseState, prevEndTermAtpId, ContextUtils.getContextInfo());
                 } else { // newCourseState of null comes here, is this desired?
                	 updateCourse(statusChangeEvent, newCourseState, courseInfo, proposalInfo);
                 }   
             }
         }
         return true;
     }
     
     /**
      * 
      * 
      * In this method, the proposal object fields are copied to the cluInfo object
      * fields to pass validation. This method copies data from the custom Retire
      * By Proposal proposalInfo Object Fields into the courseInfo object so that upon save it will
      * pass validation.
      * 
      * Admin Retire and Retire by Proposal both end up here.
      * 
      * This Route will get you here, Route Statuses:
      * 'S' Saved 
      * 'R' Enroute 
      * 'A' Approved - After final approve, status is set to 'A'  
      * 'P' Processed - During this run through coursepostprocessorbase, assuming 
      * doctype is Retire, we end up here.  
      * 
      * @param courseState - used to confirm state is retired
     * @param courseInfo - course object we are updating
     * @param proposalInfo - proposal object which has the on-screen fields we are copying from
      */
    protected void retireCourseByProposalCopyAndSave(String courseState, CourseInfo courseInfo, ProposalInfo proposalInfo) throws Exception {

        // Copy the data to the object - 
        // These Proposal Attribs need to go back to courseInfo Object 
        // to pass validation.
        if (DtoConstants.STATE_RETIRED.equals(courseState)) {
            if ((proposalInfo != null) && (proposalInfo.getAttributes() != null)) {
                String rationale = null;
                if (proposalInfo.getRationale() != null) {
                    rationale = proposalInfo.getRationale().getPlain();
                }
                String proposedEndTerm = new AttributeHelper(proposalInfo.getAttributes()).get("proposedEndTerm");
                String proposedLastTermOffered = new AttributeHelper(proposalInfo.getAttributes()).get("proposedLastTermOffered");
                String proposedLastCourseCatalogYear = new AttributeHelper(proposalInfo.getAttributes()).get("proposedLastCourseCatalogYear");

                courseInfo.setEndTerm(proposedEndTerm);
                courseInfo.getAttributes().add(new AttributeInfo("retirementRationale", rationale));
                courseInfo.getAttributes().add(new AttributeInfo("lastTermOffered", proposedLastTermOffered));
                courseInfo.getAttributes().add(new AttributeInfo("lastPublicationYear", proposedLastCourseCatalogYear));

                // lastTermOffered is a special case field, as it is required upon retire state
                // but not required for submit.  Therefore it is possible for a user to submit a retire proposal
                // without this field filled out, then when the course gets approved, and the state changes to RETIRED
                // validation would fail and the proposal will then go into exception routing.  
                // We can't simply make lastTermOffered a required field as it is not a desired field  
                // on the course proposal screen.
                //              
                // So in the case of lastTermOffered being null when a course is retired,
                // Just copy the "proposalInfo.proposedEndTerm" value (required for saves, so it will be filled out) 
                // into "courseInfo.lastTermOffered" to pass validation.   
                if ((proposalInfo != null) && (courseInfo != null)
                        && (courseInfo.getAttributeValue("lastTermOffered") == null)) {
                    courseInfo.getAttributes().add(new AttributeInfo("lastTermOffered", new AttributeHelper(proposalInfo.getAttributes()).get("proposedEndTerm")));
                }
            }
        }
        // Save the Data to the DB
        getCourseService().updateCourse(courseInfo.getId(), courseInfo, ContextUtils.getContextInfo());
    }

    protected String getCourseId(ProposalInfo proposalInfo) throws OperationFailedException {
        if (proposalInfo.getProposalReference().size() != 1) {
            LOG.error("Found " + proposalInfo.getProposalReference().size() + " CLU objects linked to proposal with proposalId='" + proposalInfo.getId() + "'. Must have exactly 1 linked.");
            throw new OperationFailedException("Found " + proposalInfo.getProposalReference().size() + " CLU objects linked to proposal with docId='" + proposalInfo.getWorkflowId() + "' and proposalId='" + proposalInfo.getId() + "'. Must have exactly 1 linked.");
        }
        return proposalInfo.getProposalReference().get(0);
    }

    /** This method returns the state a clu should go to, based on 
     *  the Proposal's docType and the newWorkflow StatusCode 
     *  which are passed in.
     * 
     * @param currentCluState - the current state set on the CLU
     * @param newWorkflowStatusCode - the new route status code that is getting set on the workflow document
     * @param docType - The doctype of the proposal which kicked off this workflow.
     * @return the CLU state to set or null if the CLU does not need it's state changed
     */
    protected String getCluStateForRouteStatus(String currentCluState, String newWorkflowStatusCode, String docType) {
        if (CLUConstants.PROPOSAL_TYPE_COURSE_RETIRE.equals(docType)) {
            // This is for Retire Proposal, Course State should remain active for
            // all other route statuses.            
            if (KewApiConstants.ROUTE_HEADER_PROCESSED_CD.equals(newWorkflowStatusCode)){
                return DtoConstants.STATE_RETIRED;
            }   
            return null;  // returning null indicates no change in course state required
        } else {
            //  The following is for Create, Modify, and Admin Modify proposals.    
            if (StringUtils.equals(KewApiConstants.ROUTE_HEADER_SAVED_CD, newWorkflowStatusCode)) {
                return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_DRAFT);
            } else if (KewApiConstants.ROUTE_HEADER_CANCEL_CD .equals(newWorkflowStatusCode)) {
                return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_NOT_APPROVED);
            } else if (KewApiConstants.ROUTE_HEADER_ENROUTE_CD.equals(newWorkflowStatusCode)) {
                return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_DRAFT);
            } else if (KewApiConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(newWorkflowStatusCode)) {
                /* current requirements state that on a Withdraw (which is a KEW Disapproval) the 
                 * CLU state should be submitted so no special handling required here
                 */
                return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_NOT_APPROVED);
            } else if (KewApiConstants.ROUTE_HEADER_PROCESSED_CD.equals(newWorkflowStatusCode)) {
            	if (CLUConstants.PROPOSAL_TYPE_COURSE_MODIFY_CURRENT_VERSION.equals(docType)){
                	return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_PROCESSED);
                } else {
                	return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_ACTIVE);
                }
            } else if (KewApiConstants.ROUTE_HEADER_EXCEPTION_CD.equals(newWorkflowStatusCode)) {
                return getCourseStateFromNewState(currentCluState, DtoConstants.STATE_DRAFT);
            } else {
                // no status to set
                return null;
            }
        }
    }

    /**
     * Default behavior is to return the <code>newCluState</code> variable only if it differs from the
     * <code>currentCluState</code> value. Otherwise <code>null</code> will be returned.
     */
    protected String getCourseStateFromNewState(String currentCourseState, String newCourseState) {
        if (LOG.isInfoEnabled()) {
            LOG.info("current CLU state is '" + currentCourseState + "' and new CLU state will be '" + newCourseState + "'");
        }
        return getStateFromNewState(currentCourseState, newCourseState);
    }

    @Transactional(readOnly=false,noRollbackFor={DoesNotExistException.class},rollbackFor={Throwable.class})
    protected void updateCourse(IDocumentEvent iDocumentEvent, String courseState, CourseInfo courseInfo, ProposalInfo proposalInfo) throws Exception {
        // only change the state if the course is not currently set to that state
        boolean requiresSave = false;
        if (courseState != null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Setting state '" + courseState + "' on CLU with cluId='" + courseInfo.getId() + "'");
            }
            courseInfo.setStateKey(courseState);
            requiresSave = true;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Running preProcessCluSave with cluId='" + courseInfo.getId() + "'");
        }
        requiresSave |= preProcessCourseSave(iDocumentEvent, courseInfo);

        if (requiresSave) {
        	
        	getCourseService().updateCourse(courseInfo.getId(), courseInfo, ContextUtils.getContextInfo());
        	
        	// If modify to current version, get current course and modify with updated course
        	if (CLUConstants.PROPOSAL_TYPE_COURSE_MODIFY_CURRENT_VERSION.equals(proposalInfo.getTypeKey()) && 
        			DtoConstants.STATE_PROCESSED.equals(courseState)){
        		VersionDisplayInfo currentCourse = courseService.getCurrentVersion(CourseServiceConstants.COURSE_NAMESPACE_URI, courseInfo.getVersion().getVersionIndId(), ContextUtils.getContextInfo());
        		CourseInfo currentCourseInfo = courseService.getCourse(currentCourse.getId(), ContextUtils.getContextInfo());        		
        		// Update current version fields with updated values
        		copyToCurrentVersion(currentCourseInfo, courseInfo);
        		
        		getCourseService().updateCourse(currentCourse.getId(), currentCourseInfo, ContextUtils.getContextInfo());
            } 
            
            //For a newly approved course (w/no prior active versions), make the new course the current version.
            if (DtoConstants.STATE_ACTIVE.equals(courseState) && courseInfo.getVersion().getCurrentVersionStart() == null){
            	// TODO: set states of other approved courses to superseded                
                
            	// if current version's state is not active then we can set this course as the active course
            	//if (!DtoConstants.STATE_ACTIVE.equals(getCourseService().getCourse(getCourseService().getCurrentVersion(CourseServiceConstants.COURSE_NAMESPACE_URI, courseInfo.getVersion().getVersionIndId()).getId()).getState())) { 
            		getCourseService().setCurrentCourseVersion(courseInfo.getId(), null, ContextUtils.getContextInfo());
            	//}
            }
            
            List<StatementTreeViewInfo> statementTreeViewInfos = courseService.getCourseStatements(courseInfo.getId(), null, null, ContextUtils.getContextInfo());
            if(statementTreeViewInfos!=null){
	            statementTreeViewInfoStateSetter(courseInfo.getStateKey(), statementTreeViewInfos.iterator());
	            
	            for(Iterator<StatementTreeViewInfo> it = statementTreeViewInfos.iterator(); it.hasNext();)

	        		courseService.updateCourseStatement(courseInfo.getId(), courseState, it.next(), ContextUtils.getContextInfo());
            }
        }
        
    }

    // protected so we can test
    protected void copyToCurrentVersion(CourseInfo currentCourseInfo, CourseInfo changedCourseInfo) {

        LOG.info("Updating course version " + currentCourseInfo.getId()
                + " with " + changedCourseInfo.getId() + "course version.");

        currentCourseInfo.setName(changedCourseInfo.getName());
        currentCourseInfo.setDescr(changedCourseInfo.getDescr());
        currentCourseInfo.setCode(changedCourseInfo.getCode());
        currentCourseInfo.setCourseNumberSuffix(changedCourseInfo.getCourseNumberSuffix());
        currentCourseInfo.setLevel(changedCourseInfo.getLevel());
        currentCourseInfo.setCourseTitle(changedCourseInfo.getCourseTitle());
        currentCourseInfo.setTranscriptTitle(changedCourseInfo.getTranscriptTitle());
        currentCourseInfo.getFormats().clear();
        currentCourseInfo.getFormats().addAll(changedCourseInfo.getFormats());

        for (FormatInfo changedFormat : currentCourseInfo.getFormats()) {
            changedFormat.setId(null);
            changedFormat.setMeta(null);
            changedFormat.setStateKey(changedFormat.getStateKey());

            for (AttributeInfo attribute : changedFormat.getAttributes()) {
                attribute.setId(null);
            }

            for (ActivityInfo activity : changedFormat.getActivities()) {
                activity.setId(null);

                for (AttributeInfo attribute : activity.getAttributes()) {
                    attribute.setId(null);
                }
            }
        }

        currentCourseInfo.getTermsOffered().clear();
        currentCourseInfo.getTermsOffered().addAll(changedCourseInfo.getTermsOffered());

        //duration
        currentCourseInfo.setDuration(changedCourseInfo.getDuration());

        //joints
        currentCourseInfo.getJoints().clear();
        currentCourseInfo.getJoints().addAll(changedCourseInfo.getJoints());
        for (CourseJointInfo courseJoint : currentCourseInfo.getJoints()) {
            courseJoint.setRelationId(null);
            courseJoint.setMeta(null);

            for (AttributeInfo attribute : courseJoint.getAttributes()) {
                attribute.setId(null);
            }
        }

        // crosslistings
        currentCourseInfo.getCrossListings().clear();
        currentCourseInfo.getCrossListings().addAll(changedCourseInfo.getCrossListings());
        for (CourseCrossListingInfo crossListing : currentCourseInfo.getCrossListings()) {
            crossListing.setId(null);
            crossListing.setMeta(null);
            crossListing.setStateKey(currentCourseInfo.getStateKey());

            for (AttributeInfo attribute : crossListing.getAttributes()) {
                attribute.setId(null);
            }
        }

        //variations
        currentCourseInfo.getVariations().clear();
        currentCourseInfo.getVariations().addAll(changedCourseInfo.getVariations());
        for (CourseVariationInfo variations : currentCourseInfo.getVariations()) {
            variations.setId(null);
            variations.setMeta(null);
            variations.setStateKey(currentCourseInfo.getStateKey()); // Is this correct for overall course state?

            for (AttributeInfo attribute : variations.getAttributes()) {
                attribute.setId(null);
            }
        }

        //subjectArea
        currentCourseInfo.setSubjectArea(changedCourseInfo.getSubjectArea());

        //campusLocations
        currentCourseInfo.getCampusLocations().clear();
        currentCourseInfo.getCampusLocations().addAll(changedCourseInfo.getCampusLocations());

        //outOfClassHours
        currentCourseInfo.setOutOfClassHours(changedCourseInfo.getOutOfClassHours());

        //primaryInstructor
        currentCourseInfo.setPrimaryInstructor(changedCourseInfo.getPrimaryInstructor());
        if (currentCourseInfo.getPrimaryInstructor() != null) {
            currentCourseInfo.getPrimaryInstructor().setId(null);
            for (AttributeInfo attribute : currentCourseInfo.getPrimaryInstructor().getAttributes()) {
                attribute.setId(null);
            }
        }

        //Instructors
        currentCourseInfo.getInstructors().clear();
        currentCourseInfo.getInstructors().addAll(changedCourseInfo.getInstructors());
        for (CluInstructorInfo instructor : currentCourseInfo.getInstructors()) {
            instructor.setId(null);

            for (AttributeInfo attribute : instructor.getAttributes()) {
                attribute.setId(null);
            }
        }

        //unitsDeployment
        currentCourseInfo.getUnitsDeployment().clear();
        currentCourseInfo.getUnitsDeployment().addAll(changedCourseInfo.getUnitsDeployment());

        //feeJustification
        currentCourseInfo.setFeeJustification(changedCourseInfo.getFeeJustification());

        //unitsContentOwner
        currentCourseInfo.getUnitsContentOwner().clear();
        currentCourseInfo.getUnitsContentOwner().addAll(changedCourseInfo.getUnitsContentOwner());

        //fees
        currentCourseInfo.getFees().clear();
        currentCourseInfo.getFees().addAll(changedCourseInfo.getFees());
        for (CourseFeeInfo courseFee : currentCourseInfo.getFees()) {
            courseFee.setId(null);
            courseFee.setMeta(null);
            courseFee.setStateKey(changedCourseInfo.getStateKey());
            for (CurrencyAmountInfo feeAmount : courseFee.getFeeAmounts()) {
                feeAmount.setId(null);
                feeAmount.setMeta(null);
            }
        }

        //revenues
        currentCourseInfo.getRevenues().clear();
        currentCourseInfo.getRevenues().addAll(changedCourseInfo.getRevenues());
        for (CourseRevenueInfo revenueInfo : currentCourseInfo.getRevenues()) {
            revenueInfo.setId(null);
            revenueInfo.setMeta(null);
            revenueInfo.setStateKey(changedCourseInfo.getStateKey());

            for (AffiliatedOrgInfo affiliatedOrg : revenueInfo.getAffiliatedOrgs()) {
                affiliatedOrg.setId(null);
                affiliatedOrg.setMeta(null);
                affiliatedOrg.setStateKey(changedCourseInfo.getStateKey());
                for (AttributeInfo attribute : affiliatedOrg.getAttributes()) {
                    attribute.setId(null);
                }
            }

            for (AttributeInfo attribute : revenueInfo.getAttributes()) {
                attribute.setId(null);
            }
        }

        //expenditure
        currentCourseInfo.setExpenditure(currentCourseInfo.getExpenditure());
        if (ObjectUtils.isNotNull(currentCourseInfo.getExpenditure())) {
            for (AffiliatedOrgInfo affiliatedOrg : currentCourseInfo.getExpenditure().getAffiliatedOrgs()) {
                affiliatedOrg.setId(null);
                affiliatedOrg.setMeta(null);
                affiliatedOrg.setStateKey(changedCourseInfo.getStateKey());
                for (AttributeInfo attribute : affiliatedOrg.getAttributes()) {
                    attribute.setId(null);
                }
            }
            for (AttributeInfo attribute : currentCourseInfo.getExpenditure().getAttributes()) {
                attribute.setId(null);
            }
        }
        //courseSpecificLOs
        currentCourseInfo.getCourseSpecificLOs().clear();
        currentCourseInfo.getCourseSpecificLOs().addAll(changedCourseInfo.getCourseSpecificLOs());
        for (LoDisplayInfo lo : currentCourseInfo.getCourseSpecificLOs()) {
            resetLoRecursively(lo, changedCourseInfo.getStateKey());
        }

        //gradingOptions
        currentCourseInfo.getGradingOptions().clear();
        currentCourseInfo.getGradingOptions().addAll(changedCourseInfo.getGradingOptions());

        //creditOptions
        currentCourseInfo.getCreditOptions().clear();
        currentCourseInfo.getCreditOptions().addAll(changedCourseInfo.getCreditOptions());

        //specialTopicsCourse
        currentCourseInfo.setSpecialTopicsCourse(changedCourseInfo.isSpecialTopicsCourse());

        //pilotCourse		
        currentCourseInfo.setPilotCourse(changedCourseInfo.isPilotCourse());

        //startTerm
        currentCourseInfo.setStartTerm(changedCourseInfo.getStartTerm());

        //endTerm
        currentCourseInfo.setEndTerm(changedCourseInfo.getEndTerm());

        //effectiveDate
        currentCourseInfo.setEffectiveDate(changedCourseInfo.getEffectiveDate());

        //expirationDate
        currentCourseInfo.setExpirationDate(changedCourseInfo.getExpirationDate());

	 //version (DO NOT COPY OVER)
        //meta (DO NOT COPY OVER)
        //attributes (again blow away and replace)
        currentCourseInfo.getAttributes().clear();
        currentCourseInfo.getAttributes().addAll(changedCourseInfo.getAttributes());
        for (AttributeInfo attribute : currentCourseInfo.getAttributes()) {
            attribute.setId(null);
        }
    }

    protected boolean preProcessCourseSave(IDocumentEvent iDocumentEvent, CourseInfo courseInfo) {
        return false;
    }

    protected CourseService getCourseService() {
        if (this.courseService == null) {
            this.courseService = (CourseService) GlobalResourceLoader.getService(new QName("http://student.kuali.org/wsdl/course","CourseService")); 
        }
        return this.courseService;
    }
    
    protected CourseStateChangeServiceImpl getCourseStateChangeService() {
        if (this.courseStateChangeService == null) {
            this.courseStateChangeService = new CourseStateChangeServiceImpl();
            this.courseStateChangeService.setCourseService(getCourseService());
        }
        return this.courseStateChangeService;
    } 
    
    private void resetLoRecursively(LoDisplayInfo lo, String stateKey) {
        //Clear out all the Lo ids recursively
        lo.setId(null);
        lo.setStateKey(stateKey);
        lo.setParentLoRelationid(null);
        lo.getLoInfo().setId(null);
        for (LoDisplayInfo nestedLo : lo.getLoDisplayInfoList()) {
            resetLoRecursively(nestedLo, stateKey);
        }
    }

    /*
     * Recursively set state for StatementTreeViewInfo
     * TODO: We are not able to reuse the code in CourseStateUtil for dependency reason.
     */   
    public void statementTreeViewInfoStateSetter(String courseState, Iterator<StatementTreeViewInfo> itr) {
    	while(itr.hasNext()) {
        	StatementTreeViewInfo statementTreeViewInfo = (StatementTreeViewInfo)itr.next();
        	statementTreeViewInfo.setState(courseState);
        	List<ReqComponentInfo> reqComponents = statementTreeViewInfo.getReqComponents();
        	for(Iterator<ReqComponentInfo> it = reqComponents.iterator(); it.hasNext();)
        		it.next().setState(courseState);

        	statementTreeViewInfoStateSetter(courseState, statementTreeViewInfo.getStatements().iterator());
        }
    }
}
