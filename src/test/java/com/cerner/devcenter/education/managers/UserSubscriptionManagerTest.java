package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.cerner.devcenter.education.admin.CategoryDAO;
import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserSubscriptionDAO;
import com.cerner.devcenter.education.exceptions.DuplicateUserSubscriptionException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserSubscription;

/**
 * Class that tests the functionalities of {@link UserSubscriptionManager}.
 *
 * @author Mani Teja Kurapati (MK051340)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserSubscriptionManagerTest {

    @InjectMocks
    private UserSubscriptionManager userSubscriptionManager;
    @Mock
    private UserSubscriptionDAO mockUserSubscriptionDAO;
    @Mock
    private UserSubscription mockUserSubscription;
    @Mock
    private CategoryDAO mockCategoryDAO;
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String VALID_USER_ID = "MK051340";
    private static final int VALID_CATEGORY_ID = 1;

    private List<Integer> listOfUserSubscribedCategories;
    private List<Category> emptyListOfUserSubscriptions;
    private List<Category> mockSubscribedCategoriesList;
    private List<Category> mockCategoryList;

    @Before
    public void setup() throws DAOException {
        listOfUserSubscribedCategories = new ArrayList<>();
        emptyListOfUserSubscriptions = new ArrayList<>();
        mockSubscribedCategoriesList = new ArrayList<>();
        mockCategoryList = new ArrayList<>();
        mockCategoryList.add(new Category(2));

        listOfUserSubscribedCategories.add(1);
        mockSubscribedCategoriesList.add(new Category(2));
        when(mockUserSubscription.getUserId()).thenReturn(VALID_USER_ID);
        when(mockUserSubscriptionDAO.getSubscribedCategoriesByUser(VALID_USER_ID))
                .thenReturn(mockSubscribedCategoriesList);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#addUserSubscription(UserSubscription)}
     * with valid inputs should return true.
     *
     * @throws DuplicateUserSubscriptionException
     * @throws DAOException
     */
    @Test
    public void testAddUserSubscription() throws DuplicateUserSubscriptionException, DAOException {
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        when(mockUserSubscriptionDAO.getSubscribedCategoriesByUser(VALID_USER_ID))
                .thenReturn(mockSubscribedCategoriesList);
        when(mockUserSubscriptionDAO.addUserSusbcription(mockUserSubscription)).thenReturn(true);
        assertTrue(userSubscriptionManager.addUserSubscription(mockUserSubscription));
    }

    /**
     * Test
     * {@link UserSubscriptionManager#addUserSubscription(UserSubscription)}
     * returns false when the query in the DAO fails to add the record in the
     * database.
     *
     * @throws DAOException
     * @throws DuplicateUserSubscriptionException
     */
    @Test
    public void testAddUserSubscriptionUnsuccessful() throws DAOException, DuplicateUserSubscriptionException {
        when(mockUserSubscriptionDAO.addUserSusbcription(mockUserSubscription)).thenReturn(false);
        assertFalse(userSubscriptionManager.addUserSubscription(mockUserSubscription));
    }

    /**
     * Test
     * {@link UserSubscriptionManager#addUserSubscription(UserSubscription)}
     * when the DAO throws {@link DAOException}. Expects
     * {@link ManagerException}.
     *
     * @throws DAOException
     * @throws DuplicateUserSubscriptionException
     */
    @Test(expected = ManagerException.class)
    public void testAddUserSubscriptionThrowsManagerException()
            throws DAOException, DuplicateUserSubscriptionException {
        when(mockUserSubscriptionDAO.addUserSusbcription(mockUserSubscription)).thenThrow(new DAOException());
        userSubscriptionManager.addUserSubscription(mockUserSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#addUserSubscription(UserSubscription)}
     * when the subscription already exists in the database for that user.
     * Expects {@link DuplicateUserSubscriptionException}.
     *
     * @throws DAOException
     * @throws DuplicateUserSubscriptionException
     */
    @Test(expected = DuplicateUserSubscriptionException.class)
    public void testAddUserSubscriptionThatAlreadyExists() throws DAOException, DuplicateUserSubscriptionException {
        when(mockUserSubscription.getCategoryId()).thenReturn(2);
        userSubscriptionManager.addUserSubscription(mockUserSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#addUserSubscription(UserSubscription)}
     * when {@link UserSubscription} object passed in is null. Expects
     * {@link IllegalArgumentException}.
     *
     * @throws DuplicateUserSubscriptionException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserSubscriptionWhenPassedObjectNull() throws DuplicateUserSubscriptionException {
        userSubscriptionManager.addUserSubscription(null);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#getSubscribedcategoriesByUser(String)}
     * when user id is null. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSubscribedcategoriesByUserWhenPassedIdNull() {
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionManager.getSubscribedcategoriesByUser(null);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#getSubscribedcategoriesByUser(String)}
     * when user id is empty. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetSubscribedcategoriesByUserWhenPassedIdEmpty() {
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        userSubscriptionManager.getSubscribedcategoriesByUser("");
    }

    /**
     * Test
     * {@link UserSubscriptionManager#getSubscribedcategoriesByUser(String)}
     * when the DAO throws DAOException. Expects {@link ManagerException}.
     *
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testGetSubscribedcategoriesByUserThrowsManagerException() throws DAOException {
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        when(mockUserSubscriptionDAO.getSubscribedCategoriesByUser(VALID_USER_ID)).thenThrow(new DAOException());
        userSubscriptionManager.getSubscribedcategoriesByUser(VALID_USER_ID);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#checkIfCategoryAlreadySubscribedByUser(UserSubscription)}
     * when the category already exists as subscription for that user. Must
     * return true.
     *
     * @throws DAOException
     */
    @Test
    public void testCheckIfCategoryAlreadySubscribedByUser() throws DAOException {
        when(mockUserSubscription.getCategoryId()).thenReturn(2);
        assertTrue(userSubscriptionManager.checkIfCategoryAlreadySubscribedByUser(mockUserSubscription));
    }

    /**
     * Test
     * {@link UserSubscriptionManager#checkIfCategoryAlreadySubscribedByUser(UserSubscription)}
     * when the category does not exists as subscription for that user. Must
     * return false.
     *
     * @throws DAOException
     */
    @Test
    public void testCheckIfCategoryAlreadySubscribedByUserNotAlreadyExists() throws DAOException {
        when(mockUserSubscription.getCategoryId()).thenReturn(VALID_CATEGORY_ID);
        when(mockUserSubscriptionDAO.getSubscribedCategoriesByUser(VALID_USER_ID))
                .thenReturn(emptyListOfUserSubscriptions);
        assertFalse(userSubscriptionManager.checkIfCategoryAlreadySubscribedByUser(mockUserSubscription));
    }

    /**
     * Test
     * {@link UserSubscriptionManager#deleteUserSubscription(UserSubscription)}
     * when UserSubscription is null. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserSubscriptionWhenNull() {
        userSubscriptionManager.deleteUserSubscription(null);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#deleteUserSubscription(UserSubscription)}
     * when the DAO throws DAOException. Expects {@link ManagerException}.
     *
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testDeleteUserSubscriptionThrowsManagerException() throws DAOException {
        when(mockUserSubscriptionDAO.deleteUserSubscription(mockUserSubscription)).thenThrow(new DAOException());
        userSubscriptionManager.deleteUserSubscription(mockUserSubscription);
    }

    /**
     * Test
     * {@link UserSubscriptionManager#deleteUserSubscription(UserSubscription)}
     * for valid input.
     *
     * @throws DAOException
     */
    @Test
    public void testDeleteUserSubscription() throws DuplicateUserSubscriptionException, DAOException {
        when(mockUserSubscriptionDAO.deleteUserSubscription(mockUserSubscription)).thenReturn(true);
        assertTrue(userSubscriptionManager.deleteUserSubscription(mockUserSubscription));
        verify(mockUserSubscriptionDAO).deleteUserSubscription(mockUserSubscription);
    }
}
