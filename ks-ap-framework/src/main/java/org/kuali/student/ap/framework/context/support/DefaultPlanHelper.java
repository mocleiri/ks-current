package org.kuali.student.ap.framework.context.support;

import java.util.List;

import org.kuali.student.ap.academicplan.infc.DegreeMapRequirement;
import org.kuali.student.ap.academicplan.infc.Placeholder;
import org.kuali.student.ap.academicplan.infc.PlaceholderInstance;
import org.kuali.student.ap.academicplan.infc.TypedObjectReference;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.framework.context.PlanHelper;
import org.kuali.student.enrollment.courseoffering.infc.ActivityOffering;
import org.kuali.student.myplan.academicplan.dto.LearningPlanInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.lum.course.infc.Course;

/**
 * Default implementation of the PlanHelper
 */
public class DefaultPlanHelper implements PlanHelper {

	/**
	 * Retrieves the first plan item of type
	 * PlanConstants.Learning_Plan_Type_Plan for the student as the default
	 * plan.
	 * 
	 * @see org.kuali.student.ap.framework.context.PlanHelper
	 * 
	 * @return A single learning plan.
	 */
	@Override
	public LearningPlanInfo getDefaultLearningPlan() {
		LearningPlanInfo defaultPlan = null;
		String studentId = KsapFrameworkServiceLocator.getUserSessionHelper().getStudentId();

		List<LearningPlanInfo> learningPlans = null;
		try {
			learningPlans = KsapFrameworkServiceLocator.getAcademicPlanService().getLearningPlansForStudentByType(
					studentId, PlanConstants.LEARNING_PLAN_TYPE_PLAN,
					KsapFrameworkServiceLocator.getContext().getContextInfo());
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not fetch plan for user [%s].", studentId), e);
		}

		if (learningPlans == null) {
			throw new RuntimeException(String.format("Could not fetch plan for user [%s]. The query returned null.",
					studentId));
		}

		if (learningPlans.size() != 0) {
			defaultPlan = learningPlans.get(0);
		}

		return defaultPlan;
	}

    
    
	@Override
	public Course getCourse(TypedObjectReference ref) {
		if (ref.getRefObjectType().equals(PlanConstants.REF_TYPE_COURSE)){
			// need to load a course with the course id
			
			return KsapFrameworkServiceLocator.getCourseHelper().getCourseInfo(ref.getRefObjectId());
		}
		return null;
	}

	@Override
	public ActivityOffering getActivityOffering(TypedObjectReference ref) {
		// TODO: MARK will write a helper method that can be used to get the ActivityOffering by id.
		
		return null;
	}

	@Override
	public Placeholder getPlaceHolder(TypedObjectReference ref) {

		if (ref.getRefObjectType().equals(PlanConstants.REF_TYPE_PLACEHOLDER)) {
		    
			String placeholderId = ref.getRefObjectId();
			Placeholder placeholder;
			try {
				placeholder = KsapFrameworkServiceLocator
						.getDegreeMapService().getPlaceholder(
								placeholderId, KsapFrameworkServiceLocator.getContext().getContextInfo());
				return placeholder;
			} catch (DoesNotExistException e) {
				return null;
			} catch (InvalidParameterException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder with id [%s].", placeholderId), e);
			} catch (MissingParameterException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder with id [%s].", placeholderId), e);
			} catch (OperationFailedException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder with id [%s].", placeholderId), e);
			}

		}

		return null;
	}

	
	@Override
	public PlaceholderInstance getPlaceHolderInstance(TypedObjectReference ref) {
		if (ref.getRefObjectType().equals(PlanConstants.REF_TYPE_PLACEHOLDER_INSTANCE)) {
		    
			String pid = ref.getRefObjectId();
			PlaceholderInstance pi;
			try {
				pi = KsapFrameworkServiceLocator
						.getDegreeMapService().getPlaceholderInstance(
								pid, KsapFrameworkServiceLocator.getContext().getContextInfo());
				return pi;
			} catch (DoesNotExistException e) {
				return null;
			} catch (InvalidParameterException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder instance with id [%s].", pid), e);
			} catch (MissingParameterException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder instance with id [%s].", pid), e);
			} catch (OperationFailedException e) {
				throw new RuntimeException(String.format("Could not fetch placeholder instance with id [%s].", pid), e);
			}

		}

		return null;
	}
	
	
	@Override
	public DegreeMapRequirement getRequirement(TypedObjectReference ref) {
		if (ref.getRefObjectType().equals(PlanConstants.REF_TYPE_DEGREE_MAP_REQUIREMENT)) {
		    
			String rid = ref.getRefObjectId();
			DegreeMapRequirement dmr;
			try {
				dmr = KsapFrameworkServiceLocator
						.getDegreeMapService().getRequirement(
								rid, KsapFrameworkServiceLocator.getContext().getContextInfo());
				return dmr;
			} catch (DoesNotExistException e) {
				return null;
			} catch (InvalidParameterException e) {
				throw new RuntimeException(String.format("Could not fetch degree map requirement with id [%s].", rid), e);
			} catch (MissingParameterException e) {
				throw new RuntimeException(String.format("Could not fetch degree map requirement with id [%s].", rid), e);
			} catch (OperationFailedException e) {
				throw new RuntimeException(String.format("Could not fetch degree map requirement with id [%s].", rid), e);
			}

		}

		return null;
	}

 
    
}
