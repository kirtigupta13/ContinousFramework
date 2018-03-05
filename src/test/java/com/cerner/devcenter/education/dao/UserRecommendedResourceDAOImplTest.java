package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.dao.UserRecommendedResourceDAOImpl.UserRecommendedResourceRowMapper;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.utils.Constants;

/**
 * Tests the functionality of {@link UserRecommendedResourceDAOImpl}.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Mayur Rajendran (MT049536)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRecommendedResourceDAOImplTest {

    private static final String INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE = "The minimum rating required should be non-negative";
    private static final String INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE = "The minimum number of ratings required should be non-negative";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User Id cannot be null, empty or whitespace.";

    private static final double VALID_MINIMUM_RATING_REQUIRED = 2.0;
    private static final int VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED = 10;
    private static final String VALID_USER_ID = "AB012345";
    private static final int VALID_RESOURCE_ID = 2;
    private static final String VALID_RESOURCE_DESC = "Learn loops in java.";
    private static final String VALID_RESOURCE_NAME = "Loop in Java";
    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final String VALID_CATEGORY_DESC = "Fundamentals of Java.";
    private static final int VALID_DIFFICULTY_LEVEL = 1;
    private static final int VALID_INTEREST_LEVEL = 5;
    private static final String VALID_URL = "http://www.junit.org";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "        ";

    private static URL validResourceUrl;

    @InjectMocks
    private UserRecommendedResourceDAOImpl userRecommendedResourceDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Mock
    private Category mockCategory;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserRecommendedResource userRecommendedResource;
    private UserRecommendedResource newUserRecommendedResource;
    private Category category;
    private Resource resource;
    private List<UserRecommendedResource> listOfUserRecommendedResource;
    private List<UserRecommendedResource> newListOfUserRecommendedResource;
    private UserRecommendedResourceRowMapper userRecommendedResourceMapper;
    private List<Category> categoryList;

    @Before
    public void setup() throws SQLException, MalformedURLException {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        validResourceUrl = new URL(VALID_URL);
        resource = new Resource(VALID_RESOURCE_ID, validResourceUrl, VALID_RESOURCE_DESC, VALID_RESOURCE_NAME);
        userRecommendedResource = new UserRecommendedResource(resource, category, VALID_DIFFICULTY_LEVEL,
                VALID_INTEREST_LEVEL);
        newUserRecommendedResource = new UserRecommendedResource();
        listOfUserRecommendedResource = new ArrayList<UserRecommendedResource>();
        newListOfUserRecommendedResource = new ArrayList<UserRecommendedResource>();
        listOfUserRecommendedResource.add(userRecommendedResource);
        userRecommendedResourceMapper = new UserRecommendedResourceRowMapper();
        categoryList = getTestCategoryList(5);
        when(resultSet.getInt("resource_id")).thenReturn(VALID_RESOURCE_ID);
        when(resultSet.getString("resource_name")).thenReturn(VALID_RESOURCE_NAME);
        when(resultSet.getString("link")).thenReturn(VALID_URL);
        when(resultSet.getString("resource_description")).thenReturn(VALID_RESOURCE_DESC);
        when(resultSet.getInt("id")).thenReturn(VALID_CATEGORY_ID);
        when(resultSet.getString("category_name")).thenReturn(VALID_CATEGORY_NAME);
        when(resultSet.getString("category_description")).thenReturn(VALID_CATEGORY_DESC);

        when(resultSet.getInt("difficulty_level")).thenReturn(VALID_DIFFICULTY_LEVEL);
        when(resultSet.getInt("interest_level")).thenReturn(VALID_INTEREST_LEVEL);
    }

    /**
     * Test
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * when the user id is empty.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedResourcesWhenUserIdEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(EMPTY_STRING, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Test
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * when the user id is null.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedResourcesByUserIdWhenNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(null, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Expects
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * to throw an {@link IllegalArgumentException} when userID is whitespace.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedResourcesForTheUserWhenUserIdIsNull() throws DAOException {
        try {
            userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(BLANK_STRING,
                    VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_USER_ID_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * to throw an {@link IllegalArgumentException} when minimumRatingRequired
     * is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedResourcesForTheUserWhenMinimumRatingRequiredIsNegative() throws DAOException {
        try {
            userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(VALID_USER_ID,
                    -(VALID_MINIMUM_RATING_REQUIRED) - 1, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * to function as expected when minimumRatingRequired is 0.
     */
    @Test
    public void testGetRecommendedResourcesForTheUserWhenMinimumRatingRequiredIsZero() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenReturn(listOfUserRecommendedResource);
        assertEquals(listOfUserRecommendedResource, userRecommendedResourceDAOImpl
                .getRecommendedResourcesForTheUser(VALID_USER_ID, 0, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED));
    }

    /**
     * Expects
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * to throw an {@link IllegalArgumentException} when
     * minimumNumberOfRatingsRequired is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedResourcesForTheUserWhenMinimumNumberOfRatingsRequiredIsNegative()
            throws DAOException {
        try {
            userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(VALID_USER_ID,
                    VALID_MINIMUM_RATING_REQUIRED, -(VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED) - 1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * to function as expected when minimumNumberOfRatingsRequired is 0.
     */
    @Test
    public void testGetRecommendedResourcesForTheUserWhenMinimumNumberOfRatingsRequiredIsZero() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenReturn(listOfUserRecommendedResource);
        assertEquals(listOfUserRecommendedResource, userRecommendedResourceDAOImpl
                .getRecommendedResourcesForTheUser(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED, 0));
    }

    /**
     * Test
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * when the query throws a {@link EmptyResultDataAccessException}. Expects
     * {@link DAOException}.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedResourcesThrowsException() throws DAOException {
        expectedException.expect(DAOException.class);
        doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate).query(anyString(),
                any(UserRecommendedResourceRowMapper.class), anyVararg());
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Test the functionality of
     * {@link UserRecommendedResourceDAOImpl#getRecommendedResourcesForTheUser(String, double, int)}
     * with a valid user id and valid return from database query.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedResourcesUserIdValid() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenReturn(listOfUserRecommendedResource);
        newListOfUserRecommendedResource = userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUser(
                VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertEquals(resource, newListOfUserRecommendedResource.get(0).getResource());
        assertEquals(category, newListOfUserRecommendedResource.get(0).getCategory());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfUserRecommendedResource.get(0).getDifficultyLevel());
        assertEquals(VALID_INTEREST_LEVEL, newListOfUserRecommendedResource.get(0).getInterestLevel());
    }

    /**
     * Test the functionality of
     * {@link UserRecommendedResourceRowMapper#mapRow(ResultSet, int)}
     *
     * @throws SQLException
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newUserRecommendedResource = userRecommendedResourceMapper.mapRow(resultSet, 1);
        assertEquals(resource.getResourceId(), newUserRecommendedResource.getResource().getResourceId());
        assertEquals(resource.getResourceLink(), newUserRecommendedResource.getResource().getResourceLink());
        assertEquals(resource.getResourceName(), newUserRecommendedResource.getResource().getResourceName());
        assertEquals(resource.getDescription(), newUserRecommendedResource.getResource().getDescription());
        assertEquals(category.getId(), newUserRecommendedResource.getCategory().getId());
        assertEquals(category.getName(), newUserRecommendedResource.getCategory().getName());
        assertEquals(category.getDescription(), newUserRecommendedResource.getCategory().getDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newUserRecommendedResource.getDifficultyLevel());
        assertEquals(VALID_INTEREST_LEVEL, newUserRecommendedResource.getInterestLevel());
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a null string for the user ID.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCatNullUserID() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(null, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with an empty string for the user ID.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryEmptyUserID() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(EMPTY_STRING, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a blank string for the user ID.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryBlankUserID() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(BLANK_STRING, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with an empty list of categories.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryEmptyCatList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_LIST_EMPTY);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID,
                Collections.<Category>emptyList(), VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a null list of categories.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryNullCategoryList() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_LIST_NULL);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, null,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a list containing a null Category.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryListHasNullItem() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_LIST_HAS_NULL_ITEM);
        categoryList.add(null);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a list containing a category with ID zero.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     */
    @Test
    public void testGetRecommendedByCategoryListHasZeroIdItem() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_ID_MUST_BE_POSITIVE);
        when(mockCategory.getId()).thenReturn(0);
        categoryList.add(mockCategory);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a list containing a category whose name is null.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     */
    @Test
    public void testGetRecommendedByCategoryListHasCategoryWithNullName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_NAME_NULL);
        when(mockCategory.getId()).thenReturn(VALID_CATEGORY_ID);
        when(mockCategory.getName()).thenReturn(null);
        categoryList.add(mockCategory);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a list containing a category whose name is empty.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     */
    @Test
    public void testGetRecommendedByCategoryListHasCategoryWithEmptyName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_NAME_EMPTY_OR_BLANK);
        when(mockCategory.getId()).thenReturn(VALID_CATEGORY_ID);
        when(mockCategory.getName()).thenReturn(EMPTY_STRING);
        categoryList.add(mockCategory);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * with a list containing a category whose name is blank.
     *
     * Expects a {@link IllegalArgumentException} to be thrown, with appropriate
     * messages.
     */
    @Test
    public void testGetRecommendedByCategoryListHasCategoryWithBlankName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_NAME_EMPTY_OR_BLANK);
        when(mockCategory.getId()).thenReturn(VALID_CATEGORY_ID);
        when(mockCategory.getName()).thenReturn(BLANK_STRING);
        categoryList.add(mockCategory);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * when querying the data throws an exception.
     *
     * Expects a {@link DAOException} to be thrown, with appropriate messages.
     *
     * @throws DAOException
     */
    @Test
    public void testGetRecommendedByCategoryQueryThrowsException() throws DAOException {
        expectedException.expect(DAOException.class);
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenThrow(dataAccessException);
        userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * when querying the data returns an empty list.
     */
    @Test
    public void testGetRecommendedByCategoryEmptyList() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenReturn(Collections.<UserRecommendedResource>emptyList());
        assertEquals(0, userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID,
                categoryList, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size());
    }

    /**
     * Tests the functionality of
     * {@link UserRecommendedResourceDAO#getRecommendedResourcesForTheUserInCategories(String, List, double, int)}
     * when querying the data returns an empty list.
     */
    @Test
    public void testGetRecommendedByCategoryReturnsData() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(UserRecommendedResourceRowMapper.class), anyVararg()))
                .thenReturn(listOfUserRecommendedResource);
        assertEquals(listOfUserRecommendedResource.size(),
                userRecommendedResourceDAOImpl.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID,
                        categoryList, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size());
    }

    /**
     * Used to create a {@link List} of {@link Category} objects with the
     * specified length.
     *
     * @param length
     *            - An {@link Integer} denoting the size of the list to return.
     * @return A {@link List} of {@link Category} objects.
     */
    private List<Category> getTestCategoryList(final int length) {
        final List<Category> categoryList = new ArrayList<Category>(length);
        for (int i = 0; i < length; i++) {
            final Category category = new Category(VALID_CATEGORY_ID + i, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
            categoryList.add(category);
        }
        return categoryList;
    }
}
