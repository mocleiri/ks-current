package org.kuali.student.ap.plan.ui;

import org.kuali.student.ap.plan.support.DefaultPlannerForm;
import org.kuali.student.myplan.course.dataobject.CourseSummaryDetails;
import org.kuali.student.myplan.course.service.CourseDetailsInquiryHelperImpl;

/**
 * Extends the default planner form to tie in course summary details from course
 * search in support of the course summary dialog.
 * 
 * @author Mark Fyffe <mwfyffe@iu.edu>
 * @version 0.7.6
 */
public class PlannerFormImpl extends DefaultPlannerForm {

	private static final long serialVersionUID = -3275492339094049260L;

	private transient CourseSummaryDetails courseSummaryDetails;

	@Override
	public void setLearningPlanId(String learningPlanId) {
		super.setLearningPlanId(learningPlanId);
		this.courseSummaryDetails = null;
	}

	@Override
	public void setPlanItemId(String planItemId) {
		super.setPlanItemId(planItemId);
		this.courseSummaryDetails = null;
	}

	@Override
	public void setCourseId(String courseId) {
		super.setCourseId(courseId);
		this.courseSummaryDetails = null;
	}

	public CourseSummaryDetails getCourseSummaryDetails() {
		if (courseSummaryDetails == null && getCourseId() != null)
			courseSummaryDetails = new CourseDetailsInquiryHelperImpl()
					.retrieveCourseSummaryById(getCourseId());

		return courseSummaryDetails;
	}

}
