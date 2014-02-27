package org.kuali.student.ap.plan.support;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.plan.PlanItemForm;
import org.kuali.student.myplan.academicplan.infc.LearningPlan;
import org.kuali.student.myplan.academicplan.infc.PlanItem;
import org.kuali.student.r2.lum.course.infc.Course;

public final class PlanItemControllerHelper {

	public static LearningPlan getAuthorizedLearningPlan(PlanItemForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		LearningPlan plan = form.getLearningPlan();
		if (plan == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid learning plan ID " + form.getLearningPlanId());
			return null;
		}

		String studentId = KsapFrameworkServiceLocator.getUserSessionHelper().getStudentId();
		if (studentId == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing student ID " + form.getLearningPlanId());
			return null;
		}
		
		if (!KsapFrameworkServiceLocator.getUserSessionHelper().isAdviser() &&
				!studentId.equals(GlobalVariables.getUserSession().getPrincipalId())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Planner access denied");
			return null;
		}

		if (!studentId.equals(plan.getStudentId())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, request.getRemoteUser()
					+ " is not allowed to view or update plan " + plan.getId());
			return null;
		}

		return plan;
	}

	public static PlanItem getValidatedPlanItem(PlanItemForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		LearningPlan plan = getAuthorizedLearningPlan(form, request, response);
		if (plan == null)
			return null;

		String planItemId = form.getPlanItemId();

		PlanItem planItem = form.getPlanItem();
		if (planItem == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid plan item ID " + form.getPlanItemId());
			return null;
		}

		if (!planItem.getLearningPlanId().equals(plan.getId())) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Plan item " + planItemId + " is not associated with learning plan " + plan.getId() + ", found "
							+ planItem.getLearningPlanId());
			return null;
		}

		if (!PlanConstants.COURSE_TYPE.equals(planItem.getRefObjectType())) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plan item " + planItemId
					+ " does not refer to a course, found " + planItem.getRefObjectType());
			return null;
		}

		Course course = form.getCourse();
		if (course == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plan item " + planItemId
					+ " does not refer to a course, found " + planItem.getRefObjectId());
			return null;
		}

		String expectedTypeKey = form.getExpectedPlanItemType();
		if (expectedTypeKey != null && !planItem.getTypeKey().equals(expectedTypeKey)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plan item " + planItemId + " not of expected type "
					+ expectedTypeKey + ", found " + planItem.getTypeKey());
			return null;
		}

		String expectedTermId = form.getTermId();
		if (expectedTermId != null) {
			List<String> planPeriods = planItem.getPlanPeriods();
			if (planPeriods == null || planPeriods.isEmpty() || !expectedTermId.equals(planPeriods.get(0))) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Plan item " + planItemId
						+ " not from expected term " + expectedTypeKey + ", found " + planItem.getTypeKey());
				return null;
			}
		}

		return planItem;
	}

	private PlanItemControllerHelper() {
	}

}
