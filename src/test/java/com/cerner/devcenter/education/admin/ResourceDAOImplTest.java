package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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

import com.cerner.devcenter.education.admin.ResourceDAOImpl.ResourceRowMapper;
import com.cerner.devcenter.education.helpers.HttpURLValidator;
import com.cerner.devcenter.education.models.Resource;
import com.cerner.devcenter.education.models.ResourceStatus;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * This class exists to test the {@link ResourceDAOImpl} class.
 *
 * @author Piyush Bandil (PB042879)
 * @author James Kellerman (JK042311)
 * @author Sreelakshmi Chintha (SC043016)
 * @author Wuchen Wang (WW044343)
 * @author Jacob Zimmermann (JZ022690)
 * @author Mayur Rajendran (MT049536)
 * @author Vincent Dasari (VD049645)
 * @author Rishabh Bhojak (RB048032)
 * @author Santosh Kumar (SK051343)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDAOImplTest {

    private static final String GET_MAX_ID_QUERY = "SELECT MAX(resource_id) FROM resource";
    private static final String INSERT_RESOURCE = "INSERT INTO resource (description, name, link, type_id, resource_owner, status) VALUES(?,?,?,?,?,?::status)";
    private static final String INSERT_RESOURCE_QUERY = "INSERT INTO resource (description, name, link, type_id, resource_owner) VALUES(?,?,?,?,?)";
    private static final String RESOURCE_COUNT_QUERY = "SELECT count(*) FROM resource r INNER JOIN category_resource_reltn c on r.resource_id=c.resource_id WHERE c.category_id=?";
    private static final String GET_RESOURCE_DESCRIPTION_BY_ID_QUERY = "SELECT description FROM resource WHERE resource_id = ?";
    private static final String EDIT_RESOURCE_QUERY = "SELECT type_id FROM type WHERE type_name=?";

    private static final int VALID_DIFFICULTY_LEVEL = 3;
    private static final int VALID_RESOURCE_ID = 5;
    private static final int INVALID_RESOURCE_ID = 150;
    private static final String VALID_RESOURCE_DESCRIPTION = "resource description";
    private static final String VALID_RESOURCE_NAME = "resource name";
    private static final String VALID_RESOURCE_OWNER = "AB123456";
    private static final String VALID_RESOURCE_STATUS = ResourceStatus.Available.toString();
    private static final int ID_SMALLER_THAN_ZERO = -1;
    private static final String WRONG_URL = "wrongurl//.com";
    private static final int CATEGORY_ID = 5;
    private static final URL STATIC_URL;
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final int MAX_RESOURCE_NUMBER = 5;
    private static final int VALID_RESOURCE_TYPE_ID = 4;
    private static final String VALID_RESOURCE_TYPE_NAME = "EBook";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = " ";
    private static final ResourceType VALID_RESOURCE_TYPE = new ResourceType(
            VALID_RESOURCE_TYPE_ID,
            VALID_RESOURCE_TYPE_NAME);
    private static final String INVALID_RESOURCE_STATUS = "Removed";

    private static final String INVALID_RESOURCE_DESC_MESSAGE = "Resource description is invalid";
    private static final String INVALID_RESOURCE_NAME_MESSAGE = "Resource name is invalid";
    private static final String INVALID_RESOURCE_URL_MESSAGE = "Resource URL is invalid";
    private static final String NULL_RESOURCE_TYPE_MESSAGE = "Resource Type cannot be null";
    private static final String ERROR_ADDING_RESOURCE = "Error while adding resource to the database";
    private static final String RESOURCE_ID_INVALID = "Resource Id must be greater than 0";
    private static final String RESOURCE_LEVEL_INVALID = "Resource Difficulty Level must be greater than 0";
    private static final String RESOURCE_NAME_NULL = "Resource Name can not be null";
    private static final String RESOURCE_LINK_NULL = "Resource Link can not be null";
    private static final String RESOURCE_TYPE_NULL = "Resource Type can not be null";
    private static final String RESOURCE_NAME_EMPTY = "Resource Name can't be empty/blank";
    private static final String RESOURCE_LINK_EMPTY = "Resource Link can't be empty/blank";
    private static final String RESOURCE_TYPE_EMPTY = "Resource Type can't be empty/blank";
    private static final String RESOURCE_OWNER_ERROR_MESSAGE = "Resource owner cannot be null/empty/blank";
    private static final String DATABASE_ACCESS_ERROR_MESSAGE = "There was an error while attempting to access the database";
    private static final String INVALID_STRING = "Search string cannot be null or empty";
    private static final String INVALID_ID = "The id is invalid";
    private static final String RESOURCE_STATUS_NULL_ERROR_MESSAGE = "Resource status cannot be null";
    private static final String RESOURCE_STATUS_INVALID = "Resource status must be Available/Pending/Deleted";

    private Resource resource;
    private ResourceDAOImpl.ResourceRowMapper resourceMapper;
    private Resource newResource;
    private List<Resource> newListOfResource;

    static {
        URL tempUrl;
        try {
            tempUrl = new URL("http://www.testURL.com");
        } catch (final MalformedURLException e) {
            tempUrl = null;
        }
        STATIC_URL = tempUrl;
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private ResourceDAOImpl resourceDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private CategoryResourceRelationDAO categoryResourceRelationDAO;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private HttpURLValidator validator;
    @Mock
    private ResultSet resultSet;

    @Before
    public void setUp() throws MalformedURLException, SQLException {
        resource = new Resource(
                VALID_RESOURCE_ID,
                STATIC_URL,
                VALID_RESOURCE_DESCRIPTION,
                VALID_RESOURCE_NAME,
                VALID_RESOURCE_TYPE);
        resourceMapper = new ResourceRowMapper();
        newListOfResource = new ArrayList<>();
        newListOfResource.add(resource);
        when(jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class)).thenReturn(VALID_RESOURCE_ID);
        when(
                jdbcTemplate.update(
                        INSERT_RESOURCE_QUERY,
                        VALID_RESOURCE_ID,
                        VALID_RESOURCE_DESCRIPTION,
                        STATIC_URL.toString(),
                        VALID_RESOURCE_TYPE.getResourceTypeId())).thenReturn(1);
        when(resultSet.getInt("resource_id")).thenReturn(VALID_RESOURCE_ID);
        when(resultSet.getString("name")).thenReturn(VALID_RESOURCE_NAME);
        when(resultSet.getString("description")).thenReturn(VALID_RESOURCE_DESCRIPTION);
        when(resultSet.getString("link")).thenReturn(STATIC_URL.toString());
        when(resultSet.getInt("type_id")).thenReturn(VALID_RESOURCE_TYPE.getResourceTypeId());
        when(resultSet.getString("type_name")).thenReturn(VALID_RESOURCE_TYPE.getResourceType());
    }

    /**
     * Verifies {@link ResourceDAOImpl#getResourceDescriptionById(int)} when
     * ResourceId of {@link Resource} is not present in resource table.
     */
    @Test
    public void testGetResourceDescriptionById_WhenResourceIdIsInvalid() {
        when(jdbcTemplate.queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, INVALID_RESOURCE_ID))
                .thenReturn(null);
        assertEquals(null, resourceDAOImpl.getResourceDescriptionById(INVALID_RESOURCE_ID));
        verify(jdbcTemplate).queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, INVALID_RESOURCE_ID);
    }

    /**
     * Verifies {@link ResourceDAOImpl#getResourceDescriptionById(int)} when
     * ResourceId of {@link Resource} is present in resource table.
     */
    @Test
    public void testGetResourceDescriptionById_WhenResourceIdIsvalid() {
        when(jdbcTemplate.queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, VALID_RESOURCE_ID))
                .thenReturn(VALID_RESOURCE_DESCRIPTION);
        assertEquals(VALID_RESOURCE_DESCRIPTION, resourceDAOImpl.getResourceDescriptionById(VALID_RESOURCE_ID));
        verify(jdbcTemplate).queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, VALID_RESOURCE_ID);
    }

    /**
     * Verifies {@link ResourceDAOImpl#getResourceDescriptionById(int)} when
     * ResourceId of {@link Resource} is negative.
     */
    @Test
    public void testGetResourceDescriptionById_WhenResourceIdIsNegative() {
        when(jdbcTemplate.queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, ID_SMALLER_THAN_ZERO))
                .thenReturn(null);
        assertEquals(null, resourceDAOImpl.getResourceDescriptionById(ID_SMALLER_THAN_ZERO));
        verify(jdbcTemplate).queryForObject(GET_RESOURCE_DESCRIPTION_BY_ID_QUERY, String.class, ID_SMALLER_THAN_ZERO);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getSearchedResources(String)}
     * functionality and expects {@link DAOException} when there is error trying
     * to get all the resources from database.
     */
    @Test
    public void testGetSearchedResourcesThrowsException() throws DAOException {
        doThrow(EmptyResultDataAccessException.class)
                .when(jdbcTemplate)
                .query(anyString(), any(ResourceRowMapper.class), anyString(), anyString());
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        resourceDAOImpl.getSearchedResources(VALID_RESOURCE_NAME);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getSearchedResources(String)}
     * functionality for valid query for returned input.
     */
    @Test
    public void testGetSearchedResourcesValidQueryForRows() throws DAOException {
        final List<Resource> resourcesList = createTestResources();
        when(jdbcTemplate.query(anyString(), any(ResourceRowMapper.class), anyString(), anyString()))
                .thenReturn(resourcesList);
        final List<Resource> resultResourcesList = resourceDAOImpl.getSearchedResources(VALID_RESOURCE_NAME);
        assertEquals(resourcesList.size(), resultResourcesList.size());
        for (int i = 0; i < resourcesList.size(); i++) {
            final Resource expectedResource = resourcesList.get(i);
            final Resource actualResource = resultResourcesList.get(i);
            assertEquals(expectedResource.getResourceId(), actualResource.getResourceId());
            assertEquals(expectedResource.getResourceLink(), actualResource.getResourceLink());
            assertEquals(expectedResource.getDescription(), actualResource.getDescription());
            assertEquals(expectedResource.getResourceType(), actualResource.getResourceType());
        }
    }

    /**
     * This function tests {@link ResourceDAOImpl#getSearchedResources(String)}
     * functionality for valid query when result is empty.
     */
    @Test
    public void testGetSearchedResourcesValidQueryForEmpty() throws DAOException {
        final List<Resource> resourcesList = Collections.emptyList();
        when(jdbcTemplate.query(anyString(), any(ResourceRowMapper.class), anyString(), anyString()))
                .thenReturn(resourcesList);
        final List<Resource> resultResourcesList = resourceDAOImpl.getSearchedResources(VALID_RESOURCE_NAME);
        assertEquals(0, resultResourcesList.size());
    }

    /**
     * This function tests {@link ResourceDAOImpl#getSearchedResources(String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is empty.
     */
    @Test
    public void testGetSearchedResourcesForEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_STRING);
        resourceDAOImpl.getSearchedResources("");
    }

    /**
     * This function tests {@link ResourceDAOImpl#getSearchedResources(String)}
     * functionality and expects {@link IllegalArgumentException} when search
     * string is null.
     */
    @Test
    public void testGetSearchedResourcesForNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_STRING);
        resourceDAOImpl.getSearchedResources(null);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getById(int)} functionality
     * and expects {@link IllegalArgumentException} when id is negative
     */
    @Test
    public void testGetResourceByIDWhenIdIsNegative() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID);
        resourceDAOImpl.getById(ID_SMALLER_THAN_ZERO);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getById(int)} functionality
     * and expects {@link IllegalArgumentException} when id is zero
     */
    @Test
    public void testGetResourceByIDWhenIdIsZero() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_ID);
        resourceDAOImpl.getById(0);
    }

    /**
     * This function verifies {@link ResourceRowMapper#mapRow(ResultSet, int)}
     * functionality
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newResource = resourceMapper.mapRow(resultSet, 1);
        assertEquals(STATIC_URL, newResource.getResourceLink());
        assertEquals(VALID_RESOURCE_ID, newResource.getResourceId());
        assertEquals(VALID_RESOURCE_NAME, newResource.getResourceName());
        assertEquals(VALID_RESOURCE_TYPE.getResourceTypeId(), newResource.getResourceType().getResourceTypeId());
        assertEquals(VALID_RESOURCE_TYPE.getResourceType(), newResource.getResourceType().getResourceType());
    }

    /**
     * this function tests {@link ResourceRowMapper#mapRow(ResultSet, int)}
     * functionality and expects {@link MalformedURLException} when url returned
     * from database is invalid
     */
    @Test
    public void testMapRowInvalidURL() throws SQLException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(
                "Error: Invalid URL in database; table 'course' for row with resource id " + VALID_RESOURCE_ID);
        when(resultSet.getString("link")).thenReturn(WRONG_URL);
        resourceMapper.mapRow(resultSet, 1);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getById(int)} functionality
     * and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test
    public void testGetResourceByIDWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage("Error while extracting resource by its ID");
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        resourceDAOImpl.getById(VALID_RESOURCE_ID);
    }

    /**
     * This function verifies {@link ResourceDAOImpl#getById(int)} functionality
     */
    @Test
    public void testGetResourceByIDValid() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceRowMapper.class), anyInt())).thenReturn(resource);
        newResource = resourceDAOImpl.getById(VALID_RESOURCE_ID);
        assertEquals(VALID_RESOURCE_ID, newResource.getResourceId());
        assertEquals(STATIC_URL, newResource.getResourceLink());
        assertEquals(VALID_RESOURCE_DESCRIPTION, newResource.getDescription());
        assertEquals(VALID_RESOURCE_TYPE, newResource.getResourceType());
    }

    /**
     * This function tests {@link ResourceDAOImpl#(String, String, URL,
     * ResourceType, String, String)} functionality and expects
     * {@link IllegalArgumentException} when {@link Resource#getDescription()}
     * returns null.
     */
    @Test
    public void testAddResourceToDBWhenDescriptionIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_DESC_MESSAGE);
        resourceDAOImpl.addResource(null, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE, VALID_RESOURCE_OWNER,
                VALID_RESOURCE_STATUS);
    }

    /**
     * This function tests
     * {@link ResourceDAOImpl#addResource(String, String, URL, ResourceType, String, String)}
     * functionality and expects {@link NullPointerException} when
     * {@link Resource#getResourceLink()} returns null.
     */
    @Test
    public void testAddResourceToDBWhenUrlIsNull() throws DAOException {
        expectedException.expect(NullPointerException.class);
        resourceDAOImpl.addResource(
                VALID_RESOURCE_NAME,
                VALID_RESOURCE_DESCRIPTION,
                null,
                VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER,
                VALID_RESOURCE_STATUS);
    }

    /**
     * This function tests
     * {@link ResourceDAOImpl#addResource(String, String, URL, ResourceType, String, String)}
     * after code review functionality and expects
     * {@link IllegalArgumentException} when {@link Resource#getResourceType}
     * returns null.
     */
    @Test
    public void testAddResourceToDBWhenResourceTypeIsNull() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(NULL_RESOURCE_TYPE_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL, null,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /**
     * This function tests
     * {@link ResourceDAOImpl#addResource(String, String, URL, ResourceType, String, String)}
     * and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class)} throws
     * {@link DataAccessException}.
     */
    @Test
    public void testAddResourceToDBWhenJdbcTemplateOfPrivateMethodThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(ERROR_ADDING_RESOURCE);
        when(jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class)).thenThrow(dataAccessException);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /**
     * This function tests
     * {@link ResourceDAOImpl#addResource(String, String, URL, ResourceType, String, String)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}
     */
    @Test
    public void testAddResourceToDBWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(ERROR_ADDING_RESOURCE);
        when(jdbcTemplate.update(INSERT_RESOURCE, VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME,
                STATIC_URL.toString(), VALID_RESOURCE_TYPE.getResourceTypeId(), VALID_RESOURCE_OWNER,
                VALID_RESOURCE_STATUS)).thenThrow(dataAccessException);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /**
     * This function verifies
     * {@link ResourceDAOImpl#addResource(String, String, URL, ResourceType, String, String) }
     * functionality when given valid inputs
     */
    @Test
    public void testAddResourceToDBValid() throws DAOException {
        final int actualId = resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL,
                VALID_RESOURCE_TYPE, VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
        assertEquals(VALID_RESOURCE_ID, actualId);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource
     * description is empty when adding a resource
     */
    @Test
    public void testAddResourceToDBEmptyResourceDescription() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_DESC_MESSAGE);
        resourceDAOImpl.addResource(EMPTY_STRING, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource
     * description is blank when adding a resource
     */
    @Test
    public void testAddResourceToDBBlankResourceDescription() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_DESC_MESSAGE);
        resourceDAOImpl.addResource(BLANK_STRING, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource name is
     * empty when adding a resource
     */
    @Test
    public void testAddResourceToDBEmptyResourceName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_NAME_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, EMPTY_STRING, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource name is
     * blank when adding a resource
     */
    @Test
    public void testAddResourceToDBBlankResourceName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_NAME_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, BLANK_STRING, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource name is
     * null when adding a resource
     */
    @Test
    public void testAddResourceToDBNullResourceName() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_NAME_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, null, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the url is null when
     * adding a resource
     */
    @Test
    public void testAddResourceToDBInvalidLink() throws DAOException, MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_RESOURCE_URL_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME,
                new URL("ftp", "somehost", "somefile"), VALID_RESOURCE_TYPE, VALID_RESOURCE_OWNER,
                VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource owner is
     * null when adding a resource
     */
    @Test
    public void testAddResourceInvalidResourceOwner() throws DAOException, MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_OWNER_ERROR_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                null, VALID_RESOURCE_STATUS);
    }

    /***
     * This test verifies that
     * {@link ResourceDAO#addResource(String, String, URL, ResourceType, String, String)}
     * will throw an {@link IllegalArgumentException} when the resource status
     * is null when adding a resource
     */
    @Test
    public void testAddResourceResourceStatusNull() throws DAOException, MalformedURLException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(RESOURCE_STATUS_NULL_ERROR_MESSAGE);
        resourceDAOImpl.addResource(VALID_RESOURCE_DESCRIPTION, VALID_RESOURCE_NAME, STATIC_URL, VALID_RESOURCE_TYPE,
                VALID_RESOURCE_OWNER, null);
    }

    /**
     * Test that {@link JdbcTemplate#update(String, Object...)} is called during
     * a call to {@link ResourceDAOImpl#deleteById(int)}.
     */
    @Test
    public void testDeleteById() throws DAOException {
        resourceDAOImpl.deleteById(1);
        verify(jdbcTemplate).update(anyString(), anyObject());
    }

    /**
     * Test that an exception is thrown when
     * {@link ResourceDAOImpl#deleteById(int)} is called with an invalid
     * {@link Resource} id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByIdNegativeResourceId() throws DAOException {
        resourceDAOImpl.deleteById(-1);
    }

    /**
     * Tests that zero is returned from
     * {@link JdbcTemplate#update(org.springframework.jdbc.core.PreparedStatementCreator)}
     * when {@link ResourceDAOImpl#deleteById(int)} is called with an id which
     * is positive but not present in database.
     */
    @Test
    public void testDeleteByIdInvalidResourceId() throws DAOException {
        resourceDAOImpl.deleteById(INVALID_RESOURCE_ID);
        assertEquals(0, jdbcTemplate.update("DELETE FROM resource WHERE resource_id=?"));
    }

    /**
     * Test verifies that a {@link DAOException} is thrown when there is an
     * error in accessing the database.
     */
    @Test(expected = DAOException.class)
    public void testDeleteByIdDAOException() throws DAOException {
        when(jdbcTemplate.update(anyString(), anyObject())).thenThrow(dataAccessException);
        resourceDAOImpl.deleteById(1);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getResourcesByCategoryId(int)}
     * functionality and expects {@link IllegalArgumentException} when id is
     * negative
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourcesByCategoryIdWhenIdIsNegative() throws DAOException {
        resourceDAOImpl.getResourcesByCategoryId(ID_SMALLER_THAN_ZERO);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getResourcesByCategoryId(int)}
     * functionality and expects {@link IllegalArgumentException} when
     * category_id is zero
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceByCategoryIDWhenIdIsZero() throws DAOException {
        resourceDAOImpl.getResourcesByCategoryId(0);
    }

    /**
     * This function tests {@link ResourceDAOImpl#getResourcesByCategoryId(int)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#query(String, org.springframework.jdbc.core.RowMapper, Object...)}
     * throws {@link DataAccessException} .
     */
    @Test(expected = DAOException.class)
    public void testGetResourceByCategoryIDWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceRowMapper.class), anyObject())).thenThrow(dataAccessException);
        resourceDAOImpl.getResourcesByCategoryId(CATEGORY_ID);
    }

    /**
     * This function verifies
     * {@link ResourceDAOImpl#getResourcesByCategoryId(int)} functionality with
     * valid input.
     */
    @Test
    public void testGetResourceByCategoryIDValid() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(ResourceRowMapper.class), anyObject())).thenReturn(newListOfResource);
        newListOfResource = resourceDAOImpl.getResourcesByCategoryId(CATEGORY_ID);
        assertEquals(STATIC_URL, newListOfResource.get(0).getResourceLink());
        assertEquals(VALID_RESOURCE_NAME, newListOfResource.get(0).getResourceName());
        assertEquals(VALID_RESOURCE_ID, newListOfResource.get(0).getResourceId());
        assertEquals(VALID_RESOURCE_TYPE, newListOfResource.get(0).getResourceType());
    }

    /**
     * Helper method to initialize a list of {@link Resource}.
     */
    private List<Resource> createTestResources() {
        final List<Resource> resources = new ArrayList<>();

        for (int i = 0; i < MAX_RESOURCE_NUMBER; i++) {
            resources.add(
                    new Resource(
                            VALID_RESOURCE_ID + i,
                            STATIC_URL,
                            VALID_RESOURCE_DESCRIPTION + i,
                            VALID_RESOURCE_TYPE));
        }
        return resources;
    }

    /**
     * Verifies that {@link ResourceDAOImpl#getResourceCountByCategoryId(int)}
     * expects {@link IllegalArgumentException} when Category Id is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceCountByCategoryIdWhenIdIsNegative() throws DAOException {
        resourceDAOImpl.getResourceCountByCategoryId(-1);
    }

    /**
     * Verifies that {@link ResourceDAOImpl#getResourceCountByCategoryId(int)}
     * expects {@link IllegalArgumentException} when Category Id is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetResourceCountByCategoryIdWhenIdIsZero() throws DAOException {
        resourceDAOImpl.getResourceCountByCategoryId(0);
    }

    /**
     * Verifies {@link ResourceDAOImpl#getResourceCountByCategoryId(int)}
     * expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} } throws
     * {@link DataAccessException} .
     */
    @Test(expected = DAOException.class)
    public void testGetResourceCountByCategoryIdWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(Class.class), anyInt())).thenThrow(dataAccessException);
        resourceDAOImpl.getResourceCountByCategoryId(CATEGORY_ID);
    }

    /**
     * Verifies {@link ResourceDAOImpl#getResourceCountByCategoryId(int)} and
     * expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException} .
     */
    @Test
    public void testGetResourceCountByCategoryIdIfQueryIsExecuted() throws DAOException {
        when(jdbcTemplate.queryForObject(RESOURCE_COUNT_QUERY, Integer.class, CATEGORY_ID)).thenReturn(20);
        resourceDAOImpl.getResourceCountByCategoryId(CATEGORY_ID);
        verify(jdbcTemplate).queryForObject(anyString(), any(Class.class), anyInt());
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when invalid ID is 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithZeroId() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    0,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when invalid ID is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNegativeId() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    -1,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_ID_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when resource name is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNullResourceName() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    null,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource name is empty
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithEmptyResourceName() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    EMPTY_STRING,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource name is blank
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithBlankResourceName() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    BLANK_STRING,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_NAME_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when resource URL is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNullResourceURL() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    null,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_LINK_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resource URL of the
     * {@link Resource} is invalid.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithInvalidResourceLink() throws MalformedURLException, DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    new URL("ftp", "somehost", "somefile"),
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_LINK_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when resource difficulty level
     * is 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithZeroResourceDifficultyLevel() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    0,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_LEVEL_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when resource difficulty level
     * is negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNegativeResourceDifficultyLevel() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    -1,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_LEVEL_INVALID, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when resource type is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNullResourceType() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    null,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_TYPE_NULL, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithEmptyResourceType() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    EMPTY_STRING,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the name of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithBlankResourceType() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    BLANK_STRING,
                    VALID_RESOURCE_OWNER);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_TYPE_EMPTY, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is <code>null</code>.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithNullResourceOwner() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    null);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithBlankResourceOwner() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    BLANK_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link IllegalArgumentException} when the resourceOwner of the
     * {@link Resource} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEditByIdWithEmptyResourceOwner() throws DAOException {
        try {
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    EMPTY_STRING);
        } catch (final IllegalArgumentException e) {
            assertEquals(RESOURCE_OWNER_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Checks whether
     * {@link JdbcTemplate#queryForObject(String, Object[], Class)} is called
     * during a call to
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}.
     */
    @Test
    public void testEditById() throws DAOException {
        when(jdbcTemplate.queryForObject(EDIT_RESOURCE_QUERY, new Object[] { VALID_RESOURCE_TYPE_NAME }, Integer.class))
                .thenReturn(3);
        resourceDAOImpl.updateResource(
                VALID_RESOURCE_ID,
                VALID_RESOURCE_NAME,
                STATIC_URL,
                VALID_DIFFICULTY_LEVEL,
                VALID_RESOURCE_TYPE_NAME,
                VALID_RESOURCE_OWNER);
        jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(Class.class));
    }

    /**
     * Expects
     * {@link ResourceDAOImpl#updateResource(int, String, URL, int, String, String)}
     * to throw {@link DAOException} when there is an error in accessing the
     * database.
     */
    @Test(expected = DAOException.class)
    public void testEditByIdDAOException() throws DAOException {
        try {
            when(
                    jdbcTemplate.update(
                            anyString(),
                            anyString(),
                            anyString(),
                            anyObject(),
                            anyObject(),
                            anyString(),
                            anyObject())).thenThrow(dataAccessException);
            when(
                    jdbcTemplate.queryForObject(
                            EDIT_RESOURCE_QUERY,
                            new Object[] { VALID_RESOURCE_TYPE_NAME },
                            Integer.class)).thenReturn(3);
            resourceDAOImpl.updateResource(
                    VALID_RESOURCE_ID,
                    VALID_RESOURCE_NAME,
                    STATIC_URL,
                    VALID_DIFFICULTY_LEVEL,
                    VALID_RESOURCE_TYPE_NAME,
                    VALID_RESOURCE_OWNER);
        } catch (final DAOException e) {
            assertEquals(DATABASE_ACCESS_ERROR_MESSAGE, e.getMessage());
            throw e;
        }
    }
}