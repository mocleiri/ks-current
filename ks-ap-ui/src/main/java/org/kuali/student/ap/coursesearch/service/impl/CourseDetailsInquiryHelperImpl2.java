package org.kuali.student.ap.coursesearch.service.impl;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.student.ap.academicplan.dto.LearningPlanInfo;
import org.kuali.student.ap.academicplan.dto.PlanItemInfo;
import org.kuali.student.ap.academicplan.infc.PlanItem;
import org.kuali.student.ap.academicplan.constants.AcademicPlanServiceConstants;
import org.kuali.student.ap.coursesearch.dataobject.CourseDetailsWrapper;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.coursesearch.CreditsFormatter;
import org.kuali.student.ap.utils.CourseLinkBuilder;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.infc.RichText;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.course.infc.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Inquiry Helper for the CourseDetails-InquiryView
 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl
 */
public class CourseDetailsInquiryHelperImpl2 extends KualiInquirableImpl {

    private static final long serialVersionUID = 4933435913745621395L;

    private static final Logger LOG = LoggerFactory.getLogger(CourseDetailsInquiryHelperImpl.class);

    /**
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#retrieveDataObject(java.util.Map)
     */
    @Override
    public CourseDetailsWrapper retrieveDataObject(@SuppressWarnings("rawtypes") Map fieldValues) {
        String studentId = KsapFrameworkServiceLocator.getUserSessionHelper().getStudentId();
        return retrieveCourseDetails(
                (String) fieldValues.get(PlanConstants.PARAM_COURSE_ID),
                (String) fieldValues.get(PlanConstants.PARAM_TERM_ID),
                studentId,
                fieldValues.get(PlanConstants.PARAM_OFFERINGS_FLAG) != null
                        && Boolean.valueOf(fieldValues.get(PlanConstants.PARAM_OFFERINGS_FLAG).toString()));
    }

    /**
     * Retreive and compile data needed for the page into a single object
     *
     * @param courseId - Id of the course being displayed
     * @param termId -
     * @param studentId -
     * @param loadActivityOffering -
     * @return Compiled data object for inquiry page
     */
    public CourseDetailsWrapper retrieveCourseDetails(String courseId, String termId, String studentId,
                                                      boolean loadActivityOffering) {
        final CourseInfo course = KsapFrameworkServiceLocator.getCourseHelper().getCourseInfo(courseId);

        CourseDetailsWrapper courseDetails = retrieveCourseSummary(course);

        return courseDetails;
    }

    /**
     * Populates course details with catalog information (title, id, code, description) and other summary information
     *
     * @param course
     * @return
     */
    public CourseDetailsWrapper retrieveCourseSummary(Course course) {

        if (null == course) {
            return null;
        }

        CourseDetailsWrapper courseDetails = new CourseDetailsWrapper();

        courseDetails.setCourseId(course.getId());
        courseDetails.setCourseCode(course.getCode());
        courseDetails.setCourseCredits(CreditsFormatter.formatCredits(course));
        courseDetails.setCourseTitle(course.getCourseTitle());
        courseDetails.setSubject(course.getSubjectArea().trim());

        courseDetails.setCourseDescription(getCourseDescription(course));
        courseDetails.setCourseRequisites(getCourseRequisites(course));
        courseDetails.setCourseGenEdRequirements(getCourseGenEdRequirements(course));

        courseDetails.setScheduledTerms(KsapFrameworkServiceLocator.getCourseHelper()
                .getScheduledTermsForCourse(course));
        courseDetails.setProjectedTerms(getProjectedTerms(course));
        if(courseDetails.getScheduledTerms().isEmpty()){
            //courseDetails.setLastOffered(KsapFrameworkServiceLocator.getCourseHelper().getLastOfferedTermIdForCourse(course));
            courseDetails.setLastOffered(""); // Remove when last offered term functionality is implmented
        }else{
            courseDetails.setLastOffered("");
        }

        List<PlanItem> planItems = loadStudentsPlanItemsForCourse(course);

        courseDetails.setBookmarked(isBookmarked(course, planItems));

        return courseDetails;
    }

    /**
     * Retrieve and format the Course description to be displayed on the page
     *
     * @param course - Course that is being displayed
     * @return Formatted course description
     */
    protected String getCourseDescription(Course course){
        String description = "";
        RichText richTextDescription = course.getDescr();
        if(!StringUtils.isEmpty(richTextDescription.getFormatted())){
            description = richTextDescription.getFormatted();
        }else if(!StringUtils.isEmpty(richTextDescription.getPlain())){
            description = richTextDescription.getPlain();
        }

        description = CourseLinkBuilder.makeLinks(description,KsapFrameworkServiceLocator.getContext().getContextInfo());

        return description;
    }

    /**
     * Retrieve and format the list of course requisites to be displayed on the page
     *
     * @param course - Course that is being displayed
     * @return Formatted list of requisites
     */
    protected List<String> getCourseRequisites(Course course){
        List<String> courseRequisites = new ArrayList<String>();

        //empty

        return courseRequisites;
    }

    /**
     * Retrieve and format the list of general education requirements to be displayed on the page
     *
     * @param course - Course that is being displayed
     * @return Formatted list of general education requirements
     */
    protected List<String> getCourseGenEdRequirements(Course course){
        List<String> courseGenEdRequirements = new ArrayList<String>();

        // empty

        return courseGenEdRequirements;
    }

    /**
     * Retrieve and format the list of projected terms to be displayed on the page
     *
     * @param course - Course that is being displayed
     * @return Formatted list of projected terms
     */
    protected List<String> getProjectedTerms(Course course){
        List<String> courseTermsOffered = course.getTermsOffered();
        List<String> projectedTerms = new java.util.ArrayList<String>();
        if (courseTermsOffered != null) {
            try {
                for (TypeInfo typeInfo : KsapFrameworkServiceLocator.getTypeService().getTypesByKeys(courseTermsOffered,
                        KsapFrameworkServiceLocator.getContext().getContextInfo()))
                    projectedTerms.add(typeInfo.getName());
            } catch (org.kuali.student.r2.common.exceptions.DoesNotExistException e) {
                throw new IllegalArgumentException("Type lookup error", e);
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("Type lookup error", e);
            } catch (MissingParameterException e) {
                throw new IllegalArgumentException("Type lookup error", e);
            } catch (OperationFailedException e) {
                throw new IllegalStateException("Type lookup error", e);
            } catch (PermissionDeniedException e) {
                throw new IllegalStateException("Type lookup error", e);
            }
        }
        return projectedTerms;
    }

    /**
     * Determine if the course is already bookmarked in the student's plan
     *
     * @param course - Course that is being displayed
     * @param planItems - The list of plan items for the course
     * @return True if the course is already bookmarked for the plan
     */
    protected boolean isBookmarked(Course course, List<PlanItem> planItems){
        for(PlanItem item : planItems){
            if(item.getCategory().equals(AcademicPlanServiceConstants.ItemCategory.WISHLIST)){
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve the list of plan items for this course in the student's plan
     *
     * @param course - Course that is being displayed
     * @return A List of plan items related to the course.
     */
    protected List<PlanItem> loadStudentsPlanItemsForCourse(Course course) {
        String studentId = KsapFrameworkServiceLocator.getUserSessionHelper().getStudentId();
        if (studentId == null)
            return new ArrayList<PlanItem>();

        try {
            // Retrieve plan items for the student's default plan
            LearningPlanInfo learningPlan = KsapFrameworkServiceLocator.getPlanHelper().getDefaultLearningPlan();
            List<PlanItemInfo> planItems = KsapFrameworkServiceLocator.getAcademicPlanService().getPlanItemsInPlan(
                    learningPlan.getId(), KsapFrameworkServiceLocator.getContext().getContextInfo());
            List<PlanItem> planItemsForCourse = new ArrayList<PlanItem>();

            // Filter plan items by the course
            for(PlanItem item : planItems){
                if(item.getRefObjectId().equals(course.getId())){
                    planItemsForCourse.add(new PlanItemInfo(item));
                }
            }

            return planItemsForCourse;
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("LP lookup failure ", e);
        } catch (MissingParameterException e) {
            throw new IllegalStateException("LP lookup failure ", e);
        } catch (OperationFailedException e) {
            throw new IllegalStateException("LP lookup failure ", e);
        }
    }
}
