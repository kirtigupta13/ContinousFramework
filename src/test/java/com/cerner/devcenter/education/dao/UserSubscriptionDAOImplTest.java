package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.DataAccessException;
import com.cerner.devcenter.education.dao.UserSubscriptionDAOImpl.UserListResultExtractor;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Learner;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserSubscription;

/**
 * Class that tests the functionality of {@link UserSubscriptionDAOImpl} class.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSubscriptionDAOImplTest {

    @InjectMocks
    private UserSubscriptionDAOImpl userSubscriptionDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Mock
    private Category category;
    @Mock
    private Resource resource;
    @Mock
    private UserSubscription userSubscription;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String ADD_NEW_USER_SUBSCRIPTION = "INSERT INTO user_subscription (user_id, category_id) VALUES(?,?)";
    private static final String DELETE_USER_SUBSCRIPTION = "DELETE FROM user_subscription WHERE user_id = ? and category_id = ?";

    private static final String VALID_USER_ID = "MK051340";
    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_EMAIL = "firstname.lastname@comapny.com";

    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";

    private static List<String> userList;
    private static List<String> newUserList;
    private static List<Learner> learnerList;

    @Before
    public void setup() {
        newUserList = new ArrayList<>();
        userList = new ArrayList<>();
        learnerList = new ArrayList<>();
        userList.add("MK051340");
        learnerList.add(new Learner(VALID_USER_ID, VALID_EMAIL));

        when(jdbcTemplate.update(ADD_NEW_USER_SUBSCRIPTION, VALID_USER_ID, VALID_CATEGORY_ID)).thenReturn(1);
        when(jdbcTemplate.update(DELETE_USER_SUBSCRIPTION, VALID_USER_ID, VALID_CATEGORY_ID)).thenReturn(1);
        when(jdbcTemplate.query(anyString(), any(UserListResultExtractor.class), anyString())).thenReturn(userList);

    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#addUserSusbcription(UserSubscription)}
     * when user subscription is null.
     *
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testAddUserSusbcriptionWhenUserSubscriptionIsNull() throws DAOException {
        userSubscriptionDAOImpl.addUserSusbcription(null);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#addUserSusbcription(UserSubscription)}
     * when user id is null.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserSusbcriptionWhenUserIdIsNull() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(null);
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionDAOImpl.addUserSusbcription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#addUserSusbcription(UserSubscription)}
     * when user id is empty.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserSusbcriptionWhenUserIdIsEmpty() throws DAOException {
        when(userSubscription.getUserId()).thenReturn("");
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionDAOImpl.addUserSusbcription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#addUserSusbcription(UserSubscription)}
     * when category id is not greater than zero.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserSusbcriptionWhenCategoryIdIsNotGreaterThanZero() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(userSubscription.getCategoryId()).thenReturn(-2);
        userSubscriptionDAOImpl.addUserSusbcription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#addUserSusbcription(UserSubscription)}
     * when valid user subscription parameter is provided.
     *
     * @throws DAOException
     */
    @Test
    public void testAddUserSusbcription() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        assertEquals(true, userSubscriptionDAOImpl.addUserSusbcription(userSubscription));
        verify(jdbcTemplate).update(ADD_NEW_USER_SUBSCRIPTION, VALID_USER_ID, VALID_CATEGORY_ID);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#deleteUserSubscription(UserSubscription)}
     * when user subscription is null.
     *
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testDeleteSubscriptionWhenUserSubscriptionIsNull() throws DAOException {
        userSubscriptionDAOImpl.deleteUserSubscription(null);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#deleteUserSubscription(UserSubscription)}
     * when user id is null.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionWhenUserIdIsNull() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(null);
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionDAOImpl.deleteUserSubscription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#deleteUserSubscription(UserSubscription)}
     * when user id is empty.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionWhenUserIdIsEmpty() throws DAOException {
        when(userSubscription.getUserId()).thenReturn("");
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionDAOImpl.deleteUserSubscription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#deleteUserSubscription(UserSubscription)}
     * when category id is not greater than zero.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteSubscriptionWhenCategoryIdIsNotGreaterThanZero() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(userSubscription.getCategoryId()).thenReturn(-2);
        userSubscriptionDAOImpl.deleteUserSubscription(userSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#deleteUserSubscription(UserSubscription)}
     * when valid user subscription parameter is provided.
     *
     * @throws DAOException
     */
    @Test
    public void testDeleteSubscription() throws DAOException {
        when(userSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(userSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        assertEquals(true, userSubscriptionDAOImpl.deleteUserSubscription(userSubscription));
        verify(jdbcTemplate).update(DELETE_USER_SUBSCRIPTION, VALID_USER_ID, VALID_CATEGORY_ID);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getSubscribedUsersByCategory(UserSubscription)}
     * when category id is negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSubscribedUsersByCategoryWhenCategoryIdNegative() throws DAOException {
        when(category.getId()).thenReturn(-2);
        userSubscriptionDAOImpl.getSubscribedUsersByCategory(category);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getSubscribedUsersByCategory(UserSubscription)}
     * when valid category id is provided.
     *
     * @throws DAOException
     */
    @Test
    public void testGetSubscribedUsersByCategory() throws DAOException {
        when(category.getId()).thenReturn(VALID_CATEGORY_ID);
        newUserList = userSubscriptionDAOImpl.getSubscribedUsersByCategory(category);
        assertEquals("MK051340", newUserList.get(0));
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getLearnersSubscribedToCategoriesBelongingToResource(com.cerner.devcenter.education.models.Resource)}
     * when null {@link Resource} object is passes as parameter.
     *
     * @throws DAOException
     */
    @Test(expected = NullPointerException.class)
    public void testGetLearnersSubscribedToCategoriesBelongingToResource_ResourceNull() throws DAOException {
        userSubscriptionDAOImpl.getLearnersSubscribedToCategoriesBelongingToResource(null);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getLearnersSubscribedToCategoriesBelongingToResource(com.cerner.devcenter.education.models.Resource)}
     * when resource id is not greater than zero.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetLearnersSubscribedToCategoriesBelongingToResource_ResourceIDNotGreaterThanZero()
            throws DAOException {
        when(resource.getResourceId()).thenReturn(-2);
        userSubscriptionDAOImpl.getLearnersSubscribedToCategoriesBelongingToResource(resource);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getLearnersSubscribedToCategoriesBelongingToResource(com.cerner.devcenter.education.models.Resource)}
     * when an exception is thrown.
     *
     * @throws DAOException
     */
    @Test
    public void testGetLearnersSubscribedToCategoriesBelongingToResource_DAOException() throws DAOException {
        when(resource.getResourceId()).thenReturn(1);
        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), anyString()))
                .thenThrow(EmptyResultDataAccessException.class);
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        userSubscriptionDAOImpl.getLearnersSubscribedToCategoriesBelongingToResource(resource);
    }

    /**
     * Test
     * {@link UserSubscriptionDAOImpl#getLearnersSubscribedToCategoriesBelongingToResource(com.cerner.devcenter.education.models.Resource)}
     * when valid parameter is passed.
     *
     * @throws DAOException
     */
    @Test
    public void testGetLearnersSubscribedToCategoriesBelongingToResource_ValidInput() throws DAOException {
        when(resource.getResourceId()).thenReturn(1);
        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), anyString())).thenReturn(learnerList);
        List<Learner> actualLearners = new ArrayList<>();
        actualLearners = userSubscriptionDAOImpl.getLearnersSubscribedToCategoriesBelongingToResource(resource);
        assertEquals(1, actualLearners.size());
        assertEquals(VALID_USER_ID, actualLearners.get(0).getUserId());
    }
}