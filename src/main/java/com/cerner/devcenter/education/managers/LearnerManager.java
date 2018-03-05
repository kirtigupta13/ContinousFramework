package com.cerner.devcenter.education.managers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.LearnerDAO;
import com.cerner.devcenter.education.models.Learner;

/**
 * This class manages addition of {@Learner} objects.
 *
 * @author Mani Teja Kurapati(MK051340)
 *
 */
@Service("learnerManager")
public class LearnerManager {

    @Autowired
    private LearnerDAO learnerDAO;

    private static final String ERROR_ADDING_LEARNER = "Error encountered while adding learner";
    private static final String ERROR_FINDING_LEARNER = "Error encountered while searching for learner";

    /**
     * Adds a new learner.
     *
     * @param learner
     *            {@Learner} object that contains user id and email id.
     * @return true if learner is added or else returns false.
     */
    public void addLearner(final Learner learner) {
        checkNotNull(learner, "Learner object passed cannot be null.");
        try {
            learnerDAO.addLearner(learner);
        } catch (final DAOException daoException) {
            throw new ManagerException(ERROR_ADDING_LEARNER, daoException);
        }
    }

    /**
     * Checks if learner is already present in database.
     *
     * @param learner
     *            {@Learner} object that contains user id information.
     * @return true if learner is already present or else returns false.
     */
    public boolean isPresent(final Learner learner) {
        checkNotNull(learner, "Learner object passed cannot be null.");
        try {
            return learnerDAO.isPresent(learner);
        } catch (final DAOException daoException) {
            throw new ManagerException(ERROR_FINDING_LEARNER, daoException);
        }
    }
}