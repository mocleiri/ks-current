package org.kuali.student.ap.plan.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.CourseHelper;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.framework.context.TermHelper;
import org.kuali.student.ap.framework.course.CreditsFormatter;
import org.kuali.student.ap.framework.course.CreditsFormatter.Range;
import org.kuali.student.enrollment.acal.infc.Term;
import org.kuali.student.myplan.academicplan.dto.LearningPlanInfo;
import org.kuali.student.myplan.academicplan.dto.PlanItemInfo;
import org.kuali.student.myplan.academicplan.infc.PlanItem;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.lum.course.infc.Course;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Utility class for thread-local building of planner update events.
 * 
 * @author Mark Fyffe <mwfyffe@iu.edu>
 * @version 0.7.6
 */
public class PlanEventUtils {

	private static class EventsKey {
		@Override
		public int hashCode() {
			return EventsKey.class.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof EventsKey;
		}
	}

	private static final EventsKey EVENTS_KEY = new EventsKey();

	private static String getTotalCredits(String termId, String itemType) {
		BigDecimal plannedTotalMin = BigDecimal.ZERO;
		BigDecimal plannedTotalMax = BigDecimal.ZERO;

		LearningPlanInfo plan = KsapFrameworkServiceLocator.getPlanHelper()
				.getDefaultLearningPlan();

		List<PlanItemInfo> planItems;
		try {
			planItems = KsapFrameworkServiceLocator.getAcademicPlanService()
					.getPlanItemsInPlanByAtp(
							plan.getId(),
							termId,
							itemType,
							KsapFrameworkServiceLocator.getContext()
									.getContextInfo());
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("LP lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("LP lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("LP lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("LP lookup error", e);
		}

		for (PlanItemInfo planItem : planItems) {
			if (!PlanConstants.COURSE_TYPE.equals(planItem.getRefObjectType()))
				continue;

			BigDecimal credit = planItem.getCredit();
			if (credit != null) {
				plannedTotalMin = plannedTotalMin.add(credit);
				plannedTotalMax = plannedTotalMax.add(credit);
			} else {
				Course course = KsapFrameworkServiceLocator.getCourseHelper()
						.getCourseInfo(planItem.getRefObjectId());
				Range range = CreditsFormatter.getRange(course);
				plannedTotalMin = plannedTotalMin.add(range.getMin());
				plannedTotalMax = plannedTotalMax.add(range.getMax());
			}
		}

		return CreditsFormatter.formatCredits(new Range(plannedTotalMin,
				plannedTotalMax));
	}

	/**
	 * Remove context from a type key.
	 * 
	 * @param typeKey
	 *            A KS type key.
	 * @return The last contextual element in the type key.
	 */
	public static String formatTypeKey(String typeKey) {
		return typeKey.substring(typeKey.lastIndexOf(".") + 1);
	}

	/**
	 * Get a transactional events object.
	 * 
	 * @return A transaction events object builder.
	 */
	public static JsonObjectBuilder getEventsBuilder() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			JsonObjectBuilder rv = (JsonObjectBuilder) TransactionSynchronizationManager
					.getResource(EVENTS_KEY);

			if (rv == null) {
				rv = Json.createObjectBuilder();
				TransactionSynchronizationManager
						.registerSynchronization(new TransactionSynchronizationAdapter() {
							@Override
							public void afterCompletion(int status) {
								TransactionSynchronizationManager
										.unbindResourceIfPossible(EVENTS_KEY);
							}
						});
				TransactionSynchronizationManager.bindResource(EVENTS_KEY, rv);
			}

			return rv;

		} else
			return Json.createObjectBuilder();
	}

	/**
	 * Creates an add plan item event on the current transaction.
	 * 
	 * @param planItem
	 *            The plan item to report as added.
	 * @return The transactional events builder, with the add plan item event
	 *         added.
	 */
	public static JsonObjectBuilder makeAddEvent(PlanItem planItem) {
		CourseHelper courseHelper = KsapFrameworkServiceLocator
				.getCourseHelper();
		TermHelper termHelper = KsapFrameworkServiceLocator.getTermHelper();

		assert PlanConstants.COURSE_TYPE.equals(planItem.getRefObjectType()) : planItem
				.getRefObjectType() + " " + planItem.getId();

		Course course = courseHelper.getCourseInfo(planItem.getRefObjectId());
		assert course != null : "Missing course for plan item "
				+ planItem.getId() + ", ref ID " + planItem.getRefObjectId();

		JsonObjectBuilder addEvent = Json.createObjectBuilder();
		addEvent.add("uid", UUID.randomUUID().toString());
		addEvent.add("learningPlanId", planItem.getLearningPlanId());
		addEvent.add("planItemId", planItem.getId());
		addEvent.add("courseId", course.getId());
		addEvent.add("courseTitle", course.getCourseTitle());
		if (planItem.getCredit() != null) {
			addEvent.add("credits", CreditsFormatter.trimCredits(planItem
					.getCredit().toString()));
		} else {
			addEvent.add("credits", CreditsFormatter.formatCredits(course));
		}

		StringBuilder code = new StringBuilder(course.getCode());
		String campusCode = null, activityCode = null;
		for (Attribute attr : course.getAttributes()) {
			String key = attr.getKey();
			if ("campusCode".equals(key))
				campusCode = attr.getValue();
		}
		for (Attribute attr : planItem.getAttributes()) {
			String key = attr.getKey();
			if ("campusCode".equals(key))
				campusCode = attr.getValue();
			if ("activityCode".equals(key))
				activityCode = attr.getValue();
		}
		if (campusCode != null)
			code.insert(0, " ").insert(0, campusCode);
		if (activityCode != null)
			code.append(" ").append(activityCode);
		addEvent.add("code", code.toString());

		String type = formatTypeKey(planItem.getTypeKey());
		String menusuffix = "";

		List<String> planPeriods = planItem.getPlanPeriods();
		if (planPeriods != null && !planPeriods.isEmpty()) {
			String termId = planPeriods.get(0);

			Term term = termHelper.getTerm(termId);
			assert term != null : "Invalid term " + termId + " in plan item "
					+ planItem.getId();

			// NOTE: termId is used as a post parameter by the add event.
			// For other events, it is used as a selector so needs '.' replaced
			// by '-' The replacement is not desired here.
			addEvent.add("termId", termId);

			if ("planned".equals(type)
					&& campusCode != null
					&& KsapFrameworkServiceLocator.getShoppingCartStrategy()
							.isCartAvailable(termId, campusCode))
				menusuffix = "_cartavailable";
		}

		addEvent.add("type", type);
		addEvent.add("menusuffix", menusuffix);

		JsonObjectBuilder events = getEventsBuilder();
		events.add(PlanConstants.JS_EVENT_NAME.PLAN_ITEM_ADDED.name(), addEvent);
		return events;
	}

	public static JsonObjectBuilder makeRemoveEvent(String uniqueId,
			PlanItem planItem) {
		JsonObjectBuilder removeEvent = Json.createObjectBuilder();
		removeEvent.add("uid", uniqueId);
		removeEvent.add("planItemId", planItem.getId());
		removeEvent.add("type", formatTypeKey(planItem.getTypeKey()));

		// Only planned or backup items get an atpId attribute.
		if (planItem.getTypeKey().equals(
				PlanConstants.LEARNING_PLAN_ITEM_TYPE_PLANNED)
				|| planItem.getTypeKey().equals(
						PlanConstants.LEARNING_PLAN_ITEM_TYPE_BACKUP)
				|| planItem.getTypeKey().equals(
						PlanConstants.LEARNING_PLAN_ITEM_TYPE_CART)) {
			removeEvent.add("termId",
					planItem.getPlanPeriods().get(0).replace('.', '-'));
		}

		JsonObjectBuilder events = getEventsBuilder();
		events.add(PlanConstants.JS_EVENT_NAME.PLAN_ITEM_DELETED.name(),
				removeEvent);
		return events;
	}

	public static JsonObjectBuilder updatePlanItemCreditsEvent(String uniqueId,
			PlanItem planItem) {
		JsonObjectBuilder updateCreditsEvent = Json.createObjectBuilder();
		updateCreditsEvent.add("uniqueId", uniqueId);

		if (planItem.getCredit() != null) {
			updateCreditsEvent.add("credit", CreditsFormatter
					.trimCredits(planItem.getCredit().toString()));
		} else {
			Course course = KsapFrameworkServiceLocator.getCourseHelper()
					.getCourseInfo(planItem.getRefObjectId());
			updateCreditsEvent.add("credit",
					CreditsFormatter.formatCredits(course));
		}

		JsonObjectBuilder events = getEventsBuilder();
		events.add("PLAN_ITEM_UPDATED", updateCreditsEvent);
		return events;
	}

	public static JsonObjectBuilder updateTotalCreditsEvent(boolean newTerm,
			String termId) {
		JsonObjectBuilder updateTotalCreditsEvent = Json.createObjectBuilder();
		updateTotalCreditsEvent.add("termId", termId.replace('.', '-'));
		updateTotalCreditsEvent.add(
				"totalCredits",
				getTotalCredits(termId,
						PlanConstants.LEARNING_PLAN_ITEM_TYPE_PLANNED));
		updateTotalCreditsEvent.add(
				"cartCredits",
				getTotalCredits(termId,
						PlanConstants.LEARNING_PLAN_ITEM_TYPE_CART));

		JsonObjectBuilder events = getEventsBuilder();
		events.add(
				newTerm ? PlanConstants.JS_EVENT_NAME.UPDATE_NEW_TERM_TOTAL_CREDITS
						.name()
						: PlanConstants.JS_EVENT_NAME.UPDATE_OLD_TERM_TOTAL_CREDITS
								.name(), updateTotalCreditsEvent);
		return events;
	}

	public static JsonObjectBuilder updateTermNoteEvent(String uniqueId,
			String termNote) {
		JsonObjectBuilder updateTotalTermNoteEvent = Json.createObjectBuilder();
		updateTotalTermNoteEvent.add("uniqueId", uniqueId);
		updateTotalTermNoteEvent.add("termNote", termNote == null ? ""
				: termNote);
		JsonObjectBuilder events = getEventsBuilder();
		events.add(PlanConstants.JS_EVENT_NAME.TERM_NOTE_UPDATED.name(),
				updateTotalTermNoteEvent);
		return events;
	}

	public static void sendJsonEvents(boolean success, String message,
			HttpServletResponse response) throws IOException, ServletException {
		JsonObjectBuilder json = PlanEventUtils.getEventsBuilder();
		json.add("success", success);
		if (message != null)
			json.add("message", message);

		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Cache-Control", "No-cache");
		response.setHeader("Cache-Control", "No-store");
		response.setHeader("Cache-Control", "max-age=0");
		JsonWriter jwriter = Json.createWriter(response.getWriter());
		jwriter.writeObject(json.build());
		jwriter.close();
	}

	public static void sendRefresh(String componentId, HttpServletResponse response) throws IOException, ServletException {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("component", componentId);
		json.add("refresh", true);

		response.setContentType("application/json; charset=UTF-8");
		response.setHeader("Cache-Control", "No-cache");
		response.setHeader("Cache-Control", "No-store");
		response.setHeader("Cache-Control", "max-age=0");
		JsonWriter jwriter = Json.createWriter(response.getWriter());
		jwriter.writeObject(json.build());
		jwriter.close();
	}

	private PlanEventUtils() {
	}

}
