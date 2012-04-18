package org.studentsuccessplan.ssp.dao.reference;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.studentsuccessplan.ssp.dao.AuditableCrudDao;
import org.studentsuccessplan.ssp.model.ObjectStatus;
import org.studentsuccessplan.ssp.model.reference.Challenge;

/**
 * Data access class for the Challenge reference entity.
 */
@Repository
public class ChallengeDao extends ReferenceAuditableCrudDao<Challenge>
		implements AuditableCrudDao<Challenge> {

	/**
	 * Constructor to initialize the class with the appropriate class type for
	 * use by the parent class methods.
	 */
	public ChallengeDao() {
		super(Challenge.class);
	}

	/**
	 * Retrieves all Challenges that have been marked "Affirmative" for the
	 * specified SelfHelpGuideResponseId.
	 * <p>
	 * Orders the list alphabetically by Challenge.Name.
	 * 
	 * @param selfHelpGuideResponseId
	 *            Specific SelfHelpGuideResponse identifier to use in the
	 *            criteria filter.
	 * @return All Challenges that have been marked "Affirmative" for the
	 *         specified SelfHelpGuideResponseId.
	 */
	@SuppressWarnings("unchecked")
	// :TODO paging?
	public List<Challenge> selectAffirmativeBySelfHelpGuideResponseId(
			@NotNull final UUID selfHelpGuideResponseId) {
		return sessionFactory
				.getCurrentSession()
				.createQuery(
						"select distinct c "
								+ "from Challenge c "
								+ "inner join c.selfHelpGuideQuestions shgq "
								+ "inner join shgq.selfHelpGuideQuestionResponses shgqr "
								+ "where shgqr.response = true "
								+ "and shgqr.selfHelpGuideResponse.id = ? "
								+ "order by c.name")
				.setParameter(0, selfHelpGuideResponseId).list();
	}

	/**
	 * Retrieve all Challenges that match the specified text query via a simple
	 * LIKE clause on the SelfHelpGuide Question, Description, and Tags fields.
	 * <p>
	 * Also filters out inactive Challenges, and those that are not marked to
	 * show in the SelfHelpSearch.
	 * 
	 * @param query
	 *            Text string to compare with a SQL LIKE clause on the
	 *            SelfHelpGuide Question, Description, and Tags fields
	 * @return All Challenges that match the specified criteria.
	 */
	@SuppressWarnings("unchecked")
	// :TODO paging?
	public List<Challenge> searchByQuery(final String query) {
		return sessionFactory
				.getCurrentSession()
				.createQuery(
						"select distinct c "
								+ "from Challenge c "
								+ "inner join c.challengeChallengeReferrals ccr "
								+ "where c.objectStatus = :objectStatus "
								+ "and c.showInSelfHelpSearch = true "
								+ "and (upper(c.name) like :query "
								+ "or upper(c.selfHelpGuideQuestion) like :query "
								+ "or upper(c.selfHelpGuideDescription) like :query "
								+ "or upper(c.tags) like :query) "
								+ "and exists " + "(from ChallengeReferral "
								+ "where id = ccr.challengeReferral.id "
								+ "and showInSelfHelpGuide = true "
								+ "and objectStatus = :objectStatus) "
								+ "order by c.name")
				.setParameter("query",
						"%" + query.toUpperCase(Locale.getDefault()) + "%")
				.setParameter("objectStatus", ObjectStatus.ACTIVE).list();
	}

	/**
	 * Retrieves all {@link ObjectStatus#ACTIVE} Challenges that are marked to
	 * be able to be shown in the Student Intake interface.
	 * 
	 * @return List of all Challenges that are marked to be able to be shown in
	 *         the Student Intake interface.
	 */
	@SuppressWarnings("unchecked")
	// :TODO paging?
	public List<Challenge> getAllInStudentIntake() {
		final Criteria query = sessionFactory.getCurrentSession()
				.createCriteria(Challenge.class)
				.add(Restrictions.eq("objectStatus", ObjectStatus.ACTIVE))
				.add(Restrictions.eq("showInStudentIntake", true));
		return query.list();
	}
}
