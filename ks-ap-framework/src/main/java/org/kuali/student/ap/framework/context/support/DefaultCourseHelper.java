package org.kuali.student.ap.framework.context.support;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.CourseHelper;
import org.kuali.student.ap.framework.context.CourseSearchConstants;
import org.kuali.student.ap.framework.context.DeconstructedCourseCode;
import org.kuali.student.ap.framework.context.YearTerm;
import org.kuali.student.enrollment.acal.dto.TermInfo;
import org.kuali.student.enrollment.acal.infc.Term;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingDisplayInfo;
import org.kuali.student.enrollment.courseoffering.dto.ActivityOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.enrollment.courseoffering.dto.FormatOfferingInfo;
import org.kuali.student.enrollment.courseoffering.service.CourseOfferingService;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.search.infc.SearchRequest;
import org.kuali.student.r2.core.search.infc.SearchResult;
import org.kuali.student.r2.core.search.infc.SearchResultCell;
import org.kuali.student.r2.core.search.infc.SearchResultRow;
import org.kuali.student.r2.lum.course.dto.CourseInfo;
import org.kuali.student.r2.lum.course.infc.Course;
import org.kuali.student.r2.lum.course.service.CourseService;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DefaultCourseHelper implements CourseHelper, Serializable {

	private static final long serialVersionUID = 8000868050066661992L;

	private static final Logger LOG = Logger.getLogger(DefaultCourseHelper.class);

	private static Map<String, Reference<CourseInfo>> COURSE_CACHE =
			new HashMap<String, Reference<CourseInfo>>();

	private static Map<CourseTermKey, Reference<List<ActivityOfferingDisplayInfo>>> AOD_CACHE =
			new HashMap<CourseTermKey, Reference<List<ActivityOfferingDisplayInfo>>>();

	private static ReferenceQueue<?> CACHE_Q = new ReferenceQueue<Object>();

	private static class CacheReference<T> extends SoftReference<T> {

		private final Map<?, ?> map;
		private final Object key;

		private CacheReference(T referent, Map<?, ?> map,
				Object key, ReferenceQueue<? super T> q) {
			super(referent, q);
			this.map = map;
			this.key = key;
		}

	}

	private static void pruneCache() {
		CacheReference<?> cacheRef;
		while ((cacheRef = (CacheReference<?>) CACHE_Q.poll()) != null)
			synchronized (cacheRef.map) {
				cacheRef.map.remove(cacheRef.key);
			}
	}

	private static final CourseMarkerKey COURSE_MARKER_KEY = new CourseMarkerKey();

	private static class CourseMarkerKey {
		@Override
		public int hashCode() {
			return CourseMarkerKey.class.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof CourseMarkerKey;
		}
	}

	private static class CourseTermKey {
		private final String courseId;
		private final String termId;

		private CourseTermKey(String courseId, String termId) {
			super();
			this.courseId = courseId;
			this.termId = termId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((courseId == null) ? 0 : courseId.hashCode());
			result = prime * result + ((termId == null) ? 0 : termId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CourseTermKey other = (CourseTermKey) obj;
			if (courseId == null) {
				if (other.courseId != null)
					return false;
			} else if (!courseId.equals(other.courseId))
				return false;
			if (termId == null) {
				if (other.termId != null)
					return false;
			} else if (!termId.equals(other.termId))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CourseTermKey [courseId=" + courseId + ", termId=" + termId + "]";
		}
	}

	private static class CourseMarker {
		private Map<String, CourseInfo> coursesById = new java.util.HashMap<String, CourseInfo>();
		private Map<CourseTermKey, List<ActivityOfferingDisplayInfo>> aodByCourseAndTerm =
				new HashMap<CourseTermKey, List<ActivityOfferingDisplayInfo>>();

		private void cache(CourseInfo courseInfo) {
			if (courseInfo != null) {
				String courseId = courseInfo.getId();
				coursesById.put(courseId, courseInfo);

				@SuppressWarnings("unchecked")
				ReferenceQueue<? super CourseInfo> q = (ReferenceQueue<? super CourseInfo>) CACHE_Q;
				synchronized (COURSE_CACHE) {
					COURSE_CACHE.put(courseId,
							new CacheReference<CourseInfo>(courseInfo, COURSE_CACHE, courseId, q));
				}
			}
		}

		private void cache(List<CourseInfo> courseInfos) {
			if (courseInfos != null)
				for (CourseInfo courseInfo : courseInfos)
					cache(courseInfo);
		}

		private CourseInfo getCourse(String courseId) {
			CourseInfo course = coursesById.get(courseId);

			if (course == null) {
				pruneCache();
				Reference<CourseInfo> ref = COURSE_CACHE.get(courseId);
				course = ref == null ? null : ref.get();
			}

			return course;
		}

		private void cache(CourseTermKey key, List<ActivityOfferingDisplayInfo> aod) {
			if (aod != null) {
				aodByCourseAndTerm.put(key, aod);

				@SuppressWarnings("unchecked")
				ReferenceQueue<? super List<ActivityOfferingDisplayInfo>> q =
						(ReferenceQueue<? super List<ActivityOfferingDisplayInfo>>) CACHE_Q;
				synchronized (AOD_CACHE) {
					AOD_CACHE.put(key,
							new CacheReference<List<ActivityOfferingDisplayInfo>>(
									aod, AOD_CACHE, key, q));
				}
			}
		}

		private List<ActivityOfferingDisplayInfo> getActivityOfferings(CourseTermKey key) {
			List<ActivityOfferingDisplayInfo> aod = aodByCourseAndTerm.get(key);

			if (aod == null) {
				pruneCache();
				Reference<List<ActivityOfferingDisplayInfo>> ref = AOD_CACHE.get(key);
				aod = ref == null ? null : ref.get();
			}

			return aod;
		}

		private void frontLoadCourses(List<String> courseIds) {
			StringBuilder sb = null;
			if (LOG.isDebugEnabled())
				sb = new StringBuilder("Front load courses " + courseIds);

			CourseService courseService = KsapFrameworkServiceLocator.getCourseService();
			ContextInfo context = KsapFrameworkServiceLocator.getContext().getContextInfo();

			List<String> pullCourseIds = new ArrayList<String>(courseIds);
			List<CourseInfo> courses = new ArrayList<CourseInfo>(pullCourseIds.size());
			Iterator<String> pullIdIter = pullCourseIds.iterator();
			while (pullIdIter.hasNext()) {
				String pullId = pullIdIter.next();
				CourseInfo course = getCourse(pullId);
				if (course != null) {
					courses.add(course);
					pullIdIter.remove();

					if (sb != null) {
						sb.append("\n    ");
						sb.append(course.getCode());
						sb.append(" ");
						sb.append(course.getCourseTitle());
						sb.append(" cached");
					}
				}
			}

			if (!pullCourseIds.isEmpty()) {
				List<CourseInfo> pullCourses;
				try {
					pullCourses = courseService.getCoursesByIds(pullCourseIds, context);
				} catch (DoesNotExistException e) {
					throw new IllegalArgumentException("CO lookup error", e);
				} catch (InvalidParameterException e) {
					throw new IllegalArgumentException("CO lookup error", e);
				} catch (MissingParameterException e) {
					throw new IllegalArgumentException("CO lookup error", e);
				} catch (OperationFailedException e) {
					throw new IllegalStateException("CO lookup error", e);
				} catch (PermissionDeniedException e) {
					throw new IllegalStateException("CO lookup error", e);
				}

				cache(pullCourses);
				courses.addAll(pullCourses);

				if (sb != null) {
					for (CourseInfo course : pullCourses) {
						sb.append("\n    ");
						sb.append(course.getCode());
						sb.append(" ");
						sb.append(course.getCourseTitle());
						sb.append(" pulled");
					}
				}
			}

			if (sb != null)
				LOG.debug(sb);
		}

		private void frontLoadActivityOfferings(List<String> courseIds, String[] termIds) {
			StringBuilder sb = null;
			if (LOG.isDebugEnabled())
				sb = new StringBuilder("Front load activity offerings " + courseIds);

			List<String> pullCourseIds = new ArrayList<String>(courseIds);
			Iterator<String> pullIdIter = pullCourseIds.iterator();
			while (pullIdIter.hasNext()) {
				String pullId = pullIdIter.next();

				boolean all = true;
				for (String termId : termIds) {
					CourseTermKey k = new CourseTermKey(pullId, termId);
					List<ActivityOfferingDisplayInfo> aodl = getActivityOfferings(k);
					if (aodl == null) {
						all = false;
						break;
					}
				}

				if (all) {
					if (sb != null)
						sb.append("\n  Course ").append(pullId).append(" cached");
					pullIdIter.remove();
				}
			}
			if (pullCourseIds.isEmpty()) {
				if (sb != null)
					LOG.debug(sb);
				return;
			}

			try {
				CourseOfferingService courseOfferingService = KsapFrameworkServiceLocator
						.getCourseOfferingService();
				ContextInfo context = KsapFrameworkServiceLocator.getContext().getContextInfo();
				QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(PredicateFactory
						.and(
								PredicateFactory.in("cluId", pullCourseIds.toArray()),
								PredicateFactory.in("atpId", termIds)));

				List<CourseOfferingInfo> cos =
						courseOfferingService.searchForCourseOfferings(query, context);
				if (cos == null || cos.isEmpty())
					return;
				if (sb != null)
					sb.append("\nCourse Offerings:");

				Map<String, List<CourseTermKey>> coid2key =
						new java.util.HashMap<String, List<CourseTermKey>>();
				List<String> additionalCourseIds = new LinkedList<String>();
				Queue<String> coCluIds = new LinkedList<String>();
				for (CourseOfferingInfo co : cos) {
					coCluIds.offer(co.getCourseId());
					String altCourseIds = co.getAttributeValue("alternateCourseIds");
					if (altCourseIds != null)
						for (String altCourseId : altCourseIds.split(","))
							coCluIds.offer(altCourseId);
					while (!coCluIds.isEmpty()) {
						String courseId = coCluIds.poll();
						Course c = getCourse(courseId);
						if (c == null)
							additionalCourseIds.add(courseId);
						if (sb != null) {
							sb.append("\n    ");
							sb.append(co.getId());
							if (c == null)
								sb.append(" course queued");
							else {
								sb.append(" -> ");
								sb.append(c.getCode());
							}
							sb.append(" ");
							sb.append(courseId);
						}
						CourseTermKey k = new CourseTermKey(courseId, co.getTermId());
						List<CourseTermKey> kl = coid2key.get(co.getId());
						if (kl == null)
							coid2key.put(co.getId(), kl = new ArrayList<CourseTermKey>());
						if (!kl.contains(k))
							kl.add(k);
					}
				}

				if (!additionalCourseIds.isEmpty())
					frontLoadCourses(additionalCourseIds);

				List<FormatOfferingInfo> fos =
						courseOfferingService.searchForFormatOfferings(query, context);
				Map<String, String> foid2coid = new java.util.HashMap<String, String>();
				if (sb != null)
					sb.append("\nFormat Offerings:");
				for (FormatOfferingInfo fo : fos) {
					List<CourseTermKey> kl = coid2key.get(fo.getCourseOfferingId());
					assert kl != null && !kl.isEmpty();
					for (CourseTermKey k : kl) {
						if (sb != null) {
							sb.append("\n    ");
							sb.append(fo.getId());
							sb.append(" -> ");
							sb.append(fo.getCourseOfferingId());
							sb.append(" -> ");
							sb.append(k.courseId);
						}
						foid2coid.put(fo.getId(), fo.getCourseOfferingId());
					}
				}

				List<String> aoids =
						courseOfferingService.searchForActivityOfferingIds(query, context);
				List<ActivityOfferingDisplayInfo> aods =
						courseOfferingService.getActivityOfferingDisplaysByIds(aoids, context);
				Map<CourseTermKey, List<ActivityOfferingDisplayInfo>> aodmap =
						new HashMap<CourseTermKey, List<ActivityOfferingDisplayInfo>>();
				if (sb != null)
					sb.append("\nActivity Offerings:");
				for (ActivityOfferingDisplayInfo aodi : aods) {
					String coid = foid2coid.get(aodi.getFormatOfferingId());
					assert coid != null : aodi.getId() + " " + aodi.getFormatOfferingId();
					List<CourseTermKey> kl = coid2key.get(coid);
					assert kl != null && !kl.isEmpty();
					for (CourseTermKey k : kl) {
						assert k != null : aodi.getId() + " " + aodi.getFormatOfferingId() + " "
								+ coid;
						List<ActivityOfferingDisplayInfo> aodByCo = aodmap
								.get(k);
						if (aodByCo == null)
							aodmap.put(
									k,
									aodByCo = new java.util.LinkedList<ActivityOfferingDisplayInfo>());
						aodByCo.add(aodi);
						if (sb != null) {
							sb.append("\n    ");
							sb.append(aodi.getId());
							sb.append(" -> ");
							sb.append(aodi.getFormatOfferingId());
							sb.append(" -> ");
							sb.append(coid);
							sb.append(" -> ");
							sb.append(k.courseId);
						}
					}
				}

				for (Entry<CourseTermKey, List<ActivityOfferingDisplayInfo>> aode : aodmap
						.entrySet())
					cache(aode.getKey(), aode.getValue());

				if (sb != null)
					LOG.debug(sb);

			} catch (DoesNotExistException e) {
				throw new IllegalArgumentException("CO lookup error", e);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("CO lookup error", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("CO lookup error", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("CO lookup error", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("CO lookup error", e);
			}
		}

	}

	private static CourseMarker getCourseMarker() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			CourseMarker rv = (CourseMarker) TransactionSynchronizationManager
					.getResource(COURSE_MARKER_KEY);
			if (rv == null) {
				TransactionSynchronizationManager.bindResource(COURSE_MARKER_KEY,
						rv = new CourseMarker());
				TransactionSynchronizationManager
						.registerSynchronization(new TransactionSynchronizationAdapter() {
							@Override
							public void afterCompletion(int status) {
								TransactionSynchronizationManager
										.unbindResourceIfPossible(COURSE_MARKER_KEY);
							}
						});
			}
			return rv;
		} else {
			return new CourseMarker();
		}
	}

	@Override
	public void frontLoad(List<String> courseIds, String... termId) {
		CourseMarker cm = getCourseMarker();
		cm.frontLoadCourses(courseIds);

		if (termId != null && termId.length > 0)
			cm.frontLoadActivityOfferings(courseIds, termId);
	}

	/**
	 * returns the courseInfo for the given courseId by verifying the courId to
	 * be a verifiedcourseId
	 * 
	 * @param courseId
	 * @return
	 */
	public CourseInfo getCourseInfo(String courseId) {
		CourseMarker cm = getCourseMarker();
		// call through service locator to facilitate proxying delegate override of getVerifiedCourseId
		String verifiedCourseId = KsapFrameworkServiceLocator.getCourseHelper()
				.getVerifiedCourseId(courseId);
		CourseInfo rv = cm.getCourse(verifiedCourseId);
		if (rv == null)
			try {
				cm.cache(rv = KsapFrameworkServiceLocator.getCourseService().getCourse(
						verifiedCourseId,
						KsapFrameworkServiceLocator.getContext().getContextInfo()));
			} catch (DoesNotExistException e) {
				return null;
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("CLU lookup error", e);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("CLU lookup error", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("CLU lookup error", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("CLU lookup error", e);
			}
		return rv;
	}

	@Override
	public List<ActivityOfferingDisplayInfo> getActivityOfferingDisplaysByCourseAndTerm(
			String courseId, String termId) {
		CourseMarker cm = getCourseMarker();
		cm.frontLoadActivityOfferings(
				Collections.<String> singletonList(courseId), new String[] { termId });
		CourseTermKey k = new CourseTermKey(courseId, termId);
		List<ActivityOfferingDisplayInfo> rv = cm.getActivityOfferings(k);
		if (rv == null)
			cm.cache(k, rv = Collections.emptyList());
		return rv;
	}

	@Override
	public Map<String, Map<String, Object>> getAllSectionStatus(
			Map<String, Map<String, Object>> status,
			String courseId, String termId) {
		try {
			String xtermId = termId.replace('.', '-').intern();
			for (CourseOfferingInfo co : KsapFrameworkServiceLocator.getCourseOfferingService()
					.getCourseOfferingsByCourseAndTerm(courseId, termId,
							KsapFrameworkServiceLocator.getContext().getContextInfo()))
				for (ActivityOfferingInfo ao : KsapFrameworkServiceLocator
						.getCourseOfferingService()
						.getActivityOfferingsByCourseOffering(co.getId(),
								KsapFrameworkServiceLocator.getContext().getContextInfo())) {
					Map<String, Object> enrl = new java.util.LinkedHashMap<String, Object>();
					enrl.put("enrollMaximum", ao.getMaximumEnrollment());
					// TODO: convert remaining attributes to service lookup
					// bean properties
					enrl.put("enrollCount", ao.getAttributeValue("enrollCount"));
					enrl.put("enrollOpen", ao.getAttributeValue("enrollOpen"));
					enrl.put("enrollEstimate", ao.getAttributeValue("enrollEstimate"));
					String key = "enrl_" + xtermId + "_" + ao.getActivityCode();
					status.put(key, enrl);
				}
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		}
		return status;
	}

	public List<String> getScheduledTerms(Course course) {
		ContextInfo ctx = KsapFrameworkServiceLocator.getContext().getContextInfo();
		try {
			List<String> courseIds = KsapFrameworkServiceLocator.getCourseService()
					.searchForCourseIds(
							QueryByCriteria.Builder.fromPredicates(PredicateFactory.equal(
									"officialIdentifier.code",
									course.getCode())), ctx);
			List<String> scheduledTerms = new java.util.LinkedList<String>();
			for (Term t : KsapFrameworkServiceLocator.getTermHelper().getPublishedTerms()) {
				String termId = t.getId();
				QueryByCriteria crit = QueryByCriteria.Builder.fromPredicates(PredicateFactory.and(
						PredicateFactory.in("cluId",
								courseIds.toArray(new String[courseIds.size()])),
						PredicateFactory.equal("atpId", termId)));
				if (!KsapFrameworkServiceLocator
						.getCourseOfferingService()
						.searchForCourseOfferingIds(crit,
								KsapFrameworkServiceLocator.getContext().getContextInfo())
						.isEmpty())
					scheduledTerms.add(t.getId());
			}
			return scheduledTerms;
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		}
	}

	@Override
	public String getLastOfferedTermId(Course course) {
		ContextInfo ctx = KsapFrameworkServiceLocator.getContext().getContextInfo();
		List<CourseOfferingInfo> courseOfferingInfo = null;
		try {
			List<String> courseIds = KsapFrameworkServiceLocator.getCourseService()
					.searchForCourseIds(
							QueryByCriteria.Builder.fromPredicates(PredicateFactory.equal(
									"officialIdentifier.code",
									course.getCode())), ctx);
			String termId = KsapFrameworkServiceLocator.getTermHelper().getOldestHistoricalTerm()
					.getId();
			QueryByCriteria crit = QueryByCriteria.Builder.fromPredicates(PredicateFactory.and(
					PredicateFactory.in("cluId", courseIds.toArray(new String[courseIds.size()])),
					PredicateFactory.greaterThanOrEqual("atpId", termId)));
			courseOfferingInfo = KsapFrameworkServiceLocator.getCourseOfferingService()
					.searchForCourseOfferings(crit,
							ctx);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup failure", e);
		}
		if (courseOfferingInfo != null && courseOfferingInfo.size() > 0) {
			TermInfo lo;
			try {
				lo = KsapFrameworkServiceLocator.getAcademicCalendarService().getTerm(
						courseOfferingInfo.get(0).getTermId(),
						KsapFrameworkServiceLocator.getContext().getContextInfo());
			} catch (org.kuali.student.r2.common.exceptions.DoesNotExistException e) {
				throw new IllegalArgumentException("AC lookup failure", e);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("AC lookup failure", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("AC lookup failure", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("AC lookup failure", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("AC lookup failure", e);
			}
			return lo.getName();
		} else
			return null;
	}

	/**
	 * Used to Split the course code into division and Code. eg: "COM 243" is
	 * returned as CourseCode with division=COM and number=243 and section=null.
	 * eg: "COM 243 A" is returned as CourseCode with division=COM , number=243
	 * and section=A.
	 * 
	 * @param courseCode
	 * @return
	 */
	@Override
	public DeconstructedCourseCode getCourseDivisionAndNumber(String courseCode) {
		// TODO: Evaluate whether or not this code is UW specific.
		String subject = null;
		String number = null;
		String activityCd = null;
		if (courseCode.matches(CourseSearchConstants.FORMATTED_COURSE_CODE_REGEX)) {
			String[] splitStr = courseCode.toUpperCase().split(
					CourseSearchConstants.SPLIT_DIGITS_ALPHABETS);
			subject = splitStr[0].trim();
			number = splitStr[1].trim();
		} else if (courseCode.matches(CourseSearchConstants.COURSE_CODE_WITH_SECTION_REGEX)) {
			activityCd = courseCode.substring(courseCode.lastIndexOf(" "), courseCode.length())
					.trim();
			courseCode = courseCode.substring(0, courseCode.lastIndexOf(" ")).trim();
			String[] splitStr = courseCode.toUpperCase().split(
					CourseSearchConstants.SPLIT_DIGITS_ALPHABETS);
			subject = splitStr[0].trim();
			number = splitStr[1].trim();
		} else if (courseCode.matches(CourseSearchConstants.UNFORMATTED_COURSE_CODE_REGEX)) {
			String[] splitStr = courseCode.toUpperCase().split(
					CourseSearchConstants.SPLIT_DIGITS_ALPHABETS);
			subject = splitStr[0].trim();
			number = splitStr[1].trim();
		}
		return new DefaultDeconstructedCourseCode(subject, number, activityCd);
	}

	/**
	 * Used to get the course Id for the given subject area and course number
	 * (CHEM, 120) Uses last scheduled term to calculate the course Id
	 * 
	 * @param subjectArea
	 * @param number
	 * @return
	 */
	@Override
	public String getCourseId(String subjectArea, String number) {
		return getCourseIdForTerm(subjectArea, number, KsapFrameworkServiceLocator.getTermHelper()
				.getLastScheduledTerm().getId());
	}

	@Override
	public List<Course> getCoursesByCode(String courseCd) {
		try {
			return new ArrayList<Course>(KsapFrameworkServiceLocator.getCourseService()
					.searchForCourses(
							QueryByCriteria.Builder.fromPredicates(PredicateFactory.equal(
									"officialIdentifier.code",
									courseCd)),
							KsapFrameworkServiceLocator.getContext().getContextInfo()));
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Course lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Course lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("Course lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("Course lookup error", e);
		}
	}

	/**
	 * Used to get the course Id for the given subject area and course number
	 * (CHEM, 120) for a given term
	 * 
	 * @param subjectArea
	 * @param number
	 * @return
	 */
	@Override
	public String getCourseIdForTerm(String subjectArea, String number, String termId) {
		List<SearchRequest> requests = new ArrayList<SearchRequest>();
		SearchRequestInfo request = new SearchRequestInfo(
				CourseSearchConstants.COURSE_SEARCH_FOR_COURSE_ID);
		request.addParam(CourseSearchConstants.SEARCH_REQUEST_SUBJECT_PARAM, subjectArea.trim());
		request.addParam(CourseSearchConstants.SEARCH_REQUEST_NUMBER_PARAM, number.trim());
		request.addParam(CourseSearchConstants.SEARCH_REQUEST_LAST_SCHEDULED_PARAM, termId);
		requests.add(request);
		SearchResultInfo searchResult = new SearchResultInfo();
		try {
			searchResult = KsapFrameworkServiceLocator.getCluService().search(request,
					KsapFrameworkServiceLocator.getContext().getContextInfo());
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CLU lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CLU lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CLU lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CLU lookup error", e);
		}
		return searchResult.getRows().size() > 0 ? searchResult.getRows().get(0).getCells().get(0)
				.getValue() : null;
	}

	/**
	 * @param delimiter
	 * @param list
	 * @return
	 */
	public String joinStringsByDelimiter(char delimiter, String... list) {
		return StringUtils.join(list, delimiter);
	}

	/**
	 * Takes a courseId that can be either a version independent Id or a version
	 * dependent Id and returns a version dependent Id. In case of being passed
	 * in a version depend
	 * 
	 * @param courseId
	 * @return
	 */
	@Override
	public String getVerifiedCourseId(String courseId) {
		SearchRequestInfo req = new SearchRequestInfo("myplan.course.version.id");
		req.addParam("courseId", courseId);
		req.addParam("lastScheduledTerm", KsapFrameworkServiceLocator.getTermHelper()
				.getLastScheduledTerm().getId());
		SearchResult result;
		try {
			result = KsapFrameworkServiceLocator.getCluService().search(req,
					KsapFrameworkServiceLocator.getContext().getContextInfo());
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CLU lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CLU lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CLU lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CLU lookup error", e);
		}
		for (SearchResultRow row : result.getRows())
			for (SearchResultCell cell : row.getCells())
				if ("lu.resultColumn.cluId".equals(cell.getKey()))
					return cell.getValue();
		return null;
	}

	/**
	 * retuns a SLN for given params
	 * 
	 * @param year
	 * @param term
	 * @param subject
	 * @param number
	 * @param activityCd
	 * @return
	 */
	public String getSLN(String year, String term, String subject, String number, String activityCd) {
		String activityId = joinStringsByDelimiter(':', year, term, subject, number, activityCd);
		ActivityOfferingDisplayInfo activityOfferingInfo = null;
		try {
			activityOfferingInfo = KsapFrameworkServiceLocator.getCourseOfferingService()
					.getActivityOfferingDisplay(
							activityId, KsapFrameworkServiceLocator.getContext().getContextInfo());
			if (activityOfferingInfo != null)
				for (AttributeInfo attributeInfo : activityOfferingInfo.getAttributes())
					if (attributeInfo.getKey().equalsIgnoreCase("SLN"))
						return attributeInfo.getValue();
			return null;
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup error", e);
		}
	}

	/**
	 * builds the refObjId for activity PlanItems (eg: '2013:2:CHEM:152:A')
	 * 
	 * @param atpId
	 * @param subject
	 * @param number
	 * @param activityCd
	 * @return
	 */
	public String buildActivityRefObjId(String atpId, String subject, String number,
			String activityCd) {
		YearTerm yearTerm = KsapFrameworkServiceLocator.getTermHelper().getYearTerm(atpId);
		return joinStringsByDelimiter(':', Integer.toString(yearTerm.getYear()),
				yearTerm.getTermName(), subject,
				number, activityCd);
	}

	/**
	 * returns the course code from given activityId
	 * <p/>
	 * eg: for activityId '2013:2:CHEM:152:A' course code CHEM 152 is returned
	 * 
	 * @param activityId
	 * @return
	 */
	public String getCourseCdFromActivityId(String activityId) {
		ActivityOfferingDisplayInfo activityDisplayInfo;
		try {
			activityDisplayInfo = KsapFrameworkServiceLocator.getCourseOfferingService()
					.getActivityOfferingDisplay(
							activityId, KsapFrameworkServiceLocator.getContext().getContextInfo());
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup error", e);
		}
		assert activityDisplayInfo != null : "activity id " + activityId + " returned null";

		String courseOfferingId = null;
		for (AttributeInfo attributeInfo : activityDisplayInfo.getAttributes())
			if ("PrimaryActivityOfferingId".equalsIgnoreCase(attributeInfo.getKey()))
				courseOfferingId = attributeInfo.getValue();
		assert courseOfferingId != null : "activity id " + activityId
				+ " missing PrimaryActiveOfferingId";

		try {
			CourseOfferingInfo courseOfferingInfo = KsapFrameworkServiceLocator
					.getCourseOfferingService()
					.getCourseOffering(courseOfferingId,
							KsapFrameworkServiceLocator.getContext().getContextInfo());
			return courseOfferingInfo != null ? courseOfferingInfo.getCourseCode() : null;
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup error", e);
		}
	}

	/**
	 * returns the section code from given activityId
	 * 
	 * eg: for activityId '2013:2:CHEM:152:A' section code A is returned
	 * 
	 * @param activityId
	 * @return
	 */
	public String getCodeFromActivityId(String activityId) {
		try {
			ActivityOfferingDisplayInfo activityDisplayInfo = KsapFrameworkServiceLocator
					.getCourseOfferingService()
					.getActivityOfferingDisplay(activityId,
							KsapFrameworkServiceLocator.getContext().getContextInfo());
			return activityDisplayInfo != null ? activityDisplayInfo.getActivityOfferingCode()
					: null;
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("CO lookup error", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("CO lookup error", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("CO lookup error", e);
		}
	}

}
