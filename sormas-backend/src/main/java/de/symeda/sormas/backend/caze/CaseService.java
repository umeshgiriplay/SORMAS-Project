package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {
	
	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}
	
	public List<Case> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(Case.REPORTING_USER), user);
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) {
			filter = cb.or(filter, cb.equal(from.get(Case.SURVEILLANCE_OFFICER), user));
		}
		
		// add cases for assigned tasks of the user
		Join<Case, Task> tasksJoin = from.join(Case.TASKS, JoinType.LEFT);
		filter = cb.or(filter, cb.equal(tasksJoin.get(Task.ASSIGNEE_USER), user));
		
		// TODO add Filter by Region...
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_SUPERVISOR)) {
			filter = null;//cb.or(filter, cb.equal(from.get(Case.SURVEILLANCE_SUPERVISOR), user));
		}
		
		if (date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			Join<Case, Symptoms> symptoms = from.join(Case.SYMPTOMS);
			dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		cq.distinct(true);

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
}
