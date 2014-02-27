package org.kuali.student.ap.framework.context.support;

import static org.kuali.rice.core.api.criteria.PredicateFactory.like;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.ap.framework.config.KsapFrameworkServiceLocator;
import org.kuali.student.ap.framework.context.PlanConstants;
import org.kuali.student.ap.framework.context.TermHelper;
import org.kuali.student.ap.framework.context.YearTerm;
import org.kuali.student.enrollment.acal.dto.AcademicCalendarInfo;
import org.kuali.student.enrollment.acal.dto.TermInfo;
import org.kuali.student.enrollment.acal.infc.AcademicCalendar;
import org.kuali.student.enrollment.acal.infc.Term;
import org.kuali.student.enrollment.acal.service.AcademicCalendarService;
import org.kuali.student.enrollment.courseoffering.dto.CourseOfferingInfo;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.util.constants.AcademicCalendarServiceConstants;
import org.kuali.student.r2.core.atp.infc.Atp;
import org.kuali.student.r2.core.atp.service.AtpService;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.lum.course.infc.Course;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Default implementation of {@link TermHelper} for use with applications that
 * implement all ATP logic by following the {@link AcademicCalendarService}
 * contract.
 * 
 * <p>
 * It is expected that the implementing institution provide support within
 * {@link AtpService#searchForAtps(QueryByCriteria, ContextInfo)} for searching
 * a &quot;query&quot; for the following values. This behavior may be deprecated
 * or moved to the core AtpService implementation in a future version of the KS
 * contract.
 * </p>
 * <dl>
 * <dt>{@link PlanConstants#INPROGRESS}</dt>
 * <dd>Terms that have started, but have not yet ended.</dd>
 * <dt>{@link PlanConstants#PUBLISHED}</dt>
 * <dd>Terms that have not ended for which course offerings have been published.
 * </dd>
 * <dt>{@link PlanConstants#PLANNING}</dt>
 * <dd>Published terms that have not started, and for which registration has not
 * ended.</dd>
 * </dl>
 */
public class DefaultTermHelper implements TermHelper {

	private static final MarkerKey MARKER_KEY = new MarkerKey();

	private static final Logger LOG = Logger.getLogger(DefaultTermHelper.class);

	private static Map<AtpRangeKey, Reference<List<Atp>>> ATP_RANGE_CACHE =
			new HashMap<AtpRangeKey, Reference<List<Atp>>>();

	private static Map<String, Reference<Term>> TERM_CACHE =
			new HashMap<String, Reference<Term>>();

	private static Map<String, Reference<YearTerm>> YEAR_TERM_CACHE =
			new HashMap<String, Reference<YearTerm>>();

	private static Map<String, Reference<AcademicCalendar>> ACAL_CACHE =
			new HashMap<String, Reference<AcademicCalendar>>();

	private static Map<String, Reference<List<AcademicCalendar>>> TERM_ACAL_CACHE =
			new HashMap<String, Reference<List<AcademicCalendar>>>();

	private static Map<String, Reference<List<Term>>> ACAL_TERM_CACHE =
			new HashMap<String, Reference<List<Term>>>();

	private static Reference<List<Term>> planningTerms;
	private static Reference<List<Term>> publishedTerms;
	private static Reference<List<Term>> inprogressTerms;
	private static Reference<Set<String>> termTypeKeys;

	private static ReferenceQueue<Object> CACHE_Q = new ReferenceQueue<Object>();

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

	private static class AtpRangeKey {
		private final long start;
		private final long end;

		private AtpRangeKey(Date start, Date end) {
			this.start = start.getTime();
			this.end = end.getTime();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
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
			AtpRangeKey other = (AtpRangeKey) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}
	}

	private static class MarkerKey {
		@Override
		public int hashCode() {
			return MarkerKey.class.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof MarkerKey;
		}
	}

	private class TermMarker {
		private List<Term> planningTerms;
		private List<Term> publishedTerms;
		private List<Term> inprogressTerms;
		private Set<String> termTypeKeys;
		private Map<AtpRangeKey, List<Atp>> atps = new HashMap<AtpRangeKey, List<Atp>>();
		private Map<String, Term> terms = new HashMap<String, Term>();
		private Map<String, YearTerm> yearTerms = new HashMap<String, YearTerm>();
		private Map<String, List<AcademicCalendar>> acals = new HashMap<String, List<AcademicCalendar>>();
		private Map<String, List<Term>> acalTerms = new HashMap<String, List<Term>>();

		private List<Atp> getAtpRange(Date start, Date end) {
			AtpRangeKey k = new AtpRangeKey(start, end);
			List<Atp> rv = atps.get(k);
			if (rv != null)
				return rv;

			pruneCache();
			Reference<List<Atp>> ref = ATP_RANGE_CACHE.get(k);
			rv = ref == null ? null : ref.get();
			if (rv != null)
				return rv;

			try {
				rv = Collections.<Atp> unmodifiableList(KsapFrameworkServiceLocator.getAtpService()
						.getAtpsByDates(start, end,
								KsapFrameworkServiceLocator.getContext().getContextInfo()));
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("ATP lookup failure", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("ATP lookup failure", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("ATP lookup failure", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("ATP lookup failure", e);
			}

			ref = new CacheReference<List<Atp>>(rv, ATP_RANGE_CACHE, k, CACHE_Q);
			synchronized (ATP_RANGE_CACHE) {
				ATP_RANGE_CACHE.put(k, ref);
			}
			atps.put(k, rv);

			return rv;
		}

		private Set<String> getTermTypeKeys() {
			if (termTypeKeys != null)
				return termTypeKeys;

			termTypeKeys = DefaultTermHelper.termTypeKeys == null ? null
					: DefaultTermHelper.termTypeKeys.get();
			if (termTypeKeys != null)
				return termTypeKeys;

			ContextInfo ctx = KsapFrameworkServiceLocator.getContext().getContextInfo();
			AcademicCalendarService academicCalendarService = KsapFrameworkServiceLocator
					.getAcademicCalendarService();
			List<TypeInfo> termTypes;
			try {
				termTypes = academicCalendarService.getTermTypes(ctx);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("ACAL lookup failure", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("ACAL lookup failure", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("ACAL lookup failure", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("ACAL lookup failure", e);
			}
			Set<String> termTypeKeys = new HashSet<String>();
			for (TypeInfo termType : termTypes)
				termTypeKeys.add(termType.getKey());

			DefaultTermHelper.termTypeKeys = new SoftReference<Set<String>>(
					this.termTypeKeys = Collections.unmodifiableSet(termTypeKeys));
			return this.termTypeKeys;
		}

		private void cache(Term t) {
			String termId = t.getId();

			terms.put(t.getId(), t);
			CacheReference<Term> termRef = new CacheReference<Term>(t, TERM_CACHE, termId, CACHE_Q);
			synchronized (TERM_CACHE) {
				TERM_CACHE.put(termId, termRef);
			}

			YearTerm yearTerm = getYearTerm(t);
			yearTerms.put(termId, yearTerm);
			CacheReference<YearTerm> yearTermRef =
					new CacheReference<YearTerm>(yearTerm, YEAR_TERM_CACHE, termId, CACHE_Q);
			synchronized (YEAR_TERM_CACHE) {
				YEAR_TERM_CACHE.put(termId, yearTermRef);
			}
		}

		private void cache(AcademicCalendar ac) {
			String acalId = ac.getId();
			CacheReference<AcademicCalendar> acalRef =
					new CacheReference<AcademicCalendar>(ac, ACAL_CACHE, acalId, CACHE_Q);
			synchronized (ACAL_CACHE) {
				ACAL_CACHE.put(acalId, acalRef);
			}
		}

		private List<Term> cache(Collection<? extends Term> terms) {
			List<Term> rv = new java.util.ArrayList<Term>(terms.size());
			for (Term t : terms) {
				cache(t);
				rv.add(t);
			}

			Collections.sort(rv, new Comparator<Term>() {
				@Override
				public int compare(Term o1, Term o2) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
			});
			return rv;
		}

		private void frontLoadAcal(List<Term> terms, List<AcademicCalendar> acals) {
			StringBuilder msg = null;
			if (LOG.isDebugEnabled())
				msg = new StringBuilder("Acal/term relationship front load ");
			Map<String, List<Term>> acalTermMap = new HashMap<String, List<Term>>();

			for (Term t : terms) {
				String termId = t.getId();
				if (msg != null)
					msg.append("\n  Term ").append(termId);
				List<AcademicCalendar> termAcals = this.acals.get(termId);
				if (termAcals == null) {
					Reference<List<AcademicCalendar>> termAcalsRef =
							TERM_ACAL_CACHE.get(termId);
					termAcals = termAcalsRef == null ? null : termAcalsRef.get();
					if (termAcals != null) {
						if (msg != null)
							msg.append(" acals-global");
						this.acals.put(termId, termAcals);
					} else
						if (msg != null)
							msg.append(" acals-miss");
				} else
					if (msg != null)
						msg.append(" acals-tx");
				List<AcademicCalendar> ntermAcals = termAcals != null ? null
						: new ArrayList<AcademicCalendar>(acals.size());

				for (AcademicCalendar acal : termAcals == null ? acals : termAcals) {
					if (msg != null)
						msg.append("\n    Acal ").append(acal.getId());
					if (t.getStartDate().before(acal.getStartDate())
							|| t.getEndDate().after(acal.getEndDate())) {
						if (msg != null)
							msg.append(" out of range");
						continue;
					}

					if (ntermAcals != null) {
						ntermAcals.add(acal);
						if (msg != null)
							msg.append(" term-add");
					}

					String acalId = acal.getId();
					List<Term> acalTerms = this.acalTerms.get(acalId);
					if (acalTerms == null) {
						Reference<List<Term>> acalTermsRef = ACAL_TERM_CACHE.get(acalId);
						acalTerms = acalTermsRef == null ? null : acalTermsRef.get();
						if (acalTerms != null) {
							this.acalTerms.put(acalId, acalTerms);
							if (msg != null)
								msg.append(" term-global");
						} else if (msg != null)
							msg.append(" term-miss");
					} else if (msg != null)
						msg.append(" term-tx");
					if (acalTerms != null) {
						if (msg != null)
							msg.append(" cached");
						acalTermMap.remove(acalId);
						continue;
					}

					acalTerms = acalTermMap.get(acalId);
					if (acalTerms == null) {
						acalTermMap.put(acalId,
								acalTerms = new java.util.LinkedList<Term>());
					}
					acalTerms.add(t);
					if (msg != null)
						msg.append(" acal-add");
				}

				if (ntermAcals != null) {
					this.acals.put(termId, ntermAcals);
					CacheReference<List<AcademicCalendar>> ntermAcalsRef =
							new CacheReference<List<AcademicCalendar>>(ntermAcals,
									TERM_ACAL_CACHE, termId, CACHE_Q);
					synchronized (TERM_ACAL_CACHE) {
						TERM_ACAL_CACHE.put(termId, ntermAcalsRef);
					}
					if (msg != null)
						msg.append(" acals-cached ").append(ntermAcals.size());
				}
			}

			for (Entry<String, List<Term>> e : acalTermMap.entrySet()) {
				String acalId = e.getKey();
				List<Term> acalTerms = e.getValue();
				Collections.sort(acalTerms, new Comparator<Term>() {
					@Override
					public int compare(Term term1, Term term2) {
						return getYearTerm(term1).compareTo(getYearTerm(term2));
					}
				});
				this.acalTerms.put(acalId, acalTerms);
				CacheReference<List<Term>> acalTermsRef =
						new CacheReference<List<Term>>(
								acalTerms, ACAL_TERM_CACHE, acalId, CACHE_Q);
				synchronized (ACAL_TERM_CACHE) {
					ACAL_TERM_CACHE.put(acalId, acalTermsRef);
				}
				if (msg != null)
					msg.append("\n  Acal/term relationship ").append(acalId).append(" cached ")
							.append(acalTerms.size());
			}

			if (msg != null)
				LOG.debug(msg);
		}

		private void frontLoad(Date startDate, Date endDate) {
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(startDate);
			int syear = cal.get(Calendar.YEAR) - (cal.get(Calendar.MONTH) >= Calendar.AUGUST ? 0 : 1);
			Date start = new GregorianCalendar(syear, Calendar.AUGUST, 1).getTime();
			
			cal.setTime(endDate);
			int eyear = cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) > Calendar.AUGUST ? 1 : 0);
			Date end = new GregorianCalendar(eyear, Calendar.AUGUST, 31).getTime();

			StringBuilder msg = null;
			if (LOG.isDebugEnabled())
				msg = new StringBuilder("Term front load ").append(start).append(" to ")
						.append(end);

			ContextInfo ctx = KsapFrameworkServiceLocator.getContext().getContextInfo();
			AcademicCalendarService academicCalendarService = KsapFrameworkServiceLocator
					.getAcademicCalendarService();
			try {
				List<Atp> atps = getAtpRange(start, end);
				Set<String> termTypeKeys = getTermTypeKeys();

				int initSize = atps.size();
				List<AcademicCalendar> acals = new ArrayList<AcademicCalendar>(initSize);
				List<Term> terms = new ArrayList<Term>(initSize);
				List<String> acalIds = new ArrayList<String>(initSize);
				List<String> termIds = new ArrayList<String>(initSize);
				for (Atp atp : atps) {
					String atpType = atp.getTypeKey();
					String atpId = atp.getId();
					
					if (msg != null)
						msg.append("\n  ATP ").append(atpId).append(" ").append(atpType);

					if (AcademicCalendarServiceConstants.ACADEMIC_CALENDAR_TYPE_KEY.equals(atpType)) {
						Reference<AcademicCalendar> acalRef = ACAL_CACHE.get(atpId);
						AcademicCalendar acal = acalRef == null ? null : acalRef.get();
						if (msg != null)
							msg.append(" acal");
						if (acal == null) {
							acalIds.add(atpId);
							if (msg != null)
								msg.append(" queued");
						} else {
							acals.add(acal);
							if (msg != null)
								msg.append(" cached");
						}

					} else if (termTypeKeys.contains(atpType)) {
						Reference<Term> termRef = TERM_CACHE.get(atpId);
						Term term = termRef == null ? null : termRef.get();
						if (msg != null)
							msg.append(" term");
						if (term == null) {
							termIds.add(atpId);
							if (msg != null)
								msg.append(" queued");
						} else {
							terms.add(term);
							if (msg != null)
								msg.append(" cached");
						}
					}
				}

				if (!acalIds.isEmpty())
					for (AcademicCalendarInfo acal : academicCalendarService
							.getAcademicCalendarsByIds(acalIds, ctx)) {
						cache(acal);
						acals.add(acal);
						if (msg != null)
							msg.append("\n  Academic Calendar ").append(acal.getId());
					}

				if (!termIds.isEmpty())
					for (TermInfo term : academicCalendarService
							.getTermsByIds(termIds, ctx)) {
						cache(term);
						terms.add(term);
						if (msg != null)
							msg.append("\n  Term ").append(term.getId());
					}

				frontLoadAcal(terms, acals);

			} catch (DoesNotExistException e) {
				throw new IllegalArgumentException("ATP lookup failure", e);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("ATP lookup failure", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("ATP lookup failure", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("ATP lookup failure", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("ATP lookup failure", e);
			}
			
			if (msg != null)
				LOG.debug(msg);
		}
	}

	private TermMarker getTermMarker() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TermMarker rv = (TermMarker) TransactionSynchronizationManager.getResource(MARKER_KEY);
			if (rv == null) {
				TransactionSynchronizationManager.bindResource(MARKER_KEY, rv = new TermMarker());
				TransactionSynchronizationManager
						.registerSynchronization(new TransactionSynchronizationAdapter() {
							@Override
							public void afterCompletion(int status) {
								TransactionSynchronizationManager
										.unbindResourceIfPossible(MARKER_KEY);
							}
						});
			}
			return rv;
		} else {
			return new TermMarker();
		}
	}

	@Override
	public void frontLoadForPlanner(String firstAtpId) {
		Term firstTerm = firstAtpId == null ? getPlanningTerms().get(0) : getTerm(firstAtpId);
		Date start = firstTerm.getStartDate();
		Date end = null;
		for (Term pt : getPlanningTerms()) {
			if (end == null || end.before(pt.getEndDate()))
				end = pt.getEndDate();
		}
		getTermMarker().frontLoad(start, end);
	}

	@Override
	public Term getTerm(String atpId) {
		TermMarker tm = getTermMarker();
		Term rv = tm.terms.get(atpId);

		if (rv == null) {
			pruneCache();
			Reference<Term> ref = TERM_CACHE.get(atpId);
			rv = ref == null ? null : ref.get();
		}

		if (rv == null) {
			try {
				tm.cache(rv = KsapFrameworkServiceLocator.getAcademicCalendarService().getTerm(
						atpId, KsapFrameworkServiceLocator.getContext().getContextInfo()));
			} catch (DoesNotExistException e) {
				throw new IllegalArgumentException("Acal lookup failure", e);
			} catch (InvalidParameterException e) {
				throw new IllegalArgumentException("Acal lookup failure", e);
			} catch (MissingParameterException e) {
				throw new IllegalArgumentException("Acal lookup failure", e);
			} catch (OperationFailedException e) {
				throw new IllegalStateException("Acal lookup failure", e);
			} catch (PermissionDeniedException e) {
				throw new IllegalStateException("Acal lookup failure", e);
			}
		}

		return rv;
	}

	@Override
	public YearTerm getYearTerm(String atpId) {
		TermMarker tm = getTermMarker();
		YearTerm rv = tm.yearTerms.get(atpId);

		if (rv == null) {
			pruneCache();
			Reference<YearTerm> ref = YEAR_TERM_CACHE.get(atpId);
			rv = ref == null ? null : ref.get();
		}

		if (rv == null) {
			rv = getYearTerm(getTerm(atpId));
		}
		return rv;
	}

	@Override
	public List<Term> getCurrentTerms() {
		TermMarker tm = getTermMarker();
		if (tm.inprogressTerms != null)
			return tm.inprogressTerms;

		tm.inprogressTerms = inprogressTerms == null ? null : inprogressTerms.get();
		if (tm.inprogressTerms != null)
			return tm.inprogressTerms;

		try {
			QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(like("atpStatus",
					PlanConstants.INPROGRESS));
			List<Term> rl = Collections.<Term> unmodifiableList(KsapFrameworkServiceLocator
					.getAcademicCalendarService().searchForTerms(query,
							KsapFrameworkServiceLocator.getContext().getContextInfo()));
			if (rl == null || rl.isEmpty())
				throw new IllegalStateException(
						"AcademicCalendarService did not return any in-progress terms");
			tm.inprogressTerms = rl;
			inprogressTerms = new SoftReference<List<Term>>(rl);
			return tm.cache(rl);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		}
	}

	@Override
	public Term getLastScheduledTerm() {
		Term rv = null;
		for (Term t : getPublishedTerms())
			if (rv == null || t.getEndDate().after(rv.getEndDate()))
				rv = t;
		assert rv != null;
		return rv;
	}

	@Override
	public Term getOldestHistoricalTerm() {
		Term lst = getLastScheduledTerm();
		Calendar c = Calendar.getInstance();
		c.setTime(lst.getStartDate());
		return getTerm(new DefaultYearTerm(lst.getId(), lst.getTypeKey(), c.get(Calendar.YEAR) - 10));
	}

	@Override
	public List<Term> getTermsInAcademicYear(YearTerm yearTerm) {
		try {
			String termId = yearTerm.getTermId();
			TermMarker tm = getTermMarker();

			List<AcademicCalendar> acl = tm.acals.get(termId);
			if (acl == null) {
				pruneCache();
				Reference<List<AcademicCalendar>> aclRef = TERM_ACAL_CACHE.get(termId);
				acl = aclRef == null ? null : aclRef.get();
				if (acl != null) {
					tm.acals.put(termId, acl);
				}
			}

			if (acl == null) {
				acl = Collections.<AcademicCalendar> unmodifiableList(KsapFrameworkServiceLocator
						.getAcademicCalendarService().getAcademicCalendarsForTerm(termId,
								KsapFrameworkServiceLocator.getContext().getContextInfo()));
				tm.acals.put(termId, acl);
				CacheReference<List<AcademicCalendar>> aclRef = new CacheReference<List<AcademicCalendar>>(
						acl, TERM_ACAL_CACHE, termId, CACHE_Q);
				synchronized (TERM_ACAL_CACHE) {
					TERM_ACAL_CACHE.put(termId, aclRef);
				}
			}
			if (acl == null || acl.isEmpty())
				throw new IllegalStateException(
						"AcademicCalendarService did not return an academic calendar for year/term "
								+ yearTerm + " " + acl + " " + termId);

			AcademicCalendar ac = acl.get(0);
			String acalId = ac.getId();
			List<Term> rl = tm.acalTerms.get(ac.getId());
			if (rl == null) {
				pruneCache();
				Reference<List<Term>> ref = ACAL_TERM_CACHE.get(acalId);
				rl = ref == null ? null : ref.get();
				if (rl != null) {
					tm.acalTerms.put(acalId, rl);
				}
			}

			if (rl == null) {
				List<TermInfo> rli = KsapFrameworkServiceLocator
						.getAcademicCalendarService().getTermsForAcademicCalendar(ac.getId(),
								KsapFrameworkServiceLocator.getContext().getContextInfo());
				Collections.sort(rli, new Comparator<Term>() {
					@Override
					public int compare(Term term1, Term term2) {
						return getYearTerm(term1).compareTo(getYearTerm(term2));
					}
				});
				rl = Collections.<Term> unmodifiableList(tm.cache(rli));
				tm.acalTerms.put(acalId, rl);
				CacheReference<List<Term>> ref = new CacheReference<List<Term>>(
						rl, ACAL_TERM_CACHE, acalId, CACHE_Q);
				synchronized (ACAL_TERM_CACHE) {
					ACAL_TERM_CACHE.put(acalId, ref);
				}
			}
			if (rl == null || rl.isEmpty())
				throw new IllegalStateException(
						"AcademicCalendarService did not return any terms for academic calendar "
								+ ac.getId());

			for (Term at : rl) {
				String atermId = at.getId();
				if (!atermId.equals(termId) && tm.acals.containsKey(atermId)) {
					tm.acals.put(atermId, acl);
					CacheReference<List<AcademicCalendar>> aclRef = new CacheReference<List<AcademicCalendar>>(
							acl, TERM_ACAL_CACHE, atermId, CACHE_Q);
					synchronized (TERM_ACAL_CACHE) {
						TERM_ACAL_CACHE.put(atermId, aclRef);
					}
				}
			}

			return rl;
		} catch (DoesNotExistException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		}
	}

	@Override
	public Term getFirstTermOfAcademicYear(YearTerm yearTerm) {
		Term rv = null;
		for (Term t : getTermsInAcademicYear(yearTerm))
			if (rv == null || t.getStartDate().before(rv.getStartDate()))
				rv = t;
		assert rv != null;
		return rv;
	}

	@Override
	public int getNumberOfTermsInAcademicYear(YearTerm yearTerm) {
		return getTermsInAcademicYear(yearTerm).size();
	}

	@Override
	public int getNumberOfTermsInAcademicYear() {
		return getNumberOfTermsInAcademicYear(getYearTerm(getPlanningTerms().get(0)));
	}

	@Override
	public List<Term> getTermsInAcademicYear() {
		return getTermsInAcademicYear(getYearTerm(getPlanningTerms().get(0)));
	}

	@Override
	public String getTermNameInAcadmicYear(int index) {
		if (index >= getNumberOfTermsInAcademicYear())
			return "";
		Term temp = getTermsInAcademicYear().get(index);
		YearTerm yearTerm = getYearTerm(temp);
		return yearTerm.getTermName();
	}

	@Override
	public List<Term> getPlanningTerms() {
		TermMarker tm = getTermMarker();
		if (tm.planningTerms != null)
			return tm.planningTerms;

		tm.planningTerms = planningTerms == null ? null : planningTerms.get();
		if (tm.planningTerms != null)
			return tm.planningTerms;

		try {
			QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(like("atpStatus",
					PlanConstants.PLANNING));
			List<Term> rl = Collections.<Term> unmodifiableList(KsapFrameworkServiceLocator
					.getAcademicCalendarService().searchForTerms(query,
							KsapFrameworkServiceLocator.getContext().getContextInfo()));
			if (rl == null || rl.isEmpty())
				throw new IllegalStateException(
						"AcademicCalendarService did not return any planning terms");
			tm.planningTerms = rl;
			planningTerms = new SoftReference<List<Term>>(rl);
			return tm.cache(rl);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		}
	}

	@Override
	public List<Term> getTermsByDateRange(Date startDate, Date endDate) {
		TermMarker tm = getTermMarker();
		Set<String> termTypeKeys = tm.getTermTypeKeys();
		List<Atp> atps = tm.getAtpRange(startDate, endDate);
		List<Term> terms = new ArrayList<Term>(atps.size());
		for (Atp atp : atps)
			if (termTypeKeys.contains(atp.getTypeKey()))
				terms.add(getTerm(atp.getId()));
		return terms;
	}

	@Override
	public boolean isPublished(String termId) {
		for (Term t : getPublishedTerms())
			if (t.getId().equals(termId))
				return true;
		return false;
	}

	@Override
	public boolean isPlanning(String atpId) {
		for (Term t : getPlanningTerms())
			if (t.getId().equals(atpId))
				return true;
		return false;
	}

	@Override
	public boolean isCompleted(String atpId) {
		return getTerm(atpId).getEndDate().before(new Date());
	}

	@Override
	public boolean isCourseOffered(Term term, Course course) {
		try {
			List<CourseOfferingInfo> cos = KsapFrameworkServiceLocator.getCourseOfferingService()
					.getCourseOfferingsByCourseAndTerm(course.getId(), term.getId(),
							KsapFrameworkServiceLocator.getContext().getContextInfo());
			return cos != null && !cos.isEmpty();
		} catch (DoesNotExistException e) {
			return false;
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
	public List<Term> getPublishedTerms() {
		TermMarker tm = getTermMarker();
		if (tm.publishedTerms != null)
			return tm.publishedTerms;

		tm.publishedTerms = publishedTerms == null ? null : publishedTerms.get();
		if (tm.publishedTerms != null)
			return tm.publishedTerms;

		try {
			QueryByCriteria query = QueryByCriteria.Builder.fromPredicates(like("atpStatus",
					PlanConstants.PUBLISHED));
			List<Term> rl = Collections.<Term> unmodifiableList(KsapFrameworkServiceLocator
					.getAcademicCalendarService().searchForTerms(query,
							KsapFrameworkServiceLocator.getContext().getContextInfo()));
			if (rl == null || rl.isEmpty())
				throw new IllegalStateException(
						"AcademicCalendarService did not return any published terms");
			tm.publishedTerms = rl;
			publishedTerms = new SoftReference<List<Term>>(rl);
			return tm.cache(rl);
		} catch (InvalidParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (MissingParameterException e) {
			throw new IllegalArgumentException("Acal lookup failure", e);
		} catch (OperationFailedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		} catch (PermissionDeniedException e) {
			throw new IllegalStateException("Acal lookup failure", e);
		}
	}

	public Term getTerm(YearTerm yearTerm) {
		return getTerm(yearTerm.getTermId());
	}

	@Override
	public YearTerm getYearTerm(Term term) {
		Calendar c = Calendar.getInstance();
		c.setTime(term.getStartDate());
		return new DefaultYearTerm(term.getId(), term.getTypeKey(), c.get(Calendar.YEAR));
	}

}
