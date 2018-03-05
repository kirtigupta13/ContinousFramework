package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.DataAccessException;
import com.cerner.devcenter.education.models.Learner;

/**
 * Class that tests the functionality of {@link LearnerDAOImpl} class.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@RunWith(MockitoJUnitRunner.class)
public class LearnerDAOImplTest {

    @InjectMocks
    private LearnerDAOImpl learnerDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private Learner learner;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String INSERT_LEARNER_QUERY = "INSERT INTO learners (user_id, email) VALUES(?,?)";
    private static final String IS_LEARNER_PRESENT_QUERY = "SELECT COUNT(*) FROM learners WHERE user_id=?";

    private static final String VALID_USER_ID = "MK051340";
    private static final String VALID_EMAIL_ID = "firstname.lastname@company.com";

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when learner object is
     * null.
     *
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddLearnerWhenLearnerIsNull() throws DAOException {
        learnerDAOImpl.addLearner(null);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when user id is null.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddLearnerWhenUserIdIsNull() throws DAOException {
        when(learner.getUserId()).thenReturn(null);
        when(learner.getEmailId()).thenReturn(VALID_EMAIL_ID);
        learnerDAOImpl.addLearner(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when user id is empty.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddLearnerWhenUserIdIsEmpty() throws DAOException {
        when(learner.getUserId()).thenReturn("");
        when(learner.getEmailId()).thenReturn(VALID_EMAIL_ID);
        learnerDAOImpl.addLearner(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when email id is null
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddLearnerWhenEmailIdIsNull() throws DAOException {
        when(learner.getUserId()).thenReturn(VALID_USER_ID);
        when(learner.getEmailId()).thenReturn(null);
        learnerDAOImpl.addLearner(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when email id is empty
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddLearnerWhenEmailIdIsEmpty() throws DAOException {
        when(learner.getUserId()).thenReturn(VALID_USER_ID);
        when(learner.getEmailId()).thenReturn("");
        learnerDAOImpl.addLearner(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when an exception occurs
     * in query execution.
     *
     * @throws DAOException
     */
    @Test(expected = DataAccessException.class)
    public void testAddLearnerWhenQueryFails() throws DAOException {
        doThrow(DataAccessException.class).when(jdbcTemplate).update(INSERT_LEARNER_QUERY, VALID_USER_ID,
                VALID_EMAIL_ID);
        when(learner.getUserId()).thenReturn(VALID_USER_ID);
        when(learner.getEmailId()).thenReturn(VALID_EMAIL_ID);
        learnerDAOImpl.addLearner(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#addLearner(Learner)} when valid learner object
     * information is passed.
     *
     * @throws DAOException
     */
    @Test
    public void testAddLearner() {
        when(learner.getUserId()).thenReturn(VALID_USER_ID);
        when(learner.getEmailId()).thenReturn(VALID_EMAIL_ID);
        when(jdbcTemplate.update(INSERT_LEARNER_QUERY, VALID_USER_ID)).thenReturn(1);
        try {
            learnerDAOImpl.addLearner(learner);
        } catch (DAOException e) {
            Assert.fail();
        }
        verify(jdbcTemplate).update(INSERT_LEARNER_QUERY, VALID_USER_ID, VALID_EMAIL_ID);
    }

    /**
     * Test {@link LearnerDAOImpl#isPresent(Learner)} when passed argument
     * learner object is null.
     *
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testIsPresentWhenPassedLearnerIsNull() throws DAOException {
        learnerDAOImpl.isPresent(null);
    }

    /**
     * Test {@link LearnerDAOImpl#isPresent(Learner)} when user id is null
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsPresentWhenUserIdIsNull() throws DAOException {
        when(learner.getUserId()).thenReturn(null);
        learnerDAOImpl.isPresent(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#isPresent(Learner)} when user id is empty
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIsPresentWhenUserIdIsEmpty() throws DAOException {
        when(learner.getUserId()).thenReturn("");
        learnerDAOImpl.isPresent(learner);
    }

    /**
     * Test {@link LearnerDAOImpl#isPresent(Learner)} when valid parameters are
     * passed.
     *
     * @throws DAOException
     */
    @Test
    public void testIsPresent() throws DAOException {
        when(jdbcTemplate.queryForObject(IS_LEARNER_PRESENT_QUERY, Integer.class, VALID_USER_ID)).thenReturn(1);
        when(learner.getUserId()).thenReturn(VALID_USER_ID);
        final boolean isPresent = learnerDAOImpl.isPresent(learner);
        assertEquals(true, isPresent);
    }
}
