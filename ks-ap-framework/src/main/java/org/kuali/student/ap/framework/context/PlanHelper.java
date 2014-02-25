package org.kuali.student.ap.framework.context;

import org.kuali.student.ap.academicplan.dto.LearningPlanInfo;
import org.kuali.student.ap.academicplan.infc.DegreeMapRequirement;
import org.kuali.student.ap.academicplan.infc.Placeholder;
import org.kuali.student.ap.academicplan.infc.PlaceholderInstance;
import org.kuali.student.ap.academicplan.infc.TypedObjectReference;
import org.kuali.student.enrollment.courseoffering.infc.ActivityOffering;
import org.kuali.student.r2.core.acal.infc.Term;
import org.kuali.student.r2.lum.course.infc.Course;

import java.util.List;

/**
 * Helper that handles configurable actions for accessing learning plans and Plan items.
 */
public interface PlanHelper {

    /**
     * Retrieves the default learning plan.
     *
     * @return Default Learning Plan
     */
    public LearningPlanInfo getDefaultLearningPlan();

    /**
     * Gets the id of the term that the planner should display first.
     *
     * @return Term Id
     */
    public String getStartTermId();

    /**
     * Gets the list of Terms to use in the Planner Calendar using a Start Term.
     *
     * @param startTerm - Term that the calendar starts around
     * @return A full List of terms to display in the calendar.
     */
    public List<Term> getCalendarTerms(Term startTerm);
    
  
  /**
   * Get the course from a TypedOjbectReference
   * @param ref
   * @return course or null if the reference is not for a course
   */
  public Course getCourse(TypedObjectReference ref);
  

  /**
   * Get the ActivityOffering from a TypedOjbectReference
   * @param ref
   * @return activity offering or null if the reference is not for an activity offering.
   */

  public ActivityOffering getActivityOffering(TypedObjectReference ref);


  /**
   * Get the placeholder from a TypedOjbectReference
   * @param ref
   * @return placeholder or null if the reference is not for a placeholder.
   */

  public Placeholder getPlaceHolder(TypedObjectReference ref);
  
  
  /**
   * Get the placeholder instance from a TypedOjbectReference
   * @param ref
   * @return placeholder instance or null if the reference is not for a placeholder instance.
   */

  public PlaceholderInstance getPlaceHolderInstance(TypedObjectReference ref);

  
  
  /**
   * Get the degree map requirement from a TypedOjbectReference
   * @param ref
   * @return degree map requirement or null if the reference is not for a degree map requirement
   */

  public DegreeMapRequirement getRequirement(TypedObjectReference ref);
    
  

}
