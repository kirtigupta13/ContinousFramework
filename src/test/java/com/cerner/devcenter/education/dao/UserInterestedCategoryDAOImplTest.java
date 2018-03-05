package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.DataAccessException;
import com.cerner.devcenter.education.dao.UserInterestedCategoryDAOImpl.UserInterestedCategoryRowMapper;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.UserInterestedCategory;

/**
 * Class that tests the functionality of {@link UserInterestedCategoryDAOImpl}
 * class.
 * 
 * @author Gunjan Kaphle (GK045931)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserInterestedCategoryDAOImplTest {
    @InjectMocks
    private UserInterestedCategoryDAOImpl userInterestedCategoryDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String ADD_INTERESTED_CATEGORY_FOR_USER = "INSERT INTO user_interested_category (user_id, category_id, skill_level, interest_level) VALUES (?,?,?,?)";
    private static final String UPDATE_INTERESTED_CATEGORY = "UPDATE user_interested_category set skill_level=?, interest_level=? where user_id=? and category_id=?";
    public static final String VALID_USER_ID = "USER123";
    public static final int VALID_CATEGORY_ID = 4;
    public static final String VALID_CATEGORY_NAME = "Java";
    public static final String VALID_CATEGORY_DESC = "Fundamentals of Java.";
    public static final int VALID_SKILL_LEVEL = 1;
    public static final int VALID_INTEREST_LEVEL = 5;
    public static final int INVALID_SKILL_LEVEL = 9;
    public static final int INVALID_INTEREST_LEVEL = 12;
    public static final int NEGATIVE_SKILL_LEVEL = -2;
    public static final int NEGATIVE_INTEREST_LEVEL = -3;
    private static final String DELETE_INTERESTED_CATEGORY_FOR_USER = "DELETE FROM user_interested_category WHERE user_id = ? and category_id = ?";

    private UserInterestedCategory userInterestedCategory;
    private UserInterestedCategory newUserInterestedCategory;
    private Category category;
    private List<UserInterestedCategory> listOfUserInterestedCategory;
    private List<UserInterestedCategory> newListOfUserInterestedCategory;
    private UserInterestedCategoryRowMapper userInterestedCategoryMapper;
    private int[] categoryIds;

    @Before
    public void setup() throws SQLException {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        userInterestedCategory = new UserInterestedCategory(
                VALID_USER_ID,
                category,
                VALID_SKILL_LEVEL,
                VALID_INTEREST_LEVEL);
        newUserInterestedCategory = new UserInterestedCategory();
        listOfUserInterestedCategory = new ArrayList<UserInterestedCategory>();
        newListOfUserInterestedCategory = new ArrayList<UserInterestedCategory>();
        listOfUserInterestedCategory.add(userInterestedCategory);
        userInterestedCategoryMapper = new UserInterestedCategoryRowMapper();
        categoryIds = new int[2];
        categoryIds[0] = 1;
        categoryIds[1] = 2;
        when(
                jdbcTemplate.update(
                        ADD_INTERESTED_CATEGORY_FOR_USER,
                        VALID_USER_ID,
                        VALID_CATEGORY_ID,
                        VALID_SKILL_LEVEL,
                        VALID_INTEREST_LEVEL)).thenReturn(1);
        when(
                jdbcTemplate.update(
                        UPDATE_INTERESTED_CATEGORY,
                        VALID_SKILL_LEVEL,
                        VALID_INTEREST_LEVEL,
                        VALID_USER_ID,
                        VALID_CATEGORY_ID)).thenReturn(1);
        when(resultSet.getString("user_id")).thenReturn(VALID_USER_ID);
        when(resultSet.getInt("category_id")).thenReturn(VALID_CATEGORY_ID);
        when(resultSet.getInt("skill_level")).thenReturn(VALID_SKILL_LEVEL);
        when(resultSet.getInt("interest_level")).thenReturn(VALID_INTEREST_LEVEL);
        when(resultSet.getString("name")).thenReturn(VALID_CATEGORY_NAME);
        when(resultSet.getString("description")).thenReturn(VALID_CATEGORY_DESC);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#getUserInterestedCategoryByUserId(String)}
     * when the user id is empty.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInterestedCategoriesByUserIdWhenEmpty() throws DAOException {
        userInterestedCategoryDAOImpl.getUserInterestedCategoryByUserId("");
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#getUserInterestedCategoryByUserId(String)}
     * when the user id is null.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInterestedCategoriesByUserIdWhenNull() throws DAOException {
        userInterestedCategoryDAOImpl.getUserInterestedCategoryByUserId(null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#getUserInterestedCategoryByUserId(String)}
     * when the query throws a {@link EmptyResultDataAccessException}. Expects
     * {@link DAOException}.
     * 
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testGetInterestedCategoriesThrowsException() throws DAOException {
        doThrow(EmptyResultDataAccessException.class)
                .when(jdbcTemplate)
                .query(anyString(), any(UserInterestedCategoryRowMapper.class), anyString());
        userInterestedCategoryDAOImpl.getUserInterestedCategoryByUserId(VALID_USER_ID);
    }

    /**
     * Test the functionality of
     * {@link UserInterestedCategoryDAOImpl#getUserInterestedCategoryByUserId(String)}
     * with a valid user id and valid return from database query.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetInterestedCategoriesUserIdValid() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserInterestedCategoryRowMapper.class), anyObject()))
                .thenReturn(listOfUserInterestedCategory);
        newListOfUserInterestedCategory = userInterestedCategoryDAOImpl
                .getUserInterestedCategoryByUserId(VALID_USER_ID);
        assertEquals(VALID_USER_ID, newListOfUserInterestedCategory.get(0).getUserID());
        assertEquals(category, newListOfUserInterestedCategory.get(0).getCategory());
        assertEquals(VALID_SKILL_LEVEL, newListOfUserInterestedCategory.get(0).getSkillLevel());
        assertEquals(VALID_INTEREST_LEVEL, newListOfUserInterestedCategory.get(0).getInterestLevel());
    }

    /**
     * Test the functionality of
     * {@link UserInterestedCategoryRowMapper#mapRow(ResultSet, int)}
     * 
     * @throws SQLException
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newUserInterestedCategory = userInterestedCategoryMapper.mapRow(resultSet, 1);
        assertEquals(VALID_USER_ID, newUserInterestedCategory.getUserID());
        assertEquals(category.getId(), newUserInterestedCategory.getCategory().getId());
        assertEquals(VALID_SKILL_LEVEL, newUserInterestedCategory.getSkillLevel());
        assertEquals(VALID_INTEREST_LEVEL, newUserInterestedCategory.getInterestLevel());
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when user id is null.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenUserIdNull() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(null, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when user id is empty.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenUserIdEmpty() throws DAOException {
        userInterestedCategoryDAOImpl.addUserInterestedCategory("", category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when {@link Category} object is null.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenTopicNull() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, null, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when skill level is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenSkillLevelInvalid() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, category, INVALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when interest level is invalid.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenInterestLevelInvalid() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, INVALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when skill level is negative.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenSkillLevelNegative() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, category, NEGATIVE_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when interest level is negative.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddInterestedCategoryWhenInterestLevelNegative() throws DAOException {
        userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, NEGATIVE_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#addUserInterestedCategory(String, Category, int, int)}
     * when all input are valids. Must return true.
     * 
     * @throws DAOException
     */
    @Test
    public void testAddInterestedCategoryValid() throws DAOException {
        boolean isAddToDbSuccessful = userInterestedCategoryDAOImpl
                .addUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
        assertTrue(isAddToDbSuccessful);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when categoryId is negative. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryNegativeTopicId() throws DAOException {
        userInterestedCategoryDAOImpl.deleteUserInterestedCategory(VALID_USER_ID, -1);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when user id is null. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdatedInterestedCategoryWhenUserIdNull() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(null, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when categoryId is 0. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryZeroTopicId() throws DAOException {
        userInterestedCategoryDAOImpl.deleteUserInterestedCategory(VALID_USER_ID, 0);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when user id is empty. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenUserIdEmpty() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory("", category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when userId is empty. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryForEmptyUserID() throws DAOException {
        userInterestedCategoryDAOImpl.deleteUserInterestedCategory("", 1);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when category object is null. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenTopicNull() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, null, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when userId is null. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryForNullUserID() throws DAOException {
        userInterestedCategoryDAOImpl.deleteUserInterestedCategory(null, 1);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when skill level is invalid. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    public void testUpdateInterestedCategoryWhenSkillLevelInvalid() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, category, INVALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when userId is empty with whitespace. Expects
     * {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserInterestedCategoryForEmptyUserIDWithWhiteSpace() throws DAOException {
        userInterestedCategoryDAOImpl.deleteUserInterestedCategory(" ", 1);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when interest level is invalid. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenInterestLevelInvalid() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, INVALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when skill level is negative. Expects {@link IllegalArgumentException}.
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenSkillLevelNegative() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, category, NEGATIVE_SKILL_LEVEL, VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * when interest level is negative. Expects {@link IllegalArgumentException}
     * 
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInterestedCategoryWhenInterestLevelNegative() throws DAOException {
        userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, NEGATIVE_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategory(String, int)}
     * when userId is valid.
     * 
     * @throws DAOException
     */
    @Test
    public void testDeleteUserInterestedCategoryForValidUserID() throws DAOException {
        when(jdbcTemplate.update(DELETE_INTERESTED_CATEGORY_FOR_USER, VALID_USER_ID, VALID_CATEGORY_ID)).thenReturn(1);
        assertTrue(userInterestedCategoryDAOImpl.deleteUserInterestedCategory(VALID_USER_ID, VALID_CATEGORY_ID));
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#updateUserInterestedCategory(String, Category, int, int)}
     * with all valid parameters. interested topics
     * 
     * @throws DAOException
     */
    public void testUpdateInterestedCategoryValid() throws DAOException {
        boolean isUpdateToDbSuccessful = userInterestedCategoryDAOImpl
                .updateUserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
        assertTrue(isUpdateToDbSuccessful);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with <code>null</code> user id.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithUsedIdIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(null, categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with blank user id.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithUsedIdIsBlank() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(" ", categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with empty user id.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithUsedIdIsEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch("", categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with empty category id.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithTopicIdsIsEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(VALID_USER_ID, new int[0]);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with <code>null</code> category id.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithTopicIdsIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(VALID_USER_ID, null);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * throws a DAOException.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWhenThrowsManagerException() throws DAOException {
        expectedException.expect(DAOException.class);
        doThrow(DAOException.class).when(jdbcTemplate).batchUpdate(
                eq(DELETE_INTERESTED_CATEGORY_FOR_USER),
                any(BatchPreparedStatementSetter.class));
        userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(VALID_USER_ID, categoryIds);
    }

    /**
     * Test
     * {@link UserInterestedCategoryDAOImpl#deleteUserInterestedCategoryInBatch(String, int[])}
     * with valid values and expects true.
     * 
     * @throws DAOException
     */
    @Test
    public void testdeleteUserInterestedCategoryInBatchWithValidValues() throws DAOException {
        assertTrue(userInterestedCategoryDAOImpl.deleteUserInterestedCategoryInBatch(VALID_USER_ID, categoryIds));

    }

}
