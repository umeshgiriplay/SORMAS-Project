package de.symeda.sormas.api.user;

import static de.symeda.sormas.api.user.UserRole.ADMIN;
import static de.symeda.sormas.api.user.UserRole.CASE_OFFICER;
import static de.symeda.sormas.api.user.UserRole.CASE_SUPERVISOR;
import static de.symeda.sormas.api.user.UserRole.CONTACT_OFFICER;
import static de.symeda.sormas.api.user.UserRole.CONTACT_SUPERVISOR;
import static de.symeda.sormas.api.user.UserRole.INFORMANT;
import static de.symeda.sormas.api.user.UserRole.LAB_USER;
import static de.symeda.sormas.api.user.UserRole.NATIONAL_OBSERVER;
import static de.symeda.sormas.api.user.UserRole.STATE_OBSERVER;
import static de.symeda.sormas.api.user.UserRole.NATIONAL_USER;
import static de.symeda.sormas.api.user.UserRole.RUMOR_MANAGER;
import static de.symeda.sormas.api.user.UserRole.SURVEILLANCE_OFFICER;
import static de.symeda.sormas.api.user.UserRole.SURVEILLANCE_SUPERVISOR;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum UserRight {

	CASE_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	CASE_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	CASE_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	CASE_MOVE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR
	),
	/*
	 * Edit the investigation status - either by setting a respective task to done or by manually changing it in the case
	 */
	CASE_INVESTIGATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR
	),
	/*
	 * Edit the classification and outcome of a case
	 */
	CASE_CLASSIFY(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			LAB_USER
	),
	CASE_CHANGE_DISEASE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR
	),
	CASE_DELETE(
			ADMIN
	),
	SAMPLE_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			INFORMANT,
			LAB_USER
	),
	SAMPLE_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	SAMPLE_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			INFORMANT,
			LAB_USER
	),
	SAMPLE_DELETE(
			ADMIN
	),
	SAMPLE_TRANSFER(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			LAB_USER
	),
	SAMPLETEST_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			LAB_USER
	),
	SAMPLETEST_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			LAB_USER
	),
	CONTACT_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	CONTACT_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			RUMOR_MANAGER
	),
	CONTACT_ASSIGN(
			ADMIN,
			NATIONAL_USER,
			CONTACT_SUPERVISOR
	),
	CONTACT_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	CONTACT_DELETE(
			ADMIN
	),
	CONTACT_CLASSIFY(
			ADMIN,
			NATIONAL_USER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	CONTACT_CONVERT(
			ADMIN,
			NATIONAL_USER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	VISIT_CREATE(
			ADMIN,
			NATIONAL_USER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	VISIT_EDIT(
			ADMIN,
			NATIONAL_USER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER
	),
	TASK_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			LAB_USER
	),
	TASK_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	TASK_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	TASK_ASSIGN(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			LAB_USER
	),
	EVENT_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			RUMOR_MANAGER
	),
	EVENT_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CASE_OFFICER,
			CONTACT_SUPERVISOR,
			CONTACT_OFFICER,
			INFORMANT,
			LAB_USER,
			RUMOR_MANAGER
	),
	EVENT_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			RUMOR_MANAGER
	),
	EVENTPARTICIPANT_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			RUMOR_MANAGER
	),
	EVENTPARTICIPANT_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			RUMOR_MANAGER
	),
	WEEKLYREPORT_CREATE(
			INFORMANT
	),
	WEEKLYREPORT_VIEW(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR,
			SURVEILLANCE_OFFICER,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			INFORMANT
	),
	USER_CREATE(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			LAB_USER,
			RUMOR_MANAGER
	),
	USER_EDIT(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			LAB_USER,
			RUMOR_MANAGER
	),
	USER_VIEW(
			ADMIN,
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR,
			CASE_SUPERVISOR,
			CONTACT_SUPERVISOR,
			LAB_USER,
			RUMOR_MANAGER
	),
	CONFIGURATION_ACCESS(
			ADMIN,
			NATIONAL_USER,
			NATIONAL_OBSERVER,
			STATE_OBSERVER,
			SURVEILLANCE_SUPERVISOR
	),
	OUTBREAK_CONFIGURE_ALL(
			ADMIN,
			NATIONAL_USER
	),
	OUTBREAK_CONFIGURE_RESTRICTED(
			SURVEILLANCE_SUPERVISOR
	);
	
	private final Set<UserRole> userRoles;
	
	private UserRight(UserRole... userRoles) {
		this.userRoles = Collections.unmodifiableSet(new HashSet<UserRole>(Arrays.asList(userRoles)));
	}
	
	public boolean isForRole(UserRole userRole) {
		return userRoles.contains(userRole);
	}
	
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
}
