package com.cerner.devcenter.education.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.Learner;
import com.google.common.base.Strings;

/**
 * This class is responsible for performing database operations for
 * {@link Learner} objects on a table named 'learners'.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@Repository("learnerDAO")
public class LearnerDAOImpl implements LearnerDAO {

    private static final String INSERT_LEARNER_QUERY = "INSERT INTO learners (user_id, email) VALUES(?,?)";
    private static final String IS_LEARNER_PRESENT_QUERY = "SELECT COUNT(*) FROM learners WHERE user_id=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Creates a new {@link LearnerDAOImpl}. Used for testing and Spring beans.
     */
    public LearnerDAOImpl() {
    }

    @Override
    public void addLearner(final Learner learner) throws DAOException {
        checkNotNull(learner, "Learner object is null");
        final String userId = learner.getUserId();
        final String emailId = learner.getEmailId();
        checkArgument(!Strings.isNullOrEmpty(userId), "User id is null/empty");
        checkArgument(!Strings.isNullOrEmpty(emailId), "Email id is null/empty");
        try {
            jdbcTemplate.update(INSERT_LEARNER_QUERY, userId, emailId);
        } catch (final DataAccessException daoException) {
            throw new DAOException("Error while adding learner to the database", daoException);
        }
    }

    @Override
    public boolean isPresent(final Learner learner) throws DAOException {
        checkNotNull(learner, "Learner object is null");
        final String userId = learner.getUserId();
        checkArgument(!Strings.isNullOrEmpty(userId), "User id is null/empty");
        try {
            final Integer rowcount = jdbcTemplate.queryForObject(IS_LEARNER_PRESENT_QUERY, Integer.class, userId);
            return rowcount != null && rowcount > 0;
        } catch (final DataAccessException daoException) {
            throw new DAOException("unable to execute select query on user table", daoException);
        }
    }
}