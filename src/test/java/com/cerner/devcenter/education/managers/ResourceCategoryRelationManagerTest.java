package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAO;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This class is used to test {@link ResourceCategoryRelationManager} class.
 *
 * @author Abhi Purella (AP045635)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
public class ResourceCategoryRelationManagerTest {

    @InjectMocks
    private ResourceCategoryRelationManager resourceCategoryRelationManager;
    @Mock
    private ResourceCategoryRelationDAO mockResourceCategoryRelationDAO;

    private static final int VALID_CATEGORY_ID = 5;
    private static final int VALID_RESOURCE_ID = 3;
    private static final int VALID_DIFFICULTY_LEVEL = 3;
    private static final int RESOURCE_LIMIT = 10;
    private static final int PAGE_NUMBER = 10;
    private static final String VALID_RESOURCE_NAME = "resource name";
    private static final String VALID_RESOURCE_DESCRIPTION = "resource description";
    private static final String VALID_CATEGORY_NAME = "category name";
    private static final String VALID_CATEGORY_DESCRIPTION = "category description";
    private static final int VALID_RESOURCE_TYPE_ID = 5;
    private static final String VALID_RESOURCE_TYPE_NAME = "Ebook";
    private static final String VALID_RESOURCE_OWNER = "Owner";
    private static final double VALID_AVERAGE_RATING = 2.0;
    private static final int NEGATIVE_ID = -4;

    private static final String GET_ALL_RESOURCES_ERROR_MESSAGE = "Error retrieving all the resources";
    private static final String SEARCH_ERROR_MESSAGE = "Error searching for resource category relation by category name and difficulty level";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can't be blank/empty/null";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";

    private static URL staticURL;

    private List<ResourceCategoryRelation> listOfResourceCategoryRelation;
    private List<ResourceCategoryRelation> emptyListOfResourceCategoryRelation = Collections.emptyList();;
    private ResourceCategoryRelation resourceCategory;
    private ResourceType resourceType;

    @Before
    public void setup() throws DAOException, MalformedURLException {
        MockitoAnnotations.initMocks(this);
        staticURL = new URL("http://www.testURL.com");
        resourceType = new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME);
        resourceCategory = new ResourceCategoryRelation(
                VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME,
                staticURL,
                resourceType,
                VALID_DIFFICULTY_LEVEL,
                VALID_CATEGORY_ID,
                VALID_CATEGORY_NAME,
                VALID_RESOURCE_DESCRIPTION,
                VALID_CATEGORY_DESCRIPTION,
                VALID_AVERAGE_RATING,
                VALID_RESOURCE_OWNER);
        listOfResourceCategoryRelation = new ArrayList<ResourceCategoryRelation>();
        listOfResourceCategoryRelation.add(resourceCategory);
        when(
                mockResourceCategoryRelationDAO
                        .getResourcesAndDifficultyLevelByCategoryId(VALID_CATEGORY_ID, RESOURCE_LIMIT, PAGE_NUMBER))
                                .thenReturn(listOfResourceCategoryRelation);
        when(
                mockResourceCategoryRelationDAO.getResourcesByCategoryIdAndTypeIdWithPagination(
                        VALID_CATEGORY_ID,
                        VALID_RESOURCE_TYPE_ID,
                        RESOURCE_LIMIT,
                        PAGE_NUMBER)).thenReturn(listOfResourceCategoryRelation);
        when(mockResourceCategoryRelationDAO.getAllResourcesAndAverageRatings())
                .thenReturn(listOfResourceCategoryRelation);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesAndDifficultyLevelByCategoryIdWithPagination(int, int, int)}
     * functionality, expects {@link IllegalArgumentException} when categoryId
     * is negative
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesAndDifficultyByCategoryIdWhenIdIsNegative() {
        resourceCategoryRelationManager
                .getResourcesAndDifficultyLevelByCategoryIdWithPagination(-1, RESOURCE_LIMIT, PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesAndDifficultyLevelByCategoryIdWithPagination(int, int, int)}
     * functionality, expects {@link ManagerException} when
     * {@link ResourceCategoryRelationManager#getResourcesAndDifficultyLevelByCategoryIdWithPagination(int, int, int)}
     * throws {@link DAOException}
     *
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testGetResourcesAndDifficultyByCategoryIdThrowsManagerException() throws DAOException {
        doThrow(DAOException.class).when(mockResourceCategoryRelationDAO).getResourcesAndDifficultyLevelByCategoryId(
                VALID_CATEGORY_ID,
                RESOURCE_LIMIT,
                PAGE_NUMBER);
        resourceCategoryRelationManager
                .getResourcesAndDifficultyLevelByCategoryIdWithPagination(VALID_CATEGORY_ID, RESOURCE_LIMIT, 2);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesAndDifficultyLevelByCategoryIdWithPagination(int, int, int)}
     * functionality, expects {@link IllegalArgumentException} when categoryId
     * is zero
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesAndDifficultyByCategoryIdWhencategoryIdIsZero() {
        resourceCategoryRelationManager
                .getResourcesAndDifficultyLevelByCategoryIdWithPagination(0, RESOURCE_LIMIT, PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesAndDifficultyLevelByCategoryIdWithPagination(int, int, int)}
     * functionality with valid categoryId.
     */
    @Test
    public void testGetResourcesAndDifficultyByCategoryIdValid() {
        final List<ResourceCategoryRelation> newListOfResourceCategoryRelation = resourceCategoryRelationManager
                .getResourcesAndDifficultyLevelByCategoryIdWithPagination(VALID_CATEGORY_ID, RESOURCE_LIMIT, 2);
        assertEquals(staticURL, newListOfResourceCategoryRelation.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResourceCategoryRelation.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResourceCategoryRelation.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getResourceDescription());
        assertEquals(
                VALID_RESOURCE_TYPE_ID,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceTypeId());
        assertEquals(
                VALID_RESOURCE_TYPE_NAME,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceType());
        assertEquals(VALID_CATEGORY_ID, newListOfResourceCategoryRelation.get(0).getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newListOfResourceCategoryRelation.get(0).getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfResourceCategoryRelation.get(0).getDifficultyLevel());
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when category id is negative. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenCategoryIdIsNegative() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                NEGATIVE_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when resource type id is negative. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenTypeIdIsNegative() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                NEGATIVE_ID,
                RESOURCE_LIMIT,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when resources per page is negative. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenResourceLimitIsNegative() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                NEGATIVE_ID,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when page number is negative. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenPageNumberIsNegative() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                NEGATIVE_ID);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when category id is zero. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenCategoryIdIsZero() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                0,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when resource type id is zero. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenTypeIdIsZero() {
        resourceCategoryRelationManager
                .getResourcesByCategoryIdAndTypeIdWithPagination(VALID_CATEGORY_ID, 0, RESOURCE_LIMIT, PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when resources per page is zero. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenResourceLimitIsZero() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                0,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when page number is zero. Expects
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdAndTypeIdWhenPageNumberIsZero() {
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                0);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality when
     * {@link ResourceCategoryRelationDAO#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * throws {@link DAOException}. Expects {@link ManagerException}
     *
     * @throws DAOException
     */
    @Test(expected = ManagerException.class)
    public void testgetResourcesByCategoryIdWithPaginationForManagerException() throws DAOException {
        when(
                mockResourceCategoryRelationDAO
                        .getResourcesByCategoryIdAndTypeIdWithPagination(anyInt(), anyInt(), anyInt(), anyInt()))
                                .thenThrow(new DAOException());
        resourceCategoryRelationManager.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                PAGE_NUMBER);
    }

    /**
     * Tests
     * {@link ResourceCategoryRelationManager#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality with all valid inputs.
     */
    @Test
    public void testGetResourcesByCategoryIdAndTypeIdValid() {
        final List<ResourceCategoryRelation> newListOfResourceCategoryRelation = resourceCategoryRelationManager
                .getResourcesByCategoryIdAndTypeIdWithPagination(
                        VALID_CATEGORY_ID,
                        VALID_RESOURCE_TYPE_ID,
                        RESOURCE_LIMIT,
                        2);
        assertEquals(staticURL, newListOfResourceCategoryRelation.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResourceCategoryRelation.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResourceCategoryRelation.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getResourceDescription());
        assertEquals(
                VALID_RESOURCE_TYPE_ID,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceTypeId());
        assertEquals(
                VALID_RESOURCE_TYPE_NAME,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceType());
        assertEquals(VALID_CATEGORY_ID, newListOfResourceCategoryRelation.get(0).getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newListOfResourceCategoryRelation.get(0).getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfResourceCategoryRelation.get(0).getDifficultyLevel());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#getResourcesForAllCategories()}to
     * throw {@link ManagerException} when there's an error in retrieving the
     * {@link ResourceCategoryRelation ResourceCategoryRelations} from the
     * database.
     */
    @Test(expected = ManagerException.class)
    public void testGetResourcesForAllCategoriesThrowsManagerException() throws DAOException {
        when(mockResourceCategoryRelationDAO.getAllResourcesAndAverageRatings()).thenThrow(new DAOException());
        try {
            resourceCategoryRelationManager.getResourcesForAllCategories();
        } catch (final ManagerException e) {
            assertEquals(GET_ALL_RESOURCES_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#getResourcesForAllCategories()} to
     * return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when there are no
     * {@link ResourceCategoryRelation ResourceCategoryRelations} in the
     * database.
     */
    @Test
    public void testGetResourcesForAllCategoriesReturnsEmpty() throws DAOException {
        when(mockResourceCategoryRelationDAO.getAllResourcesAndAverageRatings())
                .thenReturn(new ArrayList<ResourceCategoryRelation>());
        assertEquals(0, resourceCategoryRelationManager.getResourcesForAllCategories().size());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#getResourcesForAllCategories()}to
     * retrieve a valid {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} from the database.
     */
    @Test
    public void testGetResourcesForAllCategoriesValid() throws DAOException {
        final List<ResourceCategoryRelation> newListOfResourceCategoryRelation = resourceCategoryRelationManager
                .getResourcesForAllCategories();
        assertEquals(staticURL, newListOfResourceCategoryRelation.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResourceCategoryRelation.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResourceCategoryRelation.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getResourceDescription());
        assertEquals(
                VALID_RESOURCE_TYPE_ID,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceTypeId());
        assertEquals(
                VALID_RESOURCE_TYPE_NAME,
                newListOfResourceCategoryRelation.get(0).getResourceType().getResourceType());
        assertEquals(VALID_CATEGORY_ID, newListOfResourceCategoryRelation.get(0).getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newListOfResourceCategoryRelation.get(0).getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfResourceCategoryRelation.get(0).getDifficultyLevel());
        assertEquals(VALID_AVERAGE_RATING, newListOfResourceCategoryRelation.get(0).getAverageRating(), 0.01);
        assertEquals(VALID_RESOURCE_OWNER, newListOfResourceCategoryRelation.get(0).getResourceOwner());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to execute properly when valid inputs have been passed.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelValid() throws DAOException {
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenReturn(listOfResourceCategoryRelation);
        assertEquals(
                listOfResourceCategoryRelation,
                resourceCategoryRelationManager
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw {@link ManagerException} when there's an error in retrieving the
     * {@link ResourceCategoryRelation ResourceCategoryRelations} from the
     * database.
     */
    @Test(expected = ManagerException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelException() throws DAOException {
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenThrow(DAOException.class);
        try {
            assertEquals(
                    emptyListOfResourceCategoryRelation,
                    resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(
                            VALID_CATEGORY_NAME,
                            VALID_DIFFICULTY_LEVEL));
        } catch (final ManagerException managerException) {
            assertEquals(SEARCH_ERROR_MESSAGE, managerException.getMessage());
            throw managerException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a <code>null</code>
     * category name has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNullCategoryName() throws DAOException {
        try {
            resourceCategoryRelationManager
                    .searchResourcesByCategoryNameAndDifficultyLevel(null, VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a blank category name
     * has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelBlankCategoryName() throws DAOException {
        try {
            resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel("", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a white space category
     * name has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelWhiteSpaceCategoryName() throws DAOException {
        try {
            resourceCategoryRelationManager
                    .searchResourcesByCategoryNameAndDifficultyLevel("    ", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a negative difficulty
     * level has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNegativeDifficultyLevel() throws DAOException {
        try {
            resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, -1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when zero(boundary
     * condition) has been passed for difficulty level.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelZeroDifficultyLevel() throws DAOException {
        try {
            resourceCategoryRelationManager.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, 0);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when <code>null<code> is returned from the
     * database.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelDAOReturnsNull() throws DAOException {
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenReturn(null);
        assertEquals(
                emptyListOfResourceCategoryRelation,
                resourceCategoryRelationManager
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when <code>null<code> is present in the
     * {@link List} (size 1) returned from the database.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNullInListSizeOne() throws DAOException {
        final List<ResourceCategoryRelation> resourceCategoryRelationTest = new ArrayList<>();
        resourceCategoryRelationTest.add(null);
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenReturn(resourceCategoryRelationTest);
        assertEquals(
                emptyListOfResourceCategoryRelation,
                resourceCategoryRelationManager
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when <code>null<code> is present in the
     * {@link List} (size 2) returned from the database.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNullInListSizeTwo() throws DAOException {
        final List<ResourceCategoryRelation> resourceCategoryRelationTest = new ArrayList<>();
        resourceCategoryRelationTest.add(null);
        resourceCategoryRelationTest.add(resourceCategory);
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenReturn(resourceCategoryRelationTest);
        assertEquals(
                listOfResourceCategoryRelation,
                resourceCategoryRelationManager
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationManager#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when there are no
     * {@link ResourceCategoryRelation ResourceCategoryRelations} in the
     * database with the specified category name and difficulty level.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelReturnEmpty() throws DAOException {
        when(
                mockResourceCategoryRelationDAO
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL))
                                .thenReturn(emptyListOfResourceCategoryRelation);
        assertEquals(
                emptyListOfResourceCategoryRelation,
                resourceCategoryRelationManager
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }
}