package com.cerner.devcenter.education.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.DataAccessException;
import com.cerner.devcenter.education.models.Learner;

/**
 * This interface is responsible for performing database operations for
 * {@link Learner} objects.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
public interface LearnerDAO {

    /**
     * Perform an operation to add a learner.
     *
     * @param learner
     *            the {@link Learner} object that has learner information which
     *            includes learner's user id and email id.
     * @throws DAOException
     *             if there is an error in executing update
     */
    void addLearner(Learner learner) throws DAOException;

    /**
     * Checks for learner in database
     *
     * @param learner
     *            the {@link Learner} object that has user id information
     * @return true if learner is already present else false
     * @throws DAOException
     *             when
     *             {@link JdbcTemplate#queryForObject(String, Class, Object...)}
     *             throws {@link DataAccessException}
     */
    boolean isPresent(Learner learner) throws DAOException;
}
