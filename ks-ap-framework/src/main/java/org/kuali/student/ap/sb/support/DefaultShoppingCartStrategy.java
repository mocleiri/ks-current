package org.kuali.student.ap.sb.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.CourseHelper;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.sb.ScheduleBuildStrategy;
import org.kuali.student.ap.sb.ShoppingCartRequest;
import org.kuali.student.ap.sb.ShoppingCartRequestInfo;
import org.kuali.student.ap.sb.ShoppingCartStrategy;
import org.kuali.student.ap.sb.dto.ActivityOptionInfo;
import org.kuali.student.ap.sb.dto.CourseOptionInfo;
import org.kuali.student.ap.sb.infc.ActivityOption;
import org.kuali.student.ap.sb.infc.CourseOption;
import org.kuali.student.ap.sb.infc.PossibleScheduleOption;
import org.kuali.student.ap.sb.infc.SecondaryActivityOptions;
import org.kuali.student.enrollment.acal.dto.TermInfo;
import org.kuali.student.enrollment.acal.infc.Term;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingDisplayInfo;
import org.kuali.student.myplan.academicplan.dto.PlanItemInfo;
import org.kuali.student.myplan.academicplan.infc.PlanItem;
import org.kuali.student.myplan.academicplan.service.AcademicPlanService;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.infc.Attribute;

public class DefaultShoppingCartStrategy implements ShoppingCartStrategy,
		Serializable {

	private static final long serialVersionUID = -5919246688332396604L;

	private static final Logger LOG = Logger.getLogger(DefaultShoppingCartStrategy.class);

	@Override
	public boolean isCartAvailable(String termId, String campusCode) {
		return false;
	}

	@Override
	public List<CourseOption> getCourseOptionsForPlanItem(Term term,
			PlanItem planItem) {
		ScheduleBuildStrategy scheduleBuildStrategy = KsapFrameworkServiceLocator
				.getScheduleBuildStrategy();
		assert PlanConstants.COURSE_TYPE.equals(planItem.getRefObjectType());
		assert planItem.getPlanPeriods().contains(term.getId());

		List<CourseOption> courseOptions = scheduleBuildStrategy
				.getCourseOptions(
						Collections.singletonList(planItem.getRefObjectId()),
						term.getId());

		List<String> acodes = Collections.emptyList();
		for (Attribute attr : planItem.getAttributes())
			if (attr.getKey().equals("activityCode"))
				acodes = Arrays.asList(attr.getValue().split(","));

		for (CourseOption courseOption : courseOptions) {
			for (ActivityOption primaryActivityOption : courseOption
					.getActivityOptions()) {
				boolean selected = acodes.contains(primaryActivityOption
						.getRegistrationCode());
				if (selected) {
					((ActivityOptionInfo) primaryActivityOption)
							.setSelected(true);
					for (SecondaryActivityOptions secondaryOptions : primaryActivityOption
							.getSecondaryOptions())
						for (ActivityOption secondaryActivityOption : secondaryOptions
								.getActivityOptions())
							if (primaryActivityOption.isEnrollmentGroup())
								((ActivityOptionInfo) secondaryActivityOption)
										.setLockedIn(primaryActivityOption
												.isEnrollmentGroup());
							else if (acodes.contains(secondaryActivityOption
									.getRegistrationCode()))
								((ActivityOptionInfo) secondaryActivityOption)
										.setSelected(true);
				}
			}

			// The selected flag on the course option dictates whether we are
			// adding or removing from the shopping cart when passed to
			// createRequests() --
			// When item type is cart, the request will be to remove from the
			// cart. For other items types, the request will be to add.
			((CourseOptionInfo) courseOption).setSelected(!planItem
					.getTypeKey().equals(
							PlanConstants.LEARNING_PLAN_ITEM_TYPE_CART));
		}
		return courseOptions;
	}

	@Override
	public List<ShoppingCartRequest> createRequests(Term term,
			List<CourseOption> courseOptions) {
		CourseHelper courseHelper = KsapFrameworkServiceLocator
				.getCourseHelper();
		List<ShoppingCartRequest> requests = new ArrayList<ShoppingCartRequest>(
				courseOptions.size());
		for (CourseOption courseOption : courseOptions) {
			ShoppingCartRequestInfo cartRequest = new ShoppingCartRequestInfo();
			cartRequest.setUniqueId(UUID.randomUUID().toString());
			cartRequest.setTerm(new TermInfo(term));
			cartRequest.setCourse(courseHelper.getCourseInfo(courseOption
					.getCourseId()));
			cartRequest.setAddToCart(courseOption.isSelected());

			ActivityOption primary = null;
			int secondaryLength = 0;
			for (ActivityOption primaryActivityOption : courseOption
					.getActivityOptions()) {
				if (primary == null && primaryActivityOption.isSelected())
					primary = primaryActivityOption;
				List<SecondaryActivityOptions> secondaryOptions = primaryActivityOption
						.getSecondaryOptions();
				if (secondaryOptions != null)
					secondaryLength = Math.max(secondaryLength,
							secondaryOptions.size());
			}

			List<String> secondaryRegCodes = new ArrayList<String>(
					secondaryLength);
			if (primary != null) {
				cartRequest.setPrimaryRegistrationCode(primary
						.getRegistrationCode());

				for (SecondaryActivityOptions secondaryOption : primary
						.getSecondaryOptions()) {
					ActivityOption secondary = null;
					for (ActivityOption secondaryActivityOption : secondaryOption
							.getActivityOptions())
						if (secondary == null
								&& secondaryActivityOption.isSelected())
							secondary = secondaryActivityOption;
					if (secondary != null)
						secondaryRegCodes.add(secondary.getRegistrationCode());
				}
				cartRequest.setSecondaryRegistrationCodes(secondaryRegCodes);
			}

			requests.add(cartRequest);
		}
		return requests;
	}

	private void buildExistingCartOptions(String learningPlanId,
			TermInfo termInfo, Map<String, ShoppingCartRequestInfo> rmap) {
		AcademicPlanService academicPlanService = KsapFrameworkServiceLocator
				.getAcademicPlanService();
		CourseHelper courseHelper = KsapFrameworkServiceLocator.getCourseHelper();
		ContextInfo context = KsapFrameworkServiceLocator.getContext()
				.getContextInfo();

		List<PlanItemInfo> planItems;
		try {
			planItems = academicPlanService.getPlanItemsInPlanByType(
					learningPlanId, PlanConstants.LEARNING_PLAN_ITEM_TYPE_CART,
					context);
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		}

		StringBuilder msg = null;
		if (LOG.isDebugEnabled())
			msg = new StringBuilder("Existing cart options");
		List<String> courseIds = new ArrayList<String>(planItems.size());
		Iterator<PlanItemInfo> planItemIter = planItems.iterator();
		while (planItemIter.hasNext()) {
			PlanItemInfo planItem = planItemIter.next();
			if (!PlanConstants.COURSE_TYPE.equals(planItem.getRefObjectType())) {
				planItemIter.remove();
				continue;
			}

			List<String> periods = planItem.getPlanPeriods();
			if (periods == null || !periods.contains(termInfo.getId())) {
				planItemIter.remove();
				continue;
			}

			String courseId = planItem.getRefObjectId();
			if (msg != null)
				msg.append("\n  Course ").append(courseId);

			String acodeattr = planItem.getAttributeValue("activityCode");
			if (acodeattr == null || acodeattr.isEmpty()) {
				if (msg != null)
					msg.append(" no activity codes");

				planItemIter.remove();
				continue;
			}

			if (msg != null)
				msg.append(" activity codes ").append(acodeattr);

			if (!courseIds.contains(courseId))
				courseIds.add(courseId);
		}

		courseHelper.frontLoad(courseIds, termInfo.getId());

		Map<String, String> primaryRegCode = new HashMap<String, String>();
		for (String courseId : courseIds) {
			for (ActivityOfferingDisplayInfo aodi : courseHelper
					.getActivityOfferingDisplaysByCourseAndTerm(courseId, termInfo.getId()))
				primaryRegCode.put(aodi.getActivityOfferingCode(),
						aodi.getAttributeValue("PrimaryActivityOfferingCode"));
		}

		for (PlanItemInfo planItem : planItems) {
			List<String> acodes = Arrays.asList(
					planItem.getAttributeValue("activityCode").split(","));

			for (String acode : acodes) {
				String primaryCode = primaryRegCode.get(acode);
				
				if (primaryCode == null) {
					LOG.warn("Registration code not found on in published course offerings " + acode);
					primaryCode = acode;
				}

				if (msg != null)
					msg.append("\n  Activity ").append(acode).append(" primary ")
							.append(primaryCode);

				ShoppingCartRequestInfo cartRequest = rmap.get(primaryCode);
				if (cartRequest == null) {
					cartRequest = new ShoppingCartRequestInfo();
					cartRequest.setUniqueId(UUID.randomUUID().toString());
					cartRequest.setAddToCart(false);
					cartRequest.setCourse(KsapFrameworkServiceLocator.getCourseHelper()
							.getCourseInfo(planItem.getRefObjectId()));
					cartRequest.setPrimaryRegistrationCode(primaryCode);
					cartRequest.setTerm(termInfo);
					rmap.put(primaryCode, cartRequest);
					if (msg != null)
						msg.append(" created");
				}

				List<String> sec = cartRequest.getSecondaryRegistrationCodes();
				if (sec == null)
					cartRequest.setSecondaryRegistrationCodes(sec = new ArrayList<String>());
				if (!primaryCode.equals(acode)) {
					if (msg != null)
						msg.append(" added secondary");
					sec.add(acode);
					rmap.put(acode, cartRequest);
				} else if (msg != null)
					msg.append(" reused");
			}

		}

		if (msg != null)
			LOG.debug(msg);
	}

	@Override
	public List<ShoppingCartRequest> createRequests(String learningPlanId,
			Term term, PossibleScheduleOption schedule) {
		TermInfo termInfo = new TermInfo(term);
		Map<String, ShoppingCartRequestInfo> rmap = new LinkedHashMap<String, ShoppingCartRequestInfo>();
		buildExistingCartOptions(learningPlanId, termInfo, rmap);

		StringBuilder msg = null;
		if (LOG.isDebugEnabled())
			msg = new StringBuilder("Cart requests for schedule");
		List<String> courseIds = new ArrayList<String>(schedule.getActivityOptions().size());
		Map<String, ActivityOption> aomap = new LinkedHashMap<String, ActivityOption>();
		for (ActivityOption ao : schedule.getActivityOptions()) {
			if (ao.isCourseLockedIn())
				continue;

			if (msg != null)
				msg.append("\n    Activity ").append(ao.getRegistrationCode());
			aomap.put(ao.getRegistrationCode(), ao);
			if (!rmap.containsKey(ao.getRegistrationCode())
					&& !courseIds.contains(ao.getCourseId()))
				courseIds.add(ao.getCourseId());
		}

		CourseHelper courseHelper = KsapFrameworkServiceLocator.getCourseHelper();
		courseHelper.frontLoad(courseIds, termInfo.getId());
		Map<String, String> primaryRegCode = new HashMap<String, String>();
		for (String courseId : courseIds) {
			for (ActivityOfferingDisplayInfo aodi : courseHelper
					.getActivityOfferingDisplaysByCourseAndTerm(courseId, termInfo.getId()))
				primaryRegCode.put(aodi.getActivityOfferingCode(),
						aodi.getAttributeValue("PrimaryActivityOfferingCode"));
		}

		Map<String, ShoppingCartRequestInfo> amap = new LinkedHashMap<String, ShoppingCartRequestInfo>();
		List<ShoppingCartRequest> rv = new ArrayList<ShoppingCartRequest>(
				aomap.size() + rmap.size());
		for (Entry<String, ActivityOption> aoe : aomap.entrySet()) {
			String regCode = aoe.getKey();
			ActivityOption ao = aoe.getValue();

			if (rmap.remove(regCode) == null) {
				String primaryCode = primaryRegCode.get(regCode);
				assert primaryCode != null : regCode;

				if (msg != null)
					msg.append("\n    Add ").append(regCode).append(" primary ")
							.append(primaryCode);

				ShoppingCartRequestInfo cartRequest = amap.get(primaryCode);
				if (cartRequest == null) {
					cartRequest = new ShoppingCartRequestInfo();
					cartRequest.setUniqueId(UUID.randomUUID().toString());
					cartRequest.setAddToCart(true);
					cartRequest.setCourse(courseHelper.getCourseInfo(ao.getCourseId()));
					cartRequest.setPrimaryRegistrationCode(primaryCode);
					cartRequest.setTerm(termInfo);
					amap.put(primaryCode, cartRequest);
					rv.add(cartRequest);
					if (msg != null)
						msg.append(" created");
				}

				List<String> sec = cartRequest.getSecondaryRegistrationCodes();
				if (sec == null)
					cartRequest.setSecondaryRegistrationCodes(sec = new ArrayList<String>());
				if (!primaryCode.equals(regCode)) {
					if (msg != null)
						msg.append(" added secondary");
					sec.add(regCode);
				} else if (msg != null)
					msg.append(" reused");

			} else if (msg != null)
				msg.append("\n    Keep ").append(regCode);
		}

		for (Entry<String, ShoppingCartRequestInfo> re : rmap.entrySet()) {
			if (re.getKey().equals(re.getValue().getPrimaryRegistrationCode())) {
				rv.add(re.getValue());
				if (msg != null)
					msg.append("\n    Remove ").append(re.getKey());
			}
		}

		if (msg != null)
			LOG.debug(msg);

		return rv;
	}

	@Override
	public List<ShoppingCartRequest> processRequests(
			List<ShoppingCartRequest> requests) {
		throw new UnsupportedOperationException(
				"Not implemented in KS - override at the institution level");
	}

}
