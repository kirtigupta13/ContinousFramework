package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.UserRecommendedResourceDAO;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.UserInterestedCategory;
import com.cerner.devcenter.education.models.UserRecommendedResource;
import com.cerner.devcenter.education.utils.Constants;

/**
 * This class tests the functionalities of
 * {@link UserRecommendedResourceManager}.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author Amos Bailey (AB032627)
 * @author Mayur Rajendran (MT049536)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRecommendedResourceManagerTest {

    private static final String INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE = "The minimum rating required should be non-negative";
    private static final String INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE = "The minimum number of ratings required should be non-negative";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User Id cannot be null, empty or whitespace.";

    private static final double VALID_MINIMUM_RATING_REQUIRED = 2.0;
    private static final int VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED = 10;
    private static final String VALID_USER_ID = "AB012345";
    private static final int VALID_RESOURCE_ID = 2;
    private static final String VALID_RESOURCE_DESC = "Learn loops in java.";
    private static final int VALID_CATEGORY_ID = 1;
    private static final int ANOTHER_VALID_CATEGORY_ID = 5;
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final String VALID_CATEGORY_DESC = "Fundamentals of Java.";
    private static final int VALID_DIFFICULTY_LEVEL = 1;
    private static final int VALID_SKILL_LEVEL = 1;
    private static final int VALID_INTEREST_LEVEL = 5;
    private static final double DELTA = 0.0000001;
    private static final int MAX_COUNT_OF_RESOURCES_TO_RECOMMEND = 10;
    private static final double SUM_OF_INTEREST_LEVEL = VALID_INTEREST_LEVEL + VALID_INTEREST_LEVEL;
    private static final int NUM_OF_RESOURCE_FOR_EACH_CATEGORY = 5;
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "      ";

    private static URL STATIC_URL = null;

    static {
        try {
            STATIC_URL = new URL("http://www.testing.com");
        } catch (final MalformedURLException e) {
            STATIC_URL = null;
        }
    }

    @InjectMocks
    private UserRecommendedResourceManager userRecommendedResourceManager;
    @Mock
    private UserRecommendedResourceDAO mockUserRecommendedResourceDAO;
    @Mock
    private UserInterestedCategoryManager userInterestedCategoryManager;
    @Mock
    private DAOException daoException;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserInterestedCategory userInterestedCategory;
    private List<UserInterestedCategory> listOfUserInterestedCategory = new ArrayList<>();
    private Category category;
    private final List<Integer> listOfOneCategoryThatHasResources = new ArrayList<>();
    private final List<Integer> listOfCategoryIdsThatHasResources = new ArrayList<>();
    private List<UserRecommendedResource> listOfUserRecommendedResource = new ArrayList<>();
    private final List<UserRecommendedResource> emptyListOfUserRecommendedResource = new ArrayList<>();
    private final Map<Integer, List<UserRecommendedResource>> mapOfCategoryIdToResources = new HashMap<>();
    private final Map<Integer, Integer> mapOfUserInterestedCategoryWithInterestLevel = new HashMap<>();
    private final Map<Integer, List<UserRecommendedResource>> combinedMapOfDifferentCategories = new HashMap<>();
    private final List<UserRecommendedResource> combinedResourcesListOfDifferentCategories = new ArrayList<>();
    private List<Category> categoryList;

    @Before
    public void setup() {
        category = new Category(VALID_CATEGORY_ID, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        userInterestedCategory = new UserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL);
        listOfUserInterestedCategory.add(userInterestedCategory);
        listOfUserRecommendedResource = createListOfRecommendedResourceForCategory(VALID_CATEGORY_ID, 7);
        mapOfCategoryIdToResources.put(VALID_CATEGORY_ID, listOfUserRecommendedResource);
        listOfOneCategoryThatHasResources.add(VALID_CATEGORY_ID);
        listOfCategoryIdsThatHasResources.add(VALID_CATEGORY_ID);
        mapOfUserInterestedCategoryWithInterestLevel.put(VALID_CATEGORY_ID, VALID_INTEREST_LEVEL);
        final List<UserRecommendedResource> anotherListOfRecommendResourceUser = createListOfRecommendedResourceForCategory(
                ANOTHER_VALID_CATEGORY_ID, 7);
        listOfCategoryIdsThatHasResources.add(ANOTHER_VALID_CATEGORY_ID);
        mapOfUserInterestedCategoryWithInterestLevel.put(ANOTHER_VALID_CATEGORY_ID, VALID_INTEREST_LEVEL);
        combinedResourcesListOfDifferentCategories.addAll(listOfUserRecommendedResource);
        combinedResourcesListOfDifferentCategories.addAll(anotherListOfRecommendResourceUser);
        combinedMapOfDifferentCategories.put(VALID_CATEGORY_ID, listOfUserRecommendedResource);
        combinedMapOfDifferentCategories.put(ANOTHER_VALID_CATEGORY_ID, anotherListOfRecommendResourceUser);
        categoryList = getTestCategoryList(5);
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when user id is empty. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdWhenUserIdEmpty() throws SQLException {
        expectedException.expect(IllegalArgumentException.class);
        userRecommendedResourceManager.getRecommendedResourcesByUserId(EMPTY_STRING, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when user id is null. Expects {@link IllegalArgumentException}.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdWhenUserIdNull() throws SQLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceManager.getRecommendedResourcesByUserId(null, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * to throw an {@link IllegalArgumentException} when minimumRatingRequired
     * is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommenedResourcesByUserIdWhenMinimumRatingRequiredIsNegative() throws SQLException {
        try {
            userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                    -(VALID_MINIMUM_RATING_REQUIRED) - 1, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * to function as expected when minimumRatingRequired is 0.
     */
    @Test
    public void testGetRecommenedResourcesByUserIdWhenMinimumRatingRequiredIsZero() throws SQLException, DAOException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(VALID_USER_ID, 0,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED)).thenReturn(Collections.<UserRecommendedResource>emptyList());
        assertEquals(Collections.<UserRecommendedResource>emptyList(), userRecommendedResourceManager
                .getRecommendedResourcesByUserId(VALID_USER_ID, 0, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED));
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * to throw an {@link IllegalArgumentException} when
     * minimumNumberOfRatingsRequired is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommenedResourcesByUserIdWhenMinimumNumberOfRatingsRequiredIsNegative() throws SQLException {
        try {
            userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                    -(VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED) - 1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * to function as expected when minimumNumberOfRatingsRequired is 0.
     */
    @Test
    public void testGetRecommenedResourcesByUserIdWhenMinimumNumberOfRatingsRequiredIsZero()
            throws SQLException, DAOException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(VALID_USER_ID,
                VALID_MINIMUM_RATING_REQUIRED, 0)).thenReturn(Collections.<UserRecommendedResource>emptyList());
        assertEquals(Collections.<UserRecommendedResource>emptyList(), userRecommendedResourceManager
                .getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED, 0));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when returned list from dao is empty. Returns an empty list to the
     * controller in order to notify the user.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdWhenDatabaseThrowsEmptyList() throws DAOException, SQLException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(VALID_USER_ID,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED))
                        .thenReturn(emptyListOfUserRecommendedResource);
        assertTrue(emptyListOfUserRecommendedResource
                .equals(userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED)));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when user id is valid.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdWhenDAOThrowsException() throws DAOException, SQLException {
        expectedException.expect(ManagerException.class);
        doThrow(DAOException.class).when(mockUserRecommendedResourceDAO).getRecommendedResourcesForTheUser(
                VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#createMapForUserInterestedCategoryInterestLevel(List)}
     * when the list is valid.
     */
    @Test
    public void testMapCreationForUserInterested() {
        final Map<Integer, Integer> expectedMap = new HashMap<>();
        expectedMap.put(VALID_CATEGORY_ID, VALID_INTEREST_LEVEL);
        final Map<Integer, Integer> actualMap = userRecommendedResourceManager
                .createMapForUserInterestedCategoryInterestLevel(listOfUserInterestedCategory);
        assertTrue(actualMap.equals(expectedMap));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#findNumberOfResourcesToDisplay(double, double, double)}
     * will return a valid integer.
     */
    @Test
    public void testNumberOfResourcesToDisplay() {
        final int numberOfResourcesToDisplay = userRecommendedResourceManager.findNumberOfResourcesToDisplay(
                SUM_OF_INTEREST_LEVEL, MAX_COUNT_OF_RESOURCES_TO_RECOMMEND, VALID_INTEREST_LEVEL);
        assertEquals(NUM_OF_RESOURCE_FOR_EACH_CATEGORY, numberOfResourcesToDisplay);
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByCategory(List)}
     * so that the returned map from list of recommended resources by category
     * as key equals to the expected map when there is only one category.
     */
    @Test
    public void testCreateMapFromListOfRecommendedResourcesByCategoryAsKey() {
        assertTrue(mapOfCategoryIdToResources
                .equals(userRecommendedResourceManager.getRecommendedResourcesByCategory(listOfUserRecommendedResource)));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByCategory(List)}
     * so that the returned map from list of recommended resources by category
     * as key equals to the expected map when there are multiple categories.
     */
    @Test
    public void testCreateMapFromListOfRecommendedResourcesForMultipleCategories() {
        assertTrue(mapOfCategoryIdToResources
                .equals(userRecommendedResourceManager.getRecommendedResourcesByCategory(listOfUserRecommendedResource)));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#findTotalCountOfInterestLevel(Map, Map)}
     * for the list of category ids that have resources and the map of user
     * interested category with interest level which should equal to the sum of
     * interest levels for two valid categories, difference should be less than
     * delta.
     */
    @Test
    public void testTotalCountOfInterestLevel() {
        assertEquals(SUM_OF_INTEREST_LEVEL, userRecommendedResourceManager.findTotalCountOfInterestLevel(
                combinedMapOfDifferentCategories, mapOfUserInterestedCategoryWithInterestLevel), DELTA);
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#findTheBestTenResourcesAccordingToWeightOfInterestLevel(Map, Map, double)}
     * in order to verify that the elements of returned list are the values from
     * the returned recommended list from database.
     */
    @Test
    public void verifyTopTenResourcesWithOnlyOneCategory() {
        final List<UserRecommendedResource> bestTenRecommendedResources = userRecommendedResourceManager
                .findTheBestTenResourcesAccordingToWeightOfInterestLevel(mapOfCategoryIdToResources,
                        mapOfUserInterestedCategoryWithInterestLevel, VALID_INTEREST_LEVEL);
        assertTrue(listOfUserRecommendedResource.containsAll(bestTenRecommendedResources));
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#findTheBestTenResourcesAccordingToWeightOfInterestLevel(Map, Map, double)}
     * in order to verify that no more than ten elements are returned when more
     * than ten elements are passed in.
     */
    @Test
    public void verifyTopTenResourcesWithMoreThanTenResourcesReturnsExactlyTenResources() {
        final List<UserRecommendedResource> bestTenRecommendedResources = userRecommendedResourceManager
                .findTheBestTenResourcesAccordingToWeightOfInterestLevel(combinedMapOfDifferentCategories,
                        mapOfUserInterestedCategoryWithInterestLevel, SUM_OF_INTEREST_LEVEL);
        assertEquals(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND, bestTenRecommendedResources.size());
    }

    /**
     * Test
     * {@link UserRecommendedResourceManager#findTopTenResources(List, Map, String)}
     * when the userInterestedCategoryManager has a valid list of results.
     */
    @Test
    public void verifyTopTenResourcesWithMultipleCategoriesContainsResultsFromRecommendedList() {
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        final List<UserRecommendedResource> bestRecommendedResources = userRecommendedResourceManager
                .findTopTenResources(combinedResourcesListOfDifferentCategories, mapOfCategoryIdToResources,
                        VALID_USER_ID);
        assertTrue(combinedResourcesListOfDifferentCategories.containsAll(bestRecommendedResources));
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * with a null user ID.
     *
     * Expects a {@link IllegalArgumentException} with appropriate messages.
     */
    @Test
    public void testGetRecommendedResourcesByCategoryNullUserID() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(null, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * with a null category list. Expects a {@link IllegalArgumentException}
     * with appropriate messages.
     */
    @Test
    public void testGetRecommendedResourcesByCategoryNullCategoryList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_LIST_NULL);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, null,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * with an empty user ID.
     *
     * Expects a {@link IllegalArgumentException} with appropriate messages.
     */
    @Test
    public void testGetRecommendedResourcesByCategoryEmptyUserID() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(EMPTY_STRING, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * with a blank user ID.
     *
     * Expects a {@link IllegalArgumentException} with appropriate messages.
     */
    @Test
    public void testGetRecommendedResourcesByCategoryBlankUserID() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_ERROR_MESSAGE);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(BLANK_STRING, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * with an empty list.
     *
     * Expects a {@link IllegalArgumentException} with appropriate messages.
     */
    @Test
    public void testGetRecommendedResourcesByCategoryEmptyCategoryList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Constants.CATEGORY_LIST_EMPTY);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                Collections.<Category>emptyList(), VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * to throw an {@link IllegalArgumentException} when minimumRatingRequired
     * is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedResourcesByUserIdAndCategoriesWhenMinimumRatingRequiredIsNegative() {
        try {
            userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                    -(VALID_MINIMUM_RATING_REQUIRED) - 1, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_MINIMUM_RATING_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * to function as expected when minimumRatingRequired is 0.
     */
    @Test
    public void testGetRecommenededResourcesByUserIdAndCategoriesWhenMinimumRatingRequiredIsZero() throws DAOException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                0, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED))
                        .thenReturn(Collections.<UserRecommendedResource>emptyList());
        assertEquals(Collections.<UserRecommendedResource>emptyList(),
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        0, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED));
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * to throw an {@link IllegalArgumentException} when
     * minimumNumberOfRatingsRequired is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedResourcesByUserIdAndCategoriesWhenMinimumNumberOfRatingsRequiredIsNegative() {
        try {
            userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                    VALID_MINIMUM_RATING_REQUIRED, -(VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED) - 1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_NUMBER_OF_RATINGS_REQUIRED_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * to function as expected when minimumNumberOfRatingsRequired is 0.
     */
    @Test
    public void testGetRecommenededResourcesByUserIdAndCategoriesWhenMinimumNumberOfRatingsRequiredIsZero()
            throws DAOException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, 0)).thenReturn(Collections.<UserRecommendedResource>emptyList());
        assertEquals(Collections.<UserRecommendedResource>emptyList(),
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, 0));
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when the DAO returns an empty list. Expects an empty list to be returned
     * by the method.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdAndCategoriesDBReturnsEmptyList() throws DAOException {
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED))
                        .thenReturn(emptyListOfUserRecommendedResource);
        assertTrue(emptyListOfUserRecommendedResource
                .equals(userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                        categoryList, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED)));
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when the DAO returns a list of size 10. Expects the same list to be
     * returned.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdAndCategoriesDBReturnsListSize10() throws DAOException {
        listOfUserRecommendedResource = createListOfRecommendedResourceForCategory(VALID_CATEGORY_ID, 10);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        assertEquals(listOfUserRecommendedResource.size(),
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size());
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when the DAO returns a list of size 9.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdAndCategoriesDBReturnsListSize9() throws DAOException {
        listOfUserRecommendedResource = createListOfRecommendedResourceForCategory(VALID_CATEGORY_ID, 9);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        assertEquals(listOfUserRecommendedResource,
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED));
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when the DAO returns a list of size 11. Asserts that the returned list
     * contains at most 10 resources.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdAndCategoriesDBReturnsListSize11() throws DAOException {
        listOfUserRecommendedResource = createListOfRecommendedResourceForCategory(VALID_CATEGORY_ID, 11);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(VALID_USER_ID))
                .thenReturn(listOfUserInterestedCategory);
        assertEquals(10, userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                categoryList, VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size());
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when the DAO returns an list of size 10. Expects the same list to be
     * returned.
     */
    @Test
    public void testGetRecommendedResourcesByUserIdAndCategoriesDAOThrowsException() throws DAOException {
        expectedException.expect(ManagerException.class);
        expectedException.expectMessage(Constants.ERROR_RETRIEVING_RECOMMENDED_RESOURCES);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenThrow(daoException);
        userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1 categories that
     * the user has interest level 5 in, and there is at least one resource for
     * each category.
     */
    @Test
    public void testGetResourcesMaxResourceCountPlusOneCategoriesInterestLevel5() throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(),
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND categories that the
     * user has interest level 5 in, and there is at least one resource for each
     * category.
     */
    @Test
    public void testGetResourcesMaxResourceCountCategoriesInterestLevel5() throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(),
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1 categories that
     * the user has interest level 5 in, and there is at least one resource for
     * each category.
     */
    @Test
    public void testGetResourcesMaxResourceCountMinusOneCategoriesInterestLevel5() throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(
                userRecommendedResourceManager.getRecommendedResourcesByUserId(VALID_USER_ID,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(),
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1 categories that
     * the user has interest level 5 in, and there is at least one resource for
     * each category, and the user wishes to retrieve results from all the
     * specified categories.
     */
    @Test
    public void testGetResourcesByCategoriesMaxResourceCountPlusOneCategoriesInterestLevel5()
            throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                getTestCategoryList(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1), VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(), MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND categories that the
     * user has interest level 5 in, and there is at least one resource for each
     * category, and the user wishes to retrieve results from all the specified
     * categories.
     */
    @Test
    public void testGetResourcesByCategoriesMaxResourceCountCategoriesInterestLevel5()
            throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                getTestCategoryList(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND), VALID_MINIMUM_RATING_REQUIRED,
                VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(), MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when there are MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1 categories that
     * the user has interest level 5 in, and there is at least one resource for
     * each category, and the user wishes to retrieve results from all the
     * specified categories.
     */
    @Test
    public void testGetResourcesByCategoriesMaxResourceCountMinusOneCategoriesInterestLevel5()
            throws DAOException, SQLException {
        setupMutlipleCategoriesInterestLevel5EachWithResource(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUserInCategories(anyString(), any(List.class),
                any(Double.class), any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString())).thenReturn(listOfUserInterestedCategory);
        assertEquals(
                userRecommendedResourceManager.getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID,
                        getTestCategoryList(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1), VALID_MINIMUM_RATING_REQUIRED,
                        VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED).size(),
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1 resources are returned to
     * ensure the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByUserIdStaysSortedSizeMaxResourceCountPlusOne() throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                        VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND resources are returned to ensure
     * the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByUserIdStaysSortedSizeMaxResourceCount() throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                        VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserId(String, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1 resources are returned to
     * ensure the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByUserIdStaysSortedSizeMaxResourceCountMinusOne() throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserId(VALID_USER_ID, VALID_MINIMUM_RATING_REQUIRED,
                        VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1 resources are returned to
     * ensure the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByCategoriesStaysSortedSizeMaxResourceCountPlusOne() throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND + 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND resources are returned to ensure
     * the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByCategoriesStaysSortedSizeMaxResourceCount() throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(MAX_COUNT_OF_RESOURCES_TO_RECOMMEND);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Tests
     * {@link UserRecommendedResourceManager#getRecommendedResourcesByUserIdAndCategories(String, List, double, int)}
     * when MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1 resources are returned to
     * ensure the returned resources are sorted by interest level.
     */
    @Test
    public void testGetResourcesByCategoriesStaysSortedSizeMaxResourceCountMinusOne()
            throws DAOException, SQLException {
        listOfUserRecommendedResource = getMixedInterestRecommendedResourcesList(
                MAX_COUNT_OF_RESOURCES_TO_RECOMMEND - 1);
        when(mockUserRecommendedResourceDAO.getRecommendedResourcesForTheUser(anyString(), any(Double.class),
                any(Integer.class))).thenReturn(listOfUserRecommendedResource);
        when(userInterestedCategoryManager.getUserInterestedCategoriesById(anyString()))
                .thenReturn(getUserInterestedCategoryList());
        final List<UserRecommendedResource> recommendedResources = userRecommendedResourceManager
                .getRecommendedResourcesByUserIdAndCategories(VALID_USER_ID, categoryList,
                        VALID_MINIMUM_RATING_REQUIRED, VALID_MINIMUM_NUMBER_OF_RATINGS_REQUIRED);
        assertUserRecommendedResourcesAreSortedByInterestLevel(recommendedResources);
    }

    /**
     * Given a list of {@link UserRecommendedResource} objects, ensures that the
     * resources are sorted by interest level in descending order.
     *
     * @param recommendedResources
     *            A list of user recommended resources to check for correct
     *            sorting.
     */
    private void assertUserRecommendedResourcesAreSortedByInterestLevel(
            final List<UserRecommendedResource> recommendedResources) {
        if (recommendedResources.size() > 1) {
            for (int i = 1; i < recommendedResources.size(); i++) {
                assertTrue(recommendedResources.get(i - 1).getInterestLevel() >= recommendedResources.get(i)
                        .getInterestLevel());
            }
        }
    }

    /**
     * Creates a list of {@link UserRecommendedResource} of the specified size.
     * Creates the list so that it is nearly evenly split among resources of
     * interest levels 3, 4, and 5. To simulate the resources returned by the
     * UserRecommendedResourceDAO, the resources are in descending order by the
     * resource's interest level.
     *
     * @param listSize
     *            An integer. This represents the number of resources to return.
     *            Cannot be negative.
     * @return A list of {@link UserRecommendedResource} of size 'listSize'
     *         containing a variety of resources split amongst interest levels
     *         3, 4, and 5.
     * @throws IllegalArgumentException
     *             when resourcesPerInterestLevel is negative.
     */
    private List<UserRecommendedResource> getMixedInterestRecommendedResourcesList(final int listSize) {
        final List<UserRecommendedResource> userRecommendedResources = new ArrayList<>(listSize);
        int resourceID = 1;
        for (int i = 5; i >= 3; i--) {
            final Category category = new Category(i, VALID_CATEGORY_NAME + i, VALID_CATEGORY_DESC + i);
            for (int j = 0; j < Math.ceil(listSize / 3.0) && resourceID <= listSize; j++) {
                final Resource resource = new Resource(resourceID++, STATIC_URL, VALID_RESOURCE_DESC + resourceID);
                userRecommendedResources
                        .add(new UserRecommendedResource(resource, category, VALID_DIFFICULTY_LEVEL, i));
            }
        }
        return userRecommendedResources;
    }

    /**
     * Gets a list of 3 user interested categories. The list contains one user
     * interested category for each interest level of at least 3.
     *
     * @return a list of {@link UserInterestedCategory} of size 3.
     */
    private List<UserInterestedCategory> getUserInterestedCategoryList() {
        final List<UserInterestedCategory> userInterestedCategories = new ArrayList<>(3);
        for (int i = 3; i <= 5; i++) {
            final Category category = new Category(i, VALID_CATEGORY_NAME + i, VALID_CATEGORY_DESC + i);
            userInterestedCategories.add(new UserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, i));
        }
        return userInterestedCategories;
    }

    /**
     * Given the value of 'categoryCount', creates a list of that many user
     * interested categories where the user has interest level 5. Also creates a
     * list of user recommended resources such that each user interested
     * category has 1 recommended resource.
     *
     * @param categoryCount
     *            The size of the user interested category list and user
     *            recommended resource list to be created. Cannot be negative.
     * @throws IllegalArgumentException
     *             When 'categoryCount' is negative.
     */
    private void setupMutlipleCategoriesInterestLevel5EachWithResource(final int categoryCount) {
        listOfUserRecommendedResource = new ArrayList<>(categoryCount);
        listOfUserInterestedCategory = new ArrayList<>(categoryCount);
        for (int i = 1; i <= categoryCount; i++) {
            final Category category = new Category(i, VALID_CATEGORY_NAME + i, VALID_CATEGORY_DESC + i);
            final Resource resource = new Resource(i, STATIC_URL, VALID_RESOURCE_DESC + i);
            listOfUserInterestedCategory
                    .add(new UserInterestedCategory(VALID_USER_ID, category, VALID_SKILL_LEVEL, VALID_INTEREST_LEVEL));
            listOfUserRecommendedResource
                    .add(new UserRecommendedResource(resource, category, VALID_DIFFICULTY_LEVEL, VALID_INTEREST_LEVEL));
        }
    }

    /**
     * Helper method to initialize a list of {@link UserRecommendedResource} .
     */
    private List<UserRecommendedResource> createListOfRecommendedResourceForCategory(final int categoryId,
            final int size) {
        final List<UserRecommendedResource> recommendedResources = new ArrayList<UserRecommendedResource>(size);
        final Category category = new Category(categoryId, VALID_CATEGORY_NAME, VALID_CATEGORY_DESC);
        for (int i = 0; i < size; i++) {
            final Resource tempResource = new Resource(VALID_RESOURCE_ID + i, STATIC_URL, VALID_RESOURCE_DESC + i);
            recommendedResources.add(
                    new UserRecommendedResource(tempResource, category, VALID_DIFFICULTY_LEVEL, VALID_INTEREST_LEVEL));
        }
        return recommendedResources;
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
