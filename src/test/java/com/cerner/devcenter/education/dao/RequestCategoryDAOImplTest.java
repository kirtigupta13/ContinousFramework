package com.cerner.devcenter.education.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.models.RequestCategory;

/**
 * Tests the {@link RequestCategoryDAOImpl} class.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestCategoryDAOImplTest {

    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_USER_ID = "AA012345";
    private static final String VALID_CATEGORY_NAME = "Java";
    private static final boolean VALID_IS_APPROVED = Boolean.FALSE;

    private static final int ZERO = 0;
    private static final int NEGATIVE = -1;
    private static final String EMPTY = "";
    private static final String BLANK = " ";

    private static final String QUERY_INSERT_REQUEST_CATEGORY = "INSERT INTO request_category (name) VALUES (?)";
    private static final String QUERY_INSERT_USER_REQUEST_RELATION = "INSERT INTO user_request_category_reltn (user_id, request_category_id) VALUES (?, (SELECT id FROM request_category WHERE name = ?))";
    private static final String QUERY_DELETE_REQUEST_CATEGORY = "DELETE FROM user_request_category_reltn WHERE user_id = ? and request_category_id = ?";

    private static final String REQUEST_CATEGORY_DB_INSERT_EXPECTED_MESSAGE = "Error while adding the requested category to the database.";
    private static final String REQUEST_CATEGORY_DB_DELETE_EXPECTED_MESSAGE = "Error while deleting the requested category from the database.";
    private static final String REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE = "Error while retrieving the requested category from the database.";

    private static final String NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE = "RequestCategory object is null.";
    private static final String NULL_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE = "RequestCategory name is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_EXPECTED_MESSAGE = "UserId is null/empty/blank.";

    private final RequestCategory requestCategory = new RequestCategory();
    private final List<RequestCategory> requestCategories = new ArrayList<RequestCategory>();

    private RowMapper<RequestCategory> requestCategoryRowMapper;

    @InjectMocks
    private RequestCategoryDAOImpl mockRequestCategoryDAOImpl;
    @Mock
    private JdbcTemplate mockJdbcTemplate;
    @Mock
    private DataAccessException mockDataAccessException;
    @Mock
    private RequestCategory mockRequestCategory;

    @Before
    public void setUp() {
        requestCategory.setId(VALID_CATEGORY_ID);
        requestCategory.setName(VALID_CATEGORY_NAME);
        requestCategory.setApproved(VALID_IS_APPROVED);
        requestCategories.add(requestCategory);
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when RequestCategory object is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullRequestCategory() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(null, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when name is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullName() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(mockRequestCategory, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_REQUEST_CATEGORY_NAME_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_EmptyUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, EMPTY);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_BlankUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, BLANK);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testAddRequestCategory_FirstJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(mockJdbcTemplate.update(QUERY_INSERT_REQUEST_CATEGORY, VALID_CATEGORY_NAME))
                .thenThrow(mockDataAccessException);
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, VALID_USER_ID);
        } catch (final DAOException e) {
            assertEquals(REQUEST_CATEGORY_DB_INSERT_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to throw {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testAddRequestCategory_SecondJdbcTemplateThrowsDataAccessException() throws DAOException {
        when(mockJdbcTemplate.update(QUERY_INSERT_REQUEST_CATEGORY, VALID_CATEGORY_NAME)).thenReturn(1);
        when(mockJdbcTemplate.update(QUERY_INSERT_USER_REQUEST_RELATION, VALID_USER_ID, VALID_CATEGORY_NAME))
                .thenThrow(mockDataAccessException);
        try {
            mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, VALID_USER_ID);
        } catch (final DAOException e) {
            assertEquals(REQUEST_CATEGORY_DB_INSERT_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryDAOImpl#addRequestCategory(RequestCategory, String)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testAddRequestCategory() throws DAOException {
        when(mockJdbcTemplate.update(QUERY_INSERT_REQUEST_CATEGORY, VALID_CATEGORY_NAME)).thenReturn(1);
        when(mockJdbcTemplate.update(QUERY_INSERT_USER_REQUEST_RELATION, VALID_USER_ID, VALID_CATEGORY_NAME))
                .thenReturn(1);
        mockRequestCategoryDAOImpl.addRequestCategory(requestCategory, VALID_USER_ID);
        verify(mockJdbcTemplate, times(1)).update(QUERY_INSERT_REQUEST_CATEGORY, VALID_CATEGORY_NAME);
        verify(mockJdbcTemplate, times(1)).update(QUERY_INSERT_USER_REQUEST_RELATION, VALID_USER_ID,
                VALID_CATEGORY_NAME);
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_ZeroRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(ZERO, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is
     * negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NegativeRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(NEGATIVE, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NullUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(VALID_CATEGORY_ID, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_EmptyUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(VALID_CATEGORY_ID, EMPTY);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_BlankUserId() throws DAOException {
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(VALID_CATEGORY_ID, BLANK);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to throw {@link DAOException} when
     * {@link JdbcTemplate#update(String, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testDeleteRequestCategory_JdbcTemplateThrowsDataAccessException() throws DAOException {
        when(mockJdbcTemplate.update(QUERY_DELETE_REQUEST_CATEGORY, VALID_USER_ID, VALID_CATEGORY_ID))
                .thenThrow(mockDataAccessException);
        try {
            mockRequestCategoryDAOImpl.deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
        } catch (final DAOException e) {
            assertEquals(REQUEST_CATEGORY_DB_DELETE_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#deleteRequestCategory(int, String)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testDeleteRequestCategory() throws DAOException {
        when(mockJdbcTemplate.update(QUERY_DELETE_REQUEST_CATEGORY, VALID_USER_ID, VALID_CATEGORY_ID)).thenReturn(1);
        mockRequestCategoryDAOImpl.deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
        verify(mockJdbcTemplate, times(1)).update(QUERY_DELETE_REQUEST_CATEGORY, VALID_USER_ID, VALID_CATEGORY_ID);
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories()} to
     * return an empty list when {@link JdbcTemplate#query(String, RowMapper)}
     * returns null.
     */
    @Test
    public void testGetAllRequestCategories_NullJdbcTemplate() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper)).thenReturn(null);
        assertEquals(Collections.EMPTY_LIST, mockRequestCategoryDAOImpl.getAllRequestCategories());
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories()} to throw
     * {@link DAOException} when {@link JdbcTemplate#query(String, RowMapper)}
     * throws {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetAllRequestCategories_JdbcTemplateThrowsDataAccessException() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper)).thenThrow(mockDataAccessException);
        try {
            mockRequestCategoryDAOImpl.getAllRequestCategories();
        } catch (final DAOException e) {
            assertEquals(REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories()} to run
     * successfully when no exception is thrown.
     */
    @Test
    public void testGetAllRequestCategories() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper)).thenReturn(requestCategories);
        assertSame(requestCategories, mockRequestCategoryDAOImpl.getAllRequestCategories());
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories(boolean)}
     * to return an empty list when
     * {@link JdbcTemplate#query(String, RowMapper)} returns null.
     */
    @Test
    public void testGetAllRequestCategories_Boolean_NullJdbcTemplate() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper, anyString())).thenReturn(null);
        assertEquals(Collections.EMPTY_LIST, mockRequestCategoryDAOImpl.getAllRequestCategories(Boolean.TRUE));
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories(boolean)}
     * to throw {@link DAOException} when
     * {@link JdbcTemplate#query(String, RowMapper, Object...)} throws
     * {@link DataAccessException}.
     */
    @Test(expected = DAOException.class)
    public void testGetAllRequestCategories_Boolean_JdbcTemplateThrowsDataAccessException() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper, anyString()))
                .thenThrow(mockDataAccessException);
        try {
            mockRequestCategoryDAOImpl.getAllRequestCategories(Boolean.TRUE);
        } catch (final DAOException e) {
            assertEquals(REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryDAOImpl#getAllRequestCategories(boolean)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testGetAllRequestCategories_Boolean() throws DAOException {
        requestCategoryRowMapper = any(RowMapper.class);
        when(mockJdbcTemplate.query(anyString(), requestCategoryRowMapper, anyString())).thenReturn(requestCategories);
        assertSame(requestCategories, mockRequestCategoryDAOImpl.getAllRequestCategories(Boolean.TRUE));
    }
}