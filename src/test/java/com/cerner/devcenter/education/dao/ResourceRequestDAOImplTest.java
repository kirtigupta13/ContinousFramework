package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.ResourceRequest;

/**
 * Tests the {@link ResourceRequestDAOImpl} class.
 *
 * @author Navya Rangeneni (NR046827)
 * @author Vatsal Kesarwani (VK049896)
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceRequestDAOImplTest {

    private static final String INSERT_RESOURCE_REQUEST_QUERY = "INSERT INTO resource_request (user_id, category_name, resource_name) VALUES (?,?,?)";
    private static final String DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY = "DELETE FROM resource_request WHERE id = ?";

    private static final String VALID_USER_ID = "AA012345";
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final String VALID_RESOURCE_NAME = "YouTube";
    private static final int[] REQUEST_IDS = new int[] { 1, 2, 3 };

    private static final String EMPTY = "";
    private static final String BLANK = " ";
    private static final String NULL_RESOURCE_REQUEST_EXPECTED_MSG = "ResourceRequest object is null.";
    private static final String NULL_USER_ID_EXPECTED_MSG = "UserId is null.";
    private static final String NULL_CATEGORY_NAME_EXPECTED_MSG = "CategoryName is null";
    private static final String NULL_RESOURCE_NAME_EXPECTED_MSG = "ResourceName is null";
    private static final String INVALID_REQUEST_IDS_ERROR_MSG = "Resource request ID's array is null/empty.";
    private static final String INVALID_USER_ID_EXPECTED_MSG = "UserId is null/empty/blank.";

    private static final String RESOURCE_REQUEST_DB_INSERT_EXPECTED_MSG = "Error while adding resource request to the database.";
    private static final String RESOURCE_REQUEST_DB_READ_EXPECTED_MSG = "Error while retrieving all resource requests.";
    private static final String RESOURCE_REQUEST_DB_DELETE_ERROR_MSG = "Error while deleting the resource request.";

    @InjectMocks
    private ResourceRequestDAOImpl resourceRequestDAOImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataAccessException dataAccessException;
    @Mock
    private ResultSet resultSet;
    @Mock
    private ResourceRequest mockResourceRequest;
    @Mock
    private RowMapper<ResourceRequest> resourceRequestRowMapper;

    private final ResourceRequest resourceRequest = new ResourceRequest();

    private List<ResourceRequest> requests;

    @Before
    public void setUp() throws SQLException {
        when(resultSet.getString("user_id")).thenReturn(VALID_USER_ID);
        when(resultSet.getString("category_name")).thenReturn(VALID_CATEGORY_NAME);
        when(resultSet.getString("resource_name")).thenReturn(VALID_RESOURCE_NAME);
        requests = new ArrayList<ResourceRequest>();
        resourceRequest.setUserId(VALID_USER_ID);
        resourceRequest.setCategoryName(VALID_CATEGORY_NAME);
        resourceRequest.setResourceName(VALID_RESOURCE_NAME);
        requests.add(resourceRequest);
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to
     * throw {@link IllegalArgumentException} when ResourceRequest object is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceRequest_NullResourceRequest() throws DAOException {
        try {
            resourceRequestDAOImpl.addResourceRequest(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_RESOURCE_REQUEST_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to
     * throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceRequest_NullUserID() throws DAOException {
        when(mockResourceRequest.getUserId()).thenReturn(null);
        try {
            resourceRequestDAOImpl.addResourceRequest(mockResourceRequest);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to
     * throw {@link IllegalArgumentException} when categoryName is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceRequest_NullCategoryName() throws DAOException {
        when(mockResourceRequest.getUserId()).thenReturn(VALID_USER_ID);
        when(mockResourceRequest.getCategoryName()).thenReturn(null);
        try {
            resourceRequestDAOImpl.addResourceRequest(mockResourceRequest);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_CATEGORY_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to
     * throw {@link IllegalArgumentException} when resourceName is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddResourceRequest_NullResourceName() throws DAOException {
        when(mockResourceRequest.getUserId()).thenReturn(VALID_USER_ID);
        when(mockResourceRequest.getCategoryName()).thenReturn(VALID_CATEGORY_NAME);
        when(mockResourceRequest.getResourceName()).thenReturn(null);
        try {
            resourceRequestDAOImpl.addResourceRequest(mockResourceRequest);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_RESOURCE_NAME_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to
     * throw {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testAddResourceRequest_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.update(INSERT_RESOURCE_REQUEST_QUERY, VALID_USER_ID, VALID_CATEGORY_NAME,
                VALID_RESOURCE_NAME)).thenThrow(dataAccessException);
        try {
            resourceRequestDAOImpl.addResourceRequest(resourceRequest);
        } catch (final DAOException e) {
            assertEquals(RESOURCE_REQUEST_DB_INSERT_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link ResourceRequestDAOImpl#addResourceRequest(ResourceRequest)} to run
     * successfully when no exception is thrown.
     */
    @Test
    public void testAddResourceRequest() throws DAOException {
        when(jdbcTemplate.update(INSERT_RESOURCE_REQUEST_QUERY, VALID_USER_ID, VALID_CATEGORY_NAME,
                VALID_RESOURCE_NAME)).thenReturn(1);
        resourceRequestDAOImpl.addResourceRequest(resourceRequest);
        verify(jdbcTemplate, times(1)).update(INSERT_RESOURCE_REQUEST_QUERY, VALID_USER_ID, VALID_CATEGORY_NAME,
                VALID_RESOURCE_NAME);
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests()} to throw
     * {@link DAOException} when {@link JdbcTemplate#query(String, RowMapper)}
     * throws {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetAllResourceRequests_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(resourceRequestRowMapper.getClass()))).thenThrow(dataAccessException);
        try {
            resourceRequestDAOImpl.getAllResourceRequests();
        } catch (final DAOException e) {
            assertEquals(RESOURCE_REQUEST_DB_READ_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests()} to run
     * successfully when no exception is thrown.
     */
    @Test
    public void testGetAllResourceRequests() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(requests);
        assertSame(requests, resourceRequestDAOImpl.getAllResourceRequests());
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(String)} to
     * throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllResourceRequest_NullUserId() throws DAOException {
        try {
            resourceRequestDAOImpl.getAllResourceRequests(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(String)} to
     * throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllResourceRequest_EmptyUserId() throws DAOException {
        try {
            resourceRequestDAOImpl.getAllResourceRequests(EMPTY);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(String)} to
     * throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllResourceRequest_BlankUserId() throws DAOException {
        try {
            resourceRequestDAOImpl.getAllResourceRequests(BLANK);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(String)} to
     * throw {@link DAOException} when
     * {@link JdbcTemplate#query(String, RowMapper, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetAllResourceRequests_String_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(resourceRequestRowMapper.getClass()), anyString()))
                .thenThrow(dataAccessException);
        try {
            resourceRequestDAOImpl.getAllResourceRequests(VALID_USER_ID);
        } catch (final DAOException e) {
            assertEquals(RESOURCE_REQUEST_DB_READ_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(String)} to
     * run successfully when no exception is thrown.
     */
    @Test
    public void testGetAllResourceRequests_String() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(requests);
        assertSame(requests, resourceRequestDAOImpl.getAllResourceRequests(VALID_USER_ID));
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(boolean)} to
     * throw {@link DAOException} when
     * {@link JdbcTemplate#query(String, RowMapper, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetAllResourceRequests_Boolean_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(resourceRequestRowMapper.getClass()), anyString()))
                .thenThrow(dataAccessException);
        try {
            resourceRequestDAOImpl.getAllResourceRequests(Boolean.TRUE);
        } catch (final DAOException e) {
            assertEquals(RESOURCE_REQUEST_DB_READ_EXPECTED_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#getAllResourceRequests(boolean)} to
     * run successfully when no exception is thrown.
     */
    @Test
    public void testGetAllResourceRequests_Boolean() throws DAOException {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(requests);
        assertSame(requests, resourceRequestDAOImpl.getAllResourceRequests(Boolean.TRUE));
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} to
     * throw {@link IllegalArgumentException} when ids is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteResourceRequests_NullIds() throws DAOException {
        try {
            resourceRequestDAOImpl.deleteResourceRequests(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_IDS_ERROR_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} to
     * throw {@link IllegalArgumentException} when ids is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteResourceRequests_EmptyIds() throws DAOException {
        try {
            resourceRequestDAOImpl.deleteResourceRequests(new int[0]);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_IDS_ERROR_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} to
     * throw {@link DAOException} when
     * {@link JdbcTemplate#batchUpdate(String, BatchPreparedStatementSetter)}
     * throws {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testDeleteResourceRequests_ThrowsDataAccessException() throws DAOException {
        when(jdbcTemplate.batchUpdate(eq(DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY),
                any(BatchPreparedStatementSetter.class))).thenThrow(dataAccessException);
        try {
            resourceRequestDAOImpl.deleteResourceRequests(REQUEST_IDS);
        } catch (final DAOException e) {
            assertEquals(RESOURCE_REQUEST_DB_DELETE_ERROR_MSG, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link ResourceRequestDAOImpl#deleteResourceRequests(int[])} to
     * run successfully when no exception is thrown.
     */
    @Test
    public void testDeleteResourceRequests() throws DAOException {
        when(jdbcTemplate.batchUpdate(eq(DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY),
                any(BatchPreparedStatementSetter.class))).thenReturn(new int[] { 1 });
        resourceRequestDAOImpl.deleteResourceRequests(REQUEST_IDS);
        verify(jdbcTemplate, times(1)).batchUpdate(eq(DELETE_RESOURCE_REQUESTS_IN_BATCH_QUERY),
                any(BatchPreparedStatementSetter.class));
    }
}