package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.ResourceDAOImpl.ResourceRowMapper;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAOImpl.ResourceCategoryRelationAndAverageRatingRowMapper;
import com.cerner.devcenter.education.admin.ResourceCategoryRelationDAOImpl.ResourceCategoryRelationRowMapper;
import com.cerner.devcenter.education.exceptions.ResourceIdNotFoundException;
import com.cerner.devcenter.education.exceptions.CategoryIdNotFoundException;
import com.cerner.devcenter.education.models.Category;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceCategoryRelation;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This class exists to test the {@link ResourceCategoryRelationDAOImpl} class.
 *
 * @author Abhi Purella (AP045635)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceCategoryRelationDAOImplTest {

    private static final int VALID_RESOURCE_ID = 3;
    private static final int RESOURCE_LIMIT = 10;
    private static final int OFFSET = 10;
    private static final int VALID_DIFFICULTY_LEVEL = 3;
    private static final int NEGATIVE_ID = -3;
    private static final int GET_ID_RESOURCE = 1;
    private static final int VALID_CATEGORY_ID = 1;
    private static final int VALID_RESOURCE_TYPE_ID = 4;
    private static final double VALID_AVERAGE_RATING = 2;
    private static final String AVERAGE_RATING = "avg_rating";
    private static final String RESOURCE_OWNER = "resource_owner";
    private static final String VALID_RESOURCE_NAME = "resource name";
    private static final String VALID_RESOURCE_DESCRIPTION = "resource description";
    private static final String VALID_CATEGORY_NAME = "category name";
    private static final String VALID_CATEGORY_DESCRIPTION = "category description";
    private static final String VALID_RESOURCE_TYPE_NAME = "YouTube";
    private static final String VALID_RESOURCE_OWNER = "Owner";

    private static final String INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY = "INSERT into category_resource_reltn (category_id, resource_id, difficulty_level) VALUES(?,?,?)";

    private static final String RETRIVING_RESOURCES_ERROR_MESSAGE = "Error while retrieving all resources and their corresponding average ratings for a particular category";
    private static final String INVALID_CATEGORY_NAME_ERROR_MESSAGE = "Category Name can't be blank/empty/null";
    private static final String INVALID_RESOURCE_LEVEL_ERROR_MESSAGE = "Resource Difficulty Level must be greater than 0";

    private static URL staticURL;

    @Mock
    private ResultSet resultSet;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResourceDAO resourceDAO;
    @Mock
    private CategoryDAO categoryDAO;
    @Mock
    private DataAccessException dataAccessEx;

    @InjectMocks
    private ResourceCategoryRelationDAOImpl resourceCategoryRelationDAOImpl;

    private ResourceCategoryRelationDAOImpl.ResourceCategoryRelationRowMapper resourceCategoryMapper;
    private ResourceCategoryRelationDAOImpl.ResourceCategoryRelationAndAverageRatingRowMapper allResourcesMapper;
    private ResourceCategoryRelation newResourceCategory;
    private List<ResourceCategoryRelation> newListOfResourceCategoryRelation;
    private List<ResourceCategoryRelation> emptyListOfResourceCategoryRelation;
    private ResourceCategoryRelation resourceCategory;
    private ResourceCategoryRelation resourceCategoryForMapping;
    private Resource resource;
    private ResourceType resourceType;
    private Category category;
    private URL url;

    @Before
    public void setUp() throws SQLException, DAOException, MalformedURLException {
        resourceCategoryMapper = new ResourceCategoryRelationRowMapper();
        allResourcesMapper = new ResourceCategoryRelationAndAverageRatingRowMapper();
        staticURL = new URL("http://www.testURL.com");
        when(resultSet.getInt("resource_id")).thenReturn(VALID_RESOURCE_ID);
        when(resultSet.getString("resource_name")).thenReturn(VALID_RESOURCE_NAME);
        when(resultSet.getString("resource_description")).thenReturn(VALID_RESOURCE_DESCRIPTION);
        when(resultSet.getString("link")).thenReturn(staticURL.toString());
        when(resultSet.getInt("type_id")).thenReturn(VALID_RESOURCE_TYPE_ID);
        when(resultSet.getString("resource_type_name")).thenReturn(VALID_RESOURCE_TYPE_NAME);
        when(resultSet.getInt("id")).thenReturn(VALID_CATEGORY_ID);
        when(resultSet.getInt("difficulty_level")).thenReturn(VALID_DIFFICULTY_LEVEL);
        when(resultSet.getString("category_name")).thenReturn(VALID_CATEGORY_NAME);
        when(resultSet.getString("category_description")).thenReturn(VALID_CATEGORY_DESCRIPTION);
        when(resultSet.getString(RESOURCE_OWNER)).thenReturn(VALID_RESOURCE_OWNER);
        when(resultSet.getDouble(AVERAGE_RATING)).thenReturn(VALID_AVERAGE_RATING);
        when(
                jdbcTemplate.update(
                        INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY,
                        new Object[] { VALID_CATEGORY_ID, GET_ID_RESOURCE, VALID_DIFFICULTY_LEVEL })).thenReturn(1);
        url = new URL("https://www.google.com");
        resource = new Resource(1, url, "This is a test resource");
        category = new Category(1, "Test", "This is a test category");
        resourceType = new ResourceType(VALID_RESOURCE_TYPE_ID, VALID_RESOURCE_TYPE_NAME);
        when(resourceDAO.getById(GET_ID_RESOURCE)).thenReturn(resource);
        when(categoryDAO.getById(VALID_CATEGORY_ID)).thenReturn(category);
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
        resourceCategoryForMapping = new ResourceCategoryRelation(
                GET_ID_RESOURCE,
                VALID_RESOURCE_NAME,
                staticURL,
                resourceType,
                VALID_DIFFICULTY_LEVEL,
                VALID_CATEGORY_ID,
                VALID_CATEGORY_NAME,
                VALID_RESOURCE_DESCRIPTION,
                VALID_CATEGORY_DESCRIPTION);
        newListOfResourceCategoryRelation = new ArrayList<ResourceCategoryRelation>();
        newListOfResourceCategoryRelation.add(resourceCategory);
        emptyListOfResourceCategoryRelation = Collections.emptyList();
    }

    @After
    public void tearDown() {
        resultSet = null;
        jdbcTemplate = null;
        dataAccessEx = null;
    }

    /**
     * Expects {@link ResourceCategoryRelationRowMapper#mapRow(ResultSet, int)}
     * to function properly with valid inputs.
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newResourceCategory = resourceCategoryMapper.mapRow(resultSet, 1);
        assertEquals(staticURL, newResourceCategory.getResourceLink());
        assertEquals(VALID_RESOURCE_ID, newResourceCategory.getResourceId());
        assertEquals(VALID_RESOURCE_NAME, newResourceCategory.getResourceName());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newResourceCategory.getResourceDescription());
        assertEquals(VALID_CATEGORY_ID, newResourceCategory.getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newResourceCategory.getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newResourceCategory.getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newResourceCategory.getDifficultyLevel());
        assertEquals(VALID_RESOURCE_TYPE_ID, newResourceCategory.getResourceType().getResourceTypeId());
        assertEquals(VALID_RESOURCE_TYPE_NAME, newResourceCategory.getResourceType().getResourceType());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationAndAverageRatingRowMapper#mapRow(ResultSet, int)}
     * to return valid result set.
     */
    @Test
    public void testAllResourcesAndAverageRatingsMapRowValidResultSet() throws SQLException {
        newResourceCategory = allResourcesMapper.mapRow(resultSet, 1);
        assertEquals(staticURL, newResourceCategory.getResourceLink());
        assertEquals(VALID_RESOURCE_ID, newResourceCategory.getResourceId());
        assertEquals(VALID_RESOURCE_NAME, newResourceCategory.getResourceName());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newResourceCategory.getResourceDescription());
        assertEquals(VALID_CATEGORY_ID, newResourceCategory.getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newResourceCategory.getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newResourceCategory.getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newResourceCategory.getDifficultyLevel());
        assertEquals(VALID_RESOURCE_TYPE_ID, newResourceCategory.getResourceType().getResourceTypeId());
        assertEquals(VALID_RESOURCE_TYPE_NAME, newResourceCategory.getResourceType().getResourceType());
        assertEquals(VALID_AVERAGE_RATING, newResourceCategory.getAverageRating(), 0.01);
        assertEquals(VALID_RESOURCE_OWNER, newResourceCategory.getResourceOwner());
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesAndDifficultyLevelByCategoryId(int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when
     * categoryId is zero
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesAndDifficultyLevelByCategoryIdWhenIdIsZero() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesAndDifficultyLevelByCategoryId(0, RESOURCE_LIMIT, OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesAndDifficultyLevelByCategoryId(int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when
     * categoryId is negative
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesAndDifficultyLevelByCategoryIdWhenIdIsNegative() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesAndDifficultyLevelByCategoryId(-1, RESOURCE_LIMIT, OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesAndDifficultyLevelByCategoryId(int, int, int)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     * throws {@link DataAccessException} .
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testGetResourcesAndDifficultyLevelByCategoryIdWhenJdbcTemplateThrowsDataAccessException()
            throws DAOException {
        when(
                jdbcTemplate
                        .query(anyString(), any(ResourceCategoryRelationRowMapper.class), anyInt(), anyInt(), anyInt()))
                                .thenThrow(dataAccessException);
        resourceCategoryRelationDAOImpl
                .getResourcesAndDifficultyLevelByCategoryId(VALID_CATEGORY_ID, RESOURCE_LIMIT, OFFSET);
    }

    /**
     * This function verifies
     * {@link ResourceCategoryRelationDAOImpl#getResourcesAndDifficultyLevelByCategoryId(int, int, int)}
     * functionality with valid input.
     *
     * @throws DAOException
     */
    @Test
    public void testGetResourcesAndDifficultyLevelByCategoryIdValid() throws DAOException {
        when(
                jdbcTemplate
                        .query(anyString(), any(ResourceCategoryRelationRowMapper.class), anyInt(), anyInt(), anyInt()))
                                .thenReturn(newListOfResourceCategoryRelation);
        newListOfResourceCategoryRelation = resourceCategoryRelationDAOImpl
                .getResourcesAndDifficultyLevelByCategoryId(VALID_CATEGORY_ID, RESOURCE_LIMIT, 0);
        assertEquals(staticURL, newListOfResourceCategoryRelation.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResourceCategoryRelation.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResourceCategoryRelation.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getResourceDescription());
        assertEquals(VALID_CATEGORY_ID, newListOfResourceCategoryRelation.get(0).getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newListOfResourceCategoryRelation.get(0).getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfResourceCategoryRelation.get(0).getDifficultyLevel());
    }

    /**
     * Tests that {@link JdbcTemplate#update(String, Object...)} is called
     * during a call to
     * {@link ResourceCategoryRelationDAOImpl#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     *
     * @throws DAOException
     * @throws ResourceIdNotFoundException
     * @throws CategoryIdNotFoundException
     */
    @Test
    public void testAddResourceCategoryRelationWithDifficultyLevelToDB()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        resourceCategoryRelationDAOImpl.addResourceCategoryRelationWithDifficultyLevel(resourceCategoryForMapping);
        verify(jdbcTemplate).update(
                INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY,
                new Object[] { VALID_CATEGORY_ID, GET_ID_RESOURCE, VALID_DIFFICULTY_LEVEL });
    }

    /**
     * This method tests the
     * {@link ResourceCategoryRelationDAOImpl#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     * functionality and expects a {@link NullPointerException} when the
     * {@link ResourceCategoryRelation} object is passed as null.
     *
     * @throws DAOException
     * @throws ResourceIdNotFoundException
     * @throws CategoryIdNotFoundException
     */
    @Test(expected = NullPointerException.class)
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceCategoryRelationIsNull()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        resourceCategoryRelationDAOImpl.addResourceCategoryRelationWithDifficultyLevel(null);
    }

    /**
     * This method tests the
     * {@link ResourceCategoryRelationDAOImpl#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     * functionality and expects a {@link ResourceIdNotFoundException} when the
     * {@link ResourceDAO#getById(int)} function return null instead of
     * {@link Resource}.
     *
     * @throws DAOException
     * @throws ResourceIdNotFoundException
     * @throws CategoryIdNotFoundException
     */
    @Test(expected = ResourceIdNotFoundException.class)
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenResourceDAOGetIdIsNull()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(resourceDAO.getById(GET_ID_RESOURCE)).thenReturn(null);
        resourceCategoryRelationDAOImpl.addResourceCategoryRelationWithDifficultyLevel(resourceCategoryForMapping);
    }

    /**
     * This method tests the
     * {@link ResourceCategoryRelationDAOImpl#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     * functionality and expects a {@link NullPointerException} when the
     * {@link CategoryDAO#getById(int)} function return null instead of
     * {@link Category}.
     *
     * @throws DAOException
     * @throws ResourceIdNotFoundException
     * @throws CategoryIdNotFoundException
     */
    @Test(expected = CategoryIdNotFoundException.class)
    public void testAddResourceCategoryRelationWithDifficultyLevelWhenCategoryDAOGetIdIdIsNull()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(categoryDAO.getById(VALID_CATEGORY_ID)).thenReturn(null);
        resourceCategoryRelationDAOImpl.addResourceCategoryRelationWithDifficultyLevel(resourceCategoryForMapping);
    }

    /**
     * This method tests the
     * {@link ResourceCategoryRelationDAOImpl#addResourceCategoryRelationWithDifficultyLevel(ResourceCategoryRelation)}
     * functionality and expecting a {@link DAOException} when the
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}
     *
     * @throws DAOException
     * @throws ResourceIdNotFoundException
     * @throws CategoryIdNotFoundException
     */
    @Test(expected = DAOException.class)
    public void testAaddResourceCategoryRelationWithDifficultyLevelThrowsDAOException()
            throws DAOException, ResourceIdNotFoundException, CategoryIdNotFoundException {
        when(
                jdbcTemplate.update(
                        INSERT_CATEGORY_RESOURCE_DIFFICULTY_QUERY,
                        VALID_CATEGORY_ID,
                        GET_ID_RESOURCE,
                        VALID_DIFFICULTY_LEVEL)).thenThrow(dataAccessEx);
        resourceCategoryRelationDAOImpl.addResourceCategoryRelationWithDifficultyLevel(resourceCategoryForMapping);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     * throws {@link DataAccessException}.
     *
     * @throws DAOException
     */
    @Test(expected = DAOException.class)
    public void testGetResourceByCategoryAndTypeIdWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceRowMapper.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenThrow(dataAccessException);
        resourceCategoryRelationDAOImpl.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when category
     * id passed in is 0.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeIdWithPaginationWhenCategoryIdIsZero() throws DAOException {
        resourceCategoryRelationDAOImpl
                .getResourcesByCategoryIdAndTypeIdWithPagination(0, VALID_RESOURCE_TYPE_ID, RESOURCE_LIMIT, OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when category
     * id is negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeIdWithPaginationWhenCategoryIdIsNegative() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesByCategoryIdAndTypeIdWithPagination(
                NEGATIVE_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when resource
     * type id passed in is 0.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeWhenTypeIdIsZero() throws DAOException {
        resourceCategoryRelationDAOImpl
                .getResourcesByCategoryIdAndTypeIdWithPagination(VALID_CATEGORY_ID, 0, RESOURCE_LIMIT, OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when resource
     * type id is negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeIdWithPaginationWhenTypeIdIsNegative() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                NEGATIVE_ID,
                RESOURCE_LIMIT,
                OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when resources
     * limit passed in is 0.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeWhenLimitIsZero() throws DAOException {
        resourceCategoryRelationDAOImpl
                .getResourcesByCategoryIdAndTypeIdWithPagination(VALID_CATEGORY_ID, VALID_RESOURCE_TYPE_ID, 0, OFFSET);
    }

    /**
     * This function tests
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality and expects {@link IllegalArgumentException} when resources
     * limit passed in is negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryAndTypeIdWithPaginationWhenLimitIsNegative() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                NEGATIVE_ID,
                OFFSET);
    }

    /**
     * Verifies that
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * expects {@link IllegalArgumentException} when offset is negative.
     *
     * @throws DAOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdWithPaginationWhenOffsetIsNegative() throws DAOException {
        resourceCategoryRelationDAOImpl.getResourcesByCategoryIdAndTypeIdWithPagination(
                VALID_CATEGORY_ID,
                VALID_RESOURCE_TYPE_ID,
                RESOURCE_LIMIT,
                NEGATIVE_ID);
    }

    /**
     * This function verifies
     * {@link ResourceCategoryRelationDAOImpl#getResourcesByCategoryIdAndTypeIdWithPagination(int, int, int, int)}
     * functionality with valid input.
     *
     * @throws DAOException
     */
    @Test
    public void testGetResourcesByCategoryIdAndTypeIdValid() throws DAOException {
        when(
                jdbcTemplate
                        .query(anyString(), any(ResourceCategoryRelationRowMapper.class), anyInt(), anyInt(), anyInt()))
                                .thenReturn(newListOfResourceCategoryRelation);
        newListOfResourceCategoryRelation = resourceCategoryRelationDAOImpl
                .getResourcesAndDifficultyLevelByCategoryId(VALID_CATEGORY_ID, RESOURCE_LIMIT, 0);
        assertEquals(staticURL, newListOfResourceCategoryRelation.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResourceCategoryRelation.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResourceCategoryRelation.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getResourceDescription());
        assertEquals(resourceType, newListOfResourceCategoryRelation.get(0).getResourceType());
        assertEquals(VALID_CATEGORY_ID, newListOfResourceCategoryRelation.get(0).getCategoryId());
        assertEquals(VALID_CATEGORY_NAME, newListOfResourceCategoryRelation.get(0).getCategoryName());
        assertEquals(VALID_CATEGORY_DESCRIPTION, newListOfResourceCategoryRelation.get(0).getCategoryDescription());
        assertEquals(VALID_DIFFICULTY_LEVEL, newListOfResourceCategoryRelation.get(0).getDifficultyLevel());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#getAllResourcesAndAverageRatings()}
     * to throw {@link DAOException} when there's an error in retrieving from
     * database.
     */
    @Test(expected = DAOException.class)
    public void testGetAllResourcesAndAverageRatingsThrowsException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceCategoryRelationAndAverageRatingRowMapper.class)))
                .thenThrow(dataAccessException);
        try {
            resourceCategoryRelationDAOImpl.getAllResourcesAndAverageRatings();
        } catch (final DAOException e) {
            assertEquals(RETRIVING_RESOURCES_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#getAllResourcesAndAverageRatings()}
     * to return an empty {@link List} when there are no
     * {@link ResourceCategoryRelation ResourceCategoryRelations} in the
     * database to be retrieved.
     */
    @Test
    public void testGetAllResourcesAndAverageRatingsEmpty() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceCategoryRelationAndAverageRatingRowMapper.class)))
                .thenReturn(new ArrayList<ResourceCategoryRelation>());
        assertEquals(0, resourceCategoryRelationDAOImpl.getAllResourcesAndAverageRatings().size());
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#getAllResourcesAndAverageRatings()}
     * to return a valid {@link List} of {@link ResourceCategoryRelation}.
     */
    @Test
    public void testGetAllResourcesAndAverageRatingsValid() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceCategoryRelationAndAverageRatingRowMapper.class)))
                .thenReturn(newListOfResourceCategoryRelation);
        final List<ResourceCategoryRelation> listOfResourceCategoryRelationTest = resourceCategoryRelationDAOImpl
                .getAllResourcesAndAverageRatings();
        assertEquals(newListOfResourceCategoryRelation, listOfResourceCategoryRelationTest);
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return a valid {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} with the specified category name and
     * difficulty level.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelValid() throws DAOException {
        when(
                jdbcTemplate.query(
                        anyString(),
                        any(ResourceCategoryRelationAndAverageRatingRowMapper.class),
                        anyString(),
                        anyInt())).thenReturn(newListOfResourceCategoryRelation);
        assertEquals(
                newListOfResourceCategoryRelation,
                resourceCategoryRelationDAOImpl
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw a {@link DAOException} when there's an error in retrieving from
     * the database.
     */
    @Test(expected = DAOException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelException() throws DAOException {
        when(
                jdbcTemplate.query(
                        anyString(),
                        any(ResourceCategoryRelationAndAverageRatingRowMapper.class),
                        anyString(),
                        anyInt())).thenThrow(dataAccessException);
        try {
            resourceCategoryRelationDAOImpl
                    .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL);
        } catch (final DAOException daoException) {
            assertEquals(RETRIVING_RESOURCES_ERROR_MESSAGE, daoException.getMessage());
            throw daoException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when <code>null</code>
     * category name has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNullCategoryName() throws DAOException {
        try {
            resourceCategoryRelationDAOImpl
                    .searchResourcesByCategoryNameAndDifficultyLevel(null, VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a blank category name
     * has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelBlankCategoryName() throws DAOException {
        try {
            resourceCategoryRelationDAOImpl.searchResourcesByCategoryNameAndDifficultyLevel("", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a white space has been
     * passed for category name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelWhiteSpaceCategoryName() throws DAOException {
        try {
            resourceCategoryRelationDAOImpl
                    .searchResourcesByCategoryNameAndDifficultyLevel("    ", VALID_DIFFICULTY_LEVEL);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_CATEGORY_NAME_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when a negative difficulty
     * level has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelNegativeDifficultyLevel() throws DAOException {
        try {
            resourceCategoryRelationDAOImpl.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, -1);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to throw an {@link IllegalArgumentException} when zero(boundary
     * condition) difficulty level has been passed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSearchResourcesByCategoryNameAndDifficultyLevelZeroDifficultyLevel() throws DAOException {
        try {
            resourceCategoryRelationDAOImpl.searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, 0);
        } catch (final IllegalArgumentException illegalArgumentException) {
            assertEquals(INVALID_RESOURCE_LEVEL_ERROR_MESSAGE, illegalArgumentException.getMessage());
            throw illegalArgumentException;
        }
    }

    /**
     * Expects
     * {@link ResourceCategoryRelationDAOImpl#searchResourcesByCategoryNameAndDifficultyLevel(String, int)}
     * to return an empty {@link List} of {@link ResourceCategoryRelation
     * ResourceCategoryRelations} when the are no
     * {@link ResourceCategoryRelation ResourceCategoryRelations} in the
     * database with the specified category name and difficulty level.
     */
    @Test
    public void testSearchResourcesByCategoryNameAndDifficultyLevelDAOEmtpy() throws DAOException {
        when(
                jdbcTemplate.query(
                        anyString(),
                        any(ResourceCategoryRelationAndAverageRatingRowMapper.class),
                        anyString(),
                        anyInt())).thenReturn(emptyListOfResourceCategoryRelation);
        assertEquals(
                emptyListOfResourceCategoryRelation,
                resourceCategoryRelationDAOImpl
                        .searchResourcesByCategoryNameAndDifficultyLevel(VALID_CATEGORY_NAME, VALID_DIFFICULTY_LEVEL));
    }
}