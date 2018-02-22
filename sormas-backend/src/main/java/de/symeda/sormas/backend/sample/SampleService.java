package de.symeda.sormas.backend.sample;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class SampleService extends AbstractAdoService<Sample> {
	
	@EJB
	private CaseService caseService;	
	
	public SampleService() {
		super(Sample.class);
	}
	
	public List<Sample> getAllByCase(Case caze) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		
		if(caze != null) {
			cq.where(cb.equal(from.get(Sample.ASSOCIATED_CASE), caze));
		}
		cq.orderBy(cb.desc(from.get(Sample.REPORT_DATE_TIME)));
		
		List<Sample> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	/**
	 * Returns the sample that refers to the sample identified by the sampleUuid.
	 * 
	 * @param sampleUuid The UUID of the sample to get the referral for.
	 * @return The sample that refers to this sample, or null if none is found.
	 */
	public Sample getReferredFrom(String sampleUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Sample> cq = cb.createQuery(getElementClass());
		Root<Sample> from = cq.from(getElementClass());
		
		cq.where(cb.equal(from.get(Sample.REFERRED_TO), getByUuid(sampleUuid)));
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public void updateMainSampleTest(Sample sample) {
		if (sample.getSampleTests().isEmpty()) {
			sample.setMainSampleTest(null);
		} else {
			sample.setMainSampleTest(sample.getSampleTests().stream()
					.sorted(Comparator.comparing(SampleTest::getTestDateTime, Comparator.nullsLast(Comparator.reverseOrder())))
					.findFirst().get());
		}
	}
	
	public List<DashboardSampleDto> getNewSamplesForDashboard(District district, Disease disease, Date from, Date to, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardSampleDto> cq = cb.createQuery(DashboardSampleDto.class);
		Root<Sample> sample = cq.from(getElementClass());
		Join<Sample, Case> caze = sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		Predicate filter = createUserFilter(cb, cq, sample, user);
		Predicate dateFilter = cb.between(sample.get(Sample.REPORT_DATE_TIME), from, to);
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}
		
		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}
		
		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}
		
		List<DashboardSampleDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					sample.get(Sample.SHIPPED),
					sample.get(Sample.RECEIVED)
			);
			
			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {
		
		Predicate filter = createUserFilterWithoutCase(cb, cq, samplePath, user);

		// whoever created the case the sample is associated with or is assigned to it
		// is allowed to access it
		Path<Case> casePath = samplePath.get(Sample.ASSOCIATED_CASE);
		
		Predicate caseFilter = caseService.createUserFilter(cb, cq, (From<Case,Case>)casePath, user);
		filter = or(cb, filter, caseFilter);

		return filter;
	}
	
	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<Sample,Sample> samplePath, User user) {
		
		Predicate filter = null;
		// user that reported it is not able to access it. Otherwise they would also need to access the case
		//filter = cb.equal(samplePath.get(Sample.REPORTING_USER), user);
		
		// lab users can see samples assigned to their laboratory
		if (user.getUserRoles().contains(UserRole.LAB_USER)) {
			if(user.getLaboratory() != null) {
				filter = or(cb, filter, cb.equal(samplePath.get(Sample.LAB), user.getLaboratory()));			}
		}
		
		return filter;
	}
	
	public Predicate buildCriteriaFilter(SampleCriteria sampleCriteria, CriteriaBuilder cb, Root<Sample> from) {
		
		Join<Sample, Case> caze = from.join(Sample.ASSOCIATED_CASE, JoinType.LEFT);
		
		Predicate filter = null;
		if (sampleCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID), sampleCriteria.getRegion().getUuid()));
		}
		if (sampleCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), sampleCriteria.getDistrict().getUuid()));
		}
		if (sampleCriteria.getLaboratory() != null) {
			filter = and(cb, filter, cb.equal(from.join(Sample.LAB, JoinType.LEFT).get(Facility.UUID), sampleCriteria.getLaboratory().getUuid()));
		}
		if (sampleCriteria.getShipped() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.SHIPPED), sampleCriteria.getShipped()));
		}
		if (sampleCriteria.getReceived() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.RECEIVED), sampleCriteria.getReceived()));
		}
		if (sampleCriteria.getReferred() != null) {
			if (sampleCriteria.getReferred().equals(Boolean.TRUE)) {
				filter = and(cb, filter, cb.isNotNull(from.get(Sample.REFERRED_TO)));
			} else {
				filter = and(cb, filter, cb.isNull(from.get(Sample.REFERRED_TO)));
			}
		}
		if (sampleCriteria.getTestResult() != null) {
			Predicate subFilter = cb.equal(from.join(Sample.MAIN_SAMPLE_TEST, JoinType.LEFT).get(SampleTest.TEST_RESULT), sampleCriteria.getTestResult());
			if (sampleCriteria.getTestResult() == SampleTestResultType.PENDING) {
				subFilter = or(cb, subFilter, cb.isNull(from.join(Sample.MAIN_SAMPLE_TEST, JoinType.LEFT).get(SampleTest.TEST_RESULT)));
			}
			filter = and(cb, filter, subFilter);
		}
		if (sampleCriteria.getCaseClassification() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.CASE_CLASSIFICATION), sampleCriteria.getCaseClassification()));
		}
		if (sampleCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.UUID), sampleCriteria.getCaze().getUuid()));
		}
		if (sampleCriteria.getSpecimenCondition() != null) {
			filter = and(cb, filter, cb.equal(from.get(Sample.SPECIMEN_CONDITION), sampleCriteria.getSpecimenCondition()));
		}

		return filter;
	}
}
