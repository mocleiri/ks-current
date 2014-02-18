package org.kuali.student.ap.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.support.DefaultKsapContext;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ks-ap-test-context.xml" })
@TransactionConfiguration(transactionManager = "JtaTxManager", defaultRollback = true)
@Transactional
public class DegreeMapServiceImplTest {

	@Before
	public void setUp() {
//		DefaultKsapContext.before("student1");
//
//        createType("kuali.academicplan.type.plan", "Learning Plan", "Student learning plan type.", "http://student.kuali.org/wsdl/acadplan/LearningPlanInfo");
//        createType("kuali.academicplan.type.plan.template", "Learning Plan Template", "Student learning plan template type.", "http://student.kuali.org/wsdl/acadplan/LearningPlanInfo");
//        createType("kuali.academicplan.type.plan.review", "Learning Plan Review", "Student learning plan review type.", "http://student.kuali.org/wsdl/acadplan/LearningPlanInfo");
//
//        createType("kuali.academicplan.type.item.course", "Course Item", "Student learning plan course item type.", "http://student.kuali.org/wsdl/acadplan/PlanItemInfo");
    }

	@After
	public void tearDown() {
		DefaultKsapContext.after();
	}

	@Test(expected = DoesNotExistException.class)
	public void getUnknownRequirement() throws InvalidParameterException,
			MissingParameterException, DoesNotExistException,
			OperationFailedException {
		KsapFrameworkServiceLocator.getDegreeMapService().getRequirement("unknown_requirement",
				KsapFrameworkServiceLocator.getContext().getContextInfo());
	}

//	@Test
//	public void getLearningPlan() throws Throwable {
//		String planId = "lp1";
//		LearningPlan learningPlan = KsapFrameworkServiceLocator.getAcademicPlanService().getLearningPlan(planId,
//				KsapFrameworkServiceLocator.getContext().getContextInfo());
//		assertNotNull(learningPlan);
//		assertEquals(planId, learningPlan.getId());
//		assertEquals("student1", learningPlan.getStudentId());
//		assertEquals("Student 1 Learning Plan 1", learningPlan.getDescr()
//				.getPlain());
//	}



//	@Test(expected = DoesNotExistException.class)
//	public void deleteLearningPlan() throws Exception {
//		String id = "lp1";
//
//		// Make sure the plan exists and has some plan items.
//		try {
//			KsapFrameworkServiceLocator.getAcademicPlanService().getLearningPlan(id, KsapFrameworkServiceLocator
//					.getContext().getContextInfo());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//
//		int itemCount = KsapFrameworkServiceLocator.getAcademicPlanService().getPlanItemsInPlan(id,
//				KsapFrameworkServiceLocator.getContext().getContextInfo())
//				.size();
//		assertEquals(8, itemCount);
//
//		// Delete the plan
//		try {
//			KsapFrameworkServiceLocator.getAcademicPlanService().deleteLearningPlan(id,
//					KsapFrameworkServiceLocator.getContext().getContextInfo());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//
//		// Make sure the plan items were cleaned up.
//		itemCount = KsapFrameworkServiceLocator.getAcademicPlanService().getPlanItemsInPlan(id,
//				KsapFrameworkServiceLocator.getContext().getContextInfo())
//				.size();
//		assertEquals(0, itemCount);
//
//		try {
//			KsapFrameworkServiceLocator.getAcademicPlanService().getLearningPlan(id, KsapFrameworkServiceLocator
//					.getContext().getContextInfo());
//		} catch (Exception e) {
//			// Swallow anything but DoesNotExistException.
//			if (e instanceof DoesNotExistException) {
//				throw e;
//			}
//		}
//	}
//


//	@Test
//	public void deletePlanItem() {
//		String id = "lp1";
//
//		// Make sure the plan exists and has some plan items.
//		try {
//			KsapFrameworkServiceLocator.getAcademicPlanService().getLearningPlan(id, KsapFrameworkServiceLocator
//					.getContext().getContextInfo());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//
//		List<PlanItemInfo> planItems = null;
//		try {
//			planItems = KsapFrameworkServiceLocator.getAcademicPlanService().getPlanItemsInPlan(id,
//					KsapFrameworkServiceLocator.getContext().getContextInfo());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		assertEquals(8, planItems.size());
//
//		// Delete a plan item.
//		String planItemId = planItems.get(0).getId();
//		try {
//			KsapFrameworkServiceLocator.getAcademicPlanService().deletePlanItem(planItemId,
//					KsapFrameworkServiceLocator.getContext().getContextInfo());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//
//		// Make sure the plan items were cleaned up.
//		int itemCount = 0;
//		try {
//			itemCount = KsapFrameworkServiceLocator.getAcademicPlanService().getPlanItemsInPlan(id,
//					KsapFrameworkServiceLocator.getContext().getContextInfo())
//					.size();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		assertEquals(7, itemCount);
//	}
//

	

//    private void createType(String typeKey, String typeName, String typeDescription, String refObjectUri) {
//        TypeInfo type = new TypeInfo();
//        type.setKey(typeKey);
//        type.setName(typeName);
//        type.setDescr(new RichTextHelper().fromPlain(typeDescription));
//        type.setRefObjectUri(refObjectUri);
//        type.setEffectiveDate(new Date());
//        boolean error = false;
//        try {
//            KsapFrameworkServiceLocator.getTypeService().createType(type.getKey(), type,
//                    KsapFrameworkServiceLocator.getContext().getContextInfo());
//        } catch (Exception e) {
//            error = true;
//        }
//        assertFalse(error);
//    }
}
