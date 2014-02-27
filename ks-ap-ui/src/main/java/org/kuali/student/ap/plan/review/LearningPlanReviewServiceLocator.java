package org.kuali.student.ap.plan.review;

import javax.ejb.EJB;

import org.kuali.student.ap.framework.config.OptionalResource;

/**
 * Convenience factory for acquiring KSAP provided service.
 * 
 * @author Chris Maurer <chmaurer@iupui.edu>
 * @version 0.7.1
 * @deprecated TODO Migrate to KsapFrameworkServiceLocator when moving from
 *             sissrarm-service-kart to ks-ap.
 */
public final class LearningPlanReviewServiceLocator {

	/**
	 * Internally managed singleton instance.
	 */
	private static LearningPlanReviewServiceLocator instance;

	/**
	 * Get a singleton instance.
	 * <p>
	 * This method should be indicated as the factory method by at least one
	 * bean in an auto-wiring container in order to populate {@link EJB}
	 * instances.
	 * </p>
	 * 
	 * <pre>
	 * &lt;bean class=&quot;org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator&quot;
	 * 	factory-method=&quot;getInstance&quot; /&gt;
	 * </pre>
	 */
	public static LearningPlanReviewServiceLocator getInstance() {
		return instance == null ? instance = new LearningPlanReviewServiceLocator()
				: instance;
	}

	/**
	 * Get the schedule build strategy.
	 * 
	 * @return The schedule build strategy.
	 */
	public static LearningPlanReviewStrategy getLearningPlanReviewStrategy() {
		return getInstance().learningPlanReviewStrategy;
	}

	@OptionalResource
	@EJB
	private transient LearningPlanReviewStrategy learningPlanReviewStrategy;

	private LearningPlanReviewServiceLocator() {
	}

}
