package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
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
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserInterestedCategoryDAO;
import com.cerner.devcenter.education.exceptions.DuplicateUserInterestedCategoryException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;

/**
 * Class that tests the functionalities of
 * {@link UserInterestedCategoryManager}.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserInterestedCategoryManagerTest {

    @InjectMocks
    private UserInterestedCategoryManager userInterestedCategoryManager;
    @Mock
    private UserInterestedCategoryDAO mockUserInterestedCategoryDAO;
    @Mock
    private UserInterestedCategory mockUserInterestedCategory;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static final String VALID_USER_ID = "AB12345";
    public static final int VALID_CATEGORY_ID = 4;
    public static final String VALID_CATEGORY_NAME = "Java";
    public static final String VALID_CATEGORY_DESC = "Fundamentals of Java.";
    public static final int VALID_SKILL_LEVEL = 1;
    public static final int VALID_INTEREST_LEVEL = 5;

    private UserInterestedCategory userInterestedCategory;
    private Category category;
    private List<UserInterestedCategory> listOfUserInterestedCategory;
    private List<UserInterestedCategory> emptyListOfUserInterestedCategory;
    private int[] categoryIds;

    @Before
    public void setup() throws DAOException {
        MockitoAnnotations.initMocks(this);
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        userInterestedCategory = new UserInterestedCategory(
                VALID_USER_ID,
                category,
                VALID_SKILL_LEVEL,
                VALID_INTEREST_LEVEL);
        listOfUserInterestedCategory = new ArrayList<UserInterestedCategory>();
        emptyListOfUserInterestedCategory = new ArrayList<UserInterestedCategory>();
        listOfUserInterestedCategory.add(userInterestedCategory);
        categoryIds = new int[2];
        categoryIds[0] = 1;
        categoryIds[1] = 2;
        when(mockUserInterestedCategory.getUserID()).thenReturn(VALID_USER_ID);
        when(mockUserInterestedCategory.getCategory()).thenReturn(category);
        when(mockUserInterestedCategory.getSkillLevel()).thenReturn(VALID_SKILL_LEVEL);
        when(mockUserInterestedCategory.getInterestLevel()).thenReturn(VALID_INTEREST_LEVEL);
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenReturn(emptyListOfUserInterestedCategory);
        when(
                mockUserInterestedCategoryDAO
                        .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenReturn(true);
        when(
                mockUserInterestedCategoryDAO
                        .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenReturn(true);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#addUserInterestedCategory(UserInterestedCategory)}
     * with valid inputs should return true.
     * 
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test
    public void testAddUserInterestedCategory() throws DuplicateUserInterestedCategoryException {
        assertTrue(userInterestedCategoryManager.addUserInterestedCategory(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#addUserInterestedCategory(UserInterestedCategory)}
     * returns false when the query in the DAO fails to add the record in the
     * database.
     * 
     * @throws DAOException
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test
    public void testAddUserInterestedCategoryUnsuccessful()
            throws DAOException, DuplicateUserInterestedCategoryException {
        when(
                mockUserInterestedCategoryDAO
                        .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenReturn(false);
        assertFalse(userInterestedCategoryManager.addUserInterestedCategory(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#addUserInterestedCategory(UserInterestedCategory)}
     * when the DAO throws {@link DAOException}. Expects
     * {@link ManagerException}.
     * 
     * @throws DAOException
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = ManagerException.class)
    public void testAddUserInterestedCategoryThrowsManagerException()
            throws DAOException, DuplicateUserInterestedCategoryException {
        when(
                mockUserInterestedCategoryDAO
                        .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenThrow(new DAOException());
        userInterestedCategoryManager.addUserInterestedCategory(userInterestedCategory);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#addUserInterestedCategory(UserInterestedCategory)}
     * when the category already exists in the database for that user. Expects
     * {@link DuplicateUserInterestedCategoryException}.
     * 
     * @throws DAOException
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = DuplicateUserInterestedCategoryException.class)
    public void testAddUserInterestedCategoryThatAlreadyExists()
            throws DAOException, DuplicateUserInterestedCategoryException {
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        userInterestedCategoryManager.addUserInterestedCategory(mockUserInterestedCategory);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#addUserInterestedCategory(UserInterestedCategory)}
     * when {@link UserInterestedCategory} object passed in is null. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DuplicateUserInterestedCategoryException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserInterestedCategoryWhenPassedObjectNull() throws DuplicateUserInterestedCategoryException {
        userInterestedCategoryManager.addUserInterestedCategory(null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * when {@link UserInterestedCategory} object passed in is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUserInterestedCategoryWhenPassedObjectNull() {
        userInterestedCategoryManager.updateUserInterestedCategory(null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * when
     * {@link UserInterestedCategoryDAO#updateUserInterestedCategory(String, Category, int, int)}
     * throws {@link DAOException}. Expects {@link ManagerException}.
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testUpdateUserInterestedCategoryDAOThrowsDAOException() throws DAOException {
        when(
                mockUserInterestedCategoryDAO
                        .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenThrow(new DAOException());
        userInterestedCategoryManager.updateUserInterestedCategory(userInterestedCategory);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * when
     * {@link UserInterestedCategoryDAO#updateUserInterestedCategory(String, Category, int, int)}
     * is unsuccessful and returns false.
     * 
     * @throws DAOException
     */
    @Test
    public void testUpdateUserInterestedCategoryUnsuccessful() throws DAOException {
        when(
                mockUserInterestedCategoryDAO
                        .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL))
                                .thenReturn(false);
        assertFalse(userInterestedCategoryManager.updateUserInterestedCategory(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#updateUserInterestedCategory(UserInterestedCategory)}
     * with valid inputs and valid return from
     * {@link UserInterestedCategoryDAO}.
     */
    @Test
    public void testUpdateUserInterestedCategory() {
        assertTrue(userInterestedCategoryManager.updateUserInterestedCategory(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#getUserInterestedCategoriesById(String)}
     * when user id is null. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserInterestedCategoryWhenPassedIdNull() {
        userInterestedCategoryManager.getUserInterestedCategoriesById(null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#getUserInterestedCategoriesById(String)}
     * when user id is empty. Expects {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserInterestedCategoryWhenPassedIdEmpty() {
        userInterestedCategoryManager.getUserInterestedCategoriesById("");
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#getUserInterestedCategoriesById(String)}
     * when the DAO throws DAOException. Expects {@link ManagerException}.
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testGetInterestedCategoryThrowsManagerException() throws DAOException {
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenThrow(new DAOException());
        userInterestedCategoryManager.getUserInterestedCategoriesById(VALID_USER_ID);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#getUserInterestedCategoriesById(String)}
     * with valid user id.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllUserInterestedCategories() throws DAOException {
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        UserInterestedCategory newUserInterestedCategory = userInterestedCategoryManager
                .getUserInterestedCategoriesById(VALID_USER_ID)
                .get(0);
        assertEquals(VALID_USER_ID, newUserInterestedCategory.getUserID());
        assertEquals(category, newUserInterestedCategory.getCategory());
        assertEquals(VALID_SKILL_LEVEL, newUserInterestedCategory.getSkillLevel());
        assertEquals(VALID_INTEREST_LEVEL, newUserInterestedCategory.getInterestLevel());
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#checkIfCategoryAlreadyExistsAsInterestedForUser(UserInterestedCategory)}
     * when the category already exists as interested for that user. Must return
     * true.
     * 
     * @throws DAOException
     */
    @Test
    public void testCheckIfUserInterestedCategoryAlreadyExists() throws DAOException {
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        assertTrue(
                userInterestedCategoryManager
                        .checkIfCategoryAlreadyExistsAsInterestedForUser(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#checkIfCategoryAlreadyExistsAsInterestedForUser(UserInterestedCategory)}
     * when the category does not exists as interested for that user. Must return
     * false.
     * 
     * @throws DAOException
     */
    @Test
    public void testCheckIfUserInterestedCategoryNotAlreadyExists() throws DAOException {
        when(mockUserInterestedCategoryDAO.getUserInterestedCategoryByUserId(VALID_USER_ID))
                .thenReturn(emptyListOfUserInterestedCategory);
        assertFalse(
                userInterestedCategoryManager
                        .checkIfCategoryAlreadyExistsAsInterestedForUser(mockUserInterestedCategory));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategory(UserInterestedCategory)}
     * when UserInterestedCategory is null. Expects
     * {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryWhenNull() {
        userInterestedCategoryManager.deleteUserInterestedCategory(null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategory(UserInterestedCategory)}
     * when the DAO throws DAOException. Expects {@link ManagerException}.
     * 
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testDeleteUserInterestedCategoryThrowsManagerException() throws DAOException {
        when(mockUserInterestedCategoryDAO.deleteUserInterestedCategory(VALID_USER_ID, VALID_CATEGORY_ID))
                .thenThrow(new DAOException());
        userInterestedCategoryManager.deleteUserInterestedCategory(userInterestedCategory);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategory(UserInterestedCategory)}
     * for valid input.
     * 
     * @throws DAOException
     */
    @Test
    public void testDeleteUserInterestedCategory() throws DAOException {
        userInterestedCategoryManager.deleteUserInterestedCategory(userInterestedCategory);
        verify(mockUserInterestedCategoryDAO).deleteUserInterestedCategory(VALID_USER_ID, VALID_CATEGORY_ID);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with user id as <code>null</code>.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWhenUserIdIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(null, categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with user id as blank.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWhenUserIdIsBlank() {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(" ", categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with user id as empty.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWhenUserIdIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch("", categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with category id array as <code>null</code>.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWhenCategoryIdIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(VALID_USER_ID, null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with category id array as blank.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWhenCategoryIdIsBlank() {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(null, new int[0]);
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * with valid values and expects true.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchWithValidValues() throws DAOException {
        when(mockUserInterestedCategoryDAO.deleteUserInterestedCategoryInBatch(VALID_USER_ID, categoryIds))
                .thenReturn(true);
        assertTrue(userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(VALID_USER_ID, categoryIds));
    }

    /**
     * Test
     * {@link UserInterestedCategoryManager#deleteUserInterestedCategoriesInBatch(String, int[])}
     * throws a ManagerException.
     */
    @Test
    public void testDeleteUserInterestedCategoriesInBatchThrowsManagerException() throws DAOException {
        expectedException.expect(ManagerException.class);
        when(mockUserInterestedCategoryDAO.deleteUserInterestedCategoryInBatch(VALID_USER_ID, categoryIds))
                .thenThrow(new DAOException());
        userInterestedCategoryManager.deleteUserInterestedCategoriesInBatch(VALID_USER_ID, categoryIds);
    }
}
