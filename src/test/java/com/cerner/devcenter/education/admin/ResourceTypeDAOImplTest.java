package com.cerner.devcenter.education.admin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cerner.devcenter.education.admin.ResourceDAOImpl.ResourceRowMapper;
import com.cerner.devcenter.education.admin.ResourceTypeDAOImpl.ResourceTypeRowMapper;
import com.cerner.devcenter.education.models.ResourceType;

/**
 * For testing the {@link ResourceTypeDAOImpl}.
 *
 * @author Gunjan Kaphle (GK045931)
 * @author JZ022690
 */

@RunWith(MockitoJUnitRunner.class)
public class ResourceTypeDAOImplTest {
    @InjectMocks
    private ResourceTypeDAOImpl resourceTypeDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String GET_MAX_ID_QUERY = "SELECT MAX(type_id) FROM type";
    private static final String INSERT_RESOURCE_TYPE_QUERY = "INSERT INTO type (type_name) VALUES(?)";
    private static final int VALID_TYPE_ID = 5;
    private static final String VALID_TYPE_NAME = "Online Classroom";
    private static final int NEGATIVE_TYPE_ID = -9;
    private static final String EMPTY_RESULT_ERROR_MESSAGE = "Error: the specified query did not return any results";
    private static final int MAX_TYPE_NUMBER = 9;
    private static final String ERROR_GETTING_BY_NAME = "Error while extracting type by its name";
    private static final String ERROR_EMPTY_NAME = "The name for resource type is invalid because it is either null or empty.";
    private static final String ERROR_INVALID_RESOURCE_ID = "The id for resource type is invalid because it is less than or equal to zero";

    private ResourceType resourceType;
    private ResourceTypeDAOImpl.ResourceTypeRowMapper resourceTypeMapper;
    private ResourceType newResourceType;
    private List<ResourceType> newListOfResourceTypes;

    @Before
    public void setup() throws SQLException {
        resourceType = new ResourceType(VALID_TYPE_ID, VALID_TYPE_NAME);
        resourceTypeMapper = new ResourceTypeRowMapper();
        newListOfResourceTypes = new ArrayList<>();
        newListOfResourceTypes.add(resourceType);
        when(jdbcTemplate.queryForObject(GET_MAX_ID_QUERY, Integer.class)).thenReturn(VALID_TYPE_ID);
        when(jdbcTemplate.update(INSERT_RESOURCE_TYPE_QUERY, VALID_TYPE_ID, VALID_TYPE_NAME)).thenReturn(1);
        when(resultSet.getInt("type_id")).thenReturn(VALID_TYPE_ID);
        when(resultSet.getString("type_name")).thenReturn(VALID_TYPE_NAME);
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getById(int)}
     * functionality and expects {@link IllegalArgumentException} when id is
     * negative
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByIDWhenIdIsNegative() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ERROR_INVALID_RESOURCE_ID);
        resourceTypeDAOImpl.getById(NEGATIVE_TYPE_ID);
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getById(int)}
     * functionality and expects {@link IllegalArgumentException} when id is
     * zero
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByIDWhenIdIsZero() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ERROR_INVALID_RESOURCE_ID);
        resourceTypeDAOImpl.getById(0);
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getAllResourceTypes()}
     * functionality and expects {@link DAOException} and an error message when
     * there is an error getting all the resources from database.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllResourceTypesThrowsException() throws DAOException {
        doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate).query(anyString(),
                any(ResourceRowMapper.class));
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(EMPTY_RESULT_ERROR_MESSAGE);
        resourceTypeDAOImpl.getAllResourceTypes();
    }

    /**
     * This function verifies
     * {@link ResourceTypeRowMapper#mapRow(ResultSet, int)} functionality
     * 
     * @throws SQLException
     */
    @Test
    public void testMapRowValidResultSet() throws SQLException {
        newResourceType = resourceTypeMapper.mapRow(resultSet, 1);
        assertEquals(VALID_TYPE_ID, newResourceType.getResourceTypeId());
        assertEquals(VALID_TYPE_NAME, newResourceType.getResourceType());
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#addResourceType(String)}
     * functionality and expects {@link NullPointerException} when
     * {@link ResourceType#getResourceType()} returns null.
     * 
     * @throws DAOException
     */
    @Test
    public void testAddResourceTypeToDBWhenTypeNameIsNull() throws DAOException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("The name for resource type is invalid because it is either null or empty");
        resourceTypeDAOImpl.addResourceType(null);
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getById(int)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException} .
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByIDWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage("Error while extracting resource by its id");
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceTypeRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        resourceTypeDAOImpl.getById(VALID_TYPE_ID);
    }

    /**
     * This function verifies {@link ResourceTypeDAOImpl#getById(int)}
     * functionality
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByIDValid() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceTypeRowMapper.class), anyInt()))
                .thenReturn(resourceType);
        newResourceType = resourceTypeDAOImpl.getById(VALID_TYPE_ID);
        assertEquals(VALID_TYPE_ID, newResourceType.getResourceTypeId());
        assertEquals(VALID_TYPE_NAME, newResourceType.getResourceType());
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getByName(String)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#queryForObject(String, Class, Object...)} throws
     * {@link DataAccessException} .
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByNameWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage(ERROR_GETTING_BY_NAME);
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceTypeRowMapper.class), anyInt()))
                .thenThrow(dataAccessException);
        resourceTypeDAOImpl.getByName(VALID_TYPE_NAME);
    }

    /**
     * This function verifies {@link ResourceTypeDAOImpl#getById(int)} when
     * given valid inputs. Tests the happy path.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetResourceTypeByNameValid() throws DAOException {
        when(jdbcTemplate.queryForObject(anyString(), any(ResourceTypeRowMapper.class), anyInt()))
                .thenReturn(resourceType);
        newResourceType = resourceTypeDAOImpl.getByName(VALID_TYPE_NAME);
        assertEquals(VALID_TYPE_ID, newResourceType.getResourceTypeId());
        assertEquals(VALID_TYPE_NAME, newResourceType.getResourceType());
    }

    /**
     * This function verifies {@link ResourceTypeDAOImpl#getByName(String)} will
     * throw an {@link IllegalArgumentException} when name is empty
     * 
     * @throws DAOException
     * 
     */
    @Test
    public void testGetResourceTypeByNameEmpty() throws DAOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ERROR_EMPTY_NAME);
        resourceTypeDAOImpl.getByName("");
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#addResourceType(String)}
     * functionality and expects {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}
     * 
     * @throws DAOException
     */
    @Test
    public void testAddResourceTypeToDBWhenJdbcTemplateThrowsDataAccessException() throws DAOException {
        expectedException.expect(DAOException.class);
        expectedException.expectMessage("Error while adding resource to the database");
        when(jdbcTemplate.update(INSERT_RESOURCE_TYPE_QUERY, VALID_TYPE_NAME)).thenThrow(dataAccessException);
        resourceTypeDAOImpl.addResourceType(VALID_TYPE_NAME);
    }

    /**
     * This function verifies
     * {@link ResourceTypeDAOImpl#addResourceType(String)} functionality
     * 
     * @throws DAOException
     */
    @Test
    public void testAddResourceToDBValid() throws DAOException {
        newResourceType = resourceTypeDAOImpl.addResourceType(VALID_TYPE_NAME);
        assertEquals(VALID_TYPE_ID, newResourceType.getResourceTypeId());
        assertEquals(VALID_TYPE_NAME, newResourceType.getResourceType());
    }

    /**
     * This function tests {@link ResourceTypeDAOImpl#getAllResourceTypes()}
     * functionality. Test method with a valid query that returns rows of result
     * as expected. The {@link List} of {@link ResourceType} objects returned
     * should also match the correct data.
     * 
     * @throws DAOException
     */
    @Test
    public void testGetAllResourceTypesWhenValidQueryForRows() throws DAOException {
        List<ResourceType> resourceTypesList = createTestResourceTypes();
        when(jdbcTemplate.query(anyString(), any(ResourceTypeRowMapper.class))).thenReturn(resourceTypesList);
        List<ResourceType> resultResourceTypesList = resourceTypeDAOImpl.getAllResourceTypes();
        assertEquals(resourceTypesList.size(), resultResourceTypesList.size());
        for (int i = 0; i < resultResourceTypesList.size(); i++) {
            ResourceType expectedResourceType = resourceTypesList.get(i);
            ResourceType actualResourceType = resultResourceTypesList.get(i);
            assertEquals(expectedResourceType.getResourceTypeId(), actualResourceType.getResourceTypeId());
            assertEquals(expectedResourceType.getResourceType(), actualResourceType.getResourceType());
        }
    }

    /**
     * Helper method to initialize a {@link List} of {@link ResourceType} for
     * testing purposes.
     */
    private List<ResourceType> createTestResourceTypes() {
        List<ResourceType> resourceTypes = new ArrayList<ResourceType>();
        for (int i = 0; i < MAX_TYPE_NUMBER; i++) {
            ResourceType temp = new ResourceType(VALID_TYPE_ID + i, VALID_TYPE_NAME + i);
            resourceTypes.add(temp);
        }
        return resourceTypes;
    }
}