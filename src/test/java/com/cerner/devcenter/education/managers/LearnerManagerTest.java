package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.LearnerDAO;
import com.cerner.devcenter.education.models.Learner;

/**
 * Class that tests the functionalities of {@link LearnerManager}.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@RunWith(MockitoJUnitRunner.class)
public class LearnerManagerTest {

    @InjectMocks
    private LearnerManager learnerManager;
    @Mock
    private LearnerDAO mockLearnerDAO;
    @Mock
    private Learner mockLearner;

    /**
     * Test {@link LearnerManager#addLearner(Learner)} when the DAO throws
     * {@link DAOException}. Expects {@link ManagerException}.
     */
    @Test(expected = ManagerException.class)
    public void testaddLearnerThrowsManagerException() throws DAOException {
        doThrow(DAOException.class).when(mockLearnerDAO).addLearner(mockLearner);
        learnerManager.addLearner(mockLearner);
    }

    /**
     * Test {@link LearnerManager#addLearner(Learner)} when {@link Learner}
     * object passed in is null. Expects {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void testaddLearnerWhenPassedObjectNull() {
        learnerManager.addLearner(null);
    }

    /**
     * Test {@link LearnerManager#isPresent(Learner)} when the learner already
     * exists in database. Must return true.
     */
    @Test
    public void testisPresent() throws DAOException {
        when(mockLearnerDAO.isPresent(mockLearner)).thenReturn(true);
        assertTrue(learnerManager.isPresent(mockLearner));
    }

    /**
     * Test {@link LearnerManager#isPresent(Learner)} when the learner is not
     * present in database. Must return false.
     */
    @Test
    public void testisPresentNotAlreadyExists() throws DAOException {
        when(mockLearnerDAO.isPresent(mockLearner)).thenReturn(false);
        assertFalse(learnerManager.isPresent(mockLearner));
    }

    /**
     * Test {@link LearnerManager#isPresent(Learner)} when the DAO throws
     * {@link DAOException}. Expects {@link ManagerException}.
     */
    @Test(expected = ManagerException.class)
    public void testisPresentThrowsManagerException() throws DAOException {
        when(mockLearnerDAO.isPresent(mockLearner)).thenThrow(DAOException.class);
        learnerManager.isPresent(mockLearner);
    }
}