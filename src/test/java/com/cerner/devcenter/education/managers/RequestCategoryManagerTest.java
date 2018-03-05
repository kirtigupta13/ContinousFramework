package com.cerner.devcenter.education.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.dao.RequestCategoryDAO;
import com.cerner.devcenter.education.models.RequestCategory;

/**
 * Tests the {@link RequestCategoryManager} class.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestCategoryManagerTest {

    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_USER_ID = "AA012345";
    private static final String VALID_CATEGORY_NAME = "Java";

    private static final int ZERO = 0;
    private static final int NEGATIVE = -1;
    private static final String EMPTY = "";
    private static final String BLANK = " ";

    private static final String REQUEST_CATEGORY_DB_INSERT_EXPECTED_MESSAGE = "Error while adding the requested category to the database.";
    private static final String REQUEST_CATEGORY_DB_DELETE_EXPECTED_MESSAGE = "Error while deleting the requested category from the database.";
    private static final String REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE = "Error while retrieving the requested category from the database.";

    private static final String NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE = "RequestCategory object is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_EXPECTED_MESSAGE = "UserId is null/empty/blank.";

    private final RequestCategory requestCategory = new RequestCategory();
    private final List<RequestCategory> requestCategories = new ArrayList<RequestCategory>();

    @InjectMocks
    private RequestCategoryManager mockRequestCategoryManager;
    @Mock
    private RequestCategoryDAO mockRequestCategoryDAO;
    @Mock
    private DAOException mockDAOException;

    @Before
    public void setUp() throws DAOException {
        requestCategory.setName(VALID_CATEGORY_NAME);
        requestCategories.add(requestCategory);
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when RequestCategory object is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullRequestCategory() throws DAOException {
        try {
            mockRequestCategoryManager.addRequestCategory(null, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullUserId() throws DAOException {
        try {
            mockRequestCategoryManager.addRequestCategory(requestCategory, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_EmptyUserId() throws DAOException {
        try {
            mockRequestCategoryManager.addRequestCategory(requestCategory, EMPTY);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_BlankUserId() throws DAOException {
        try {
            mockRequestCategoryManager.addRequestCategory(requestCategory, BLANK);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to throw {@link ManagerException} when
     * {@link RequestCategoryDAO#addRequestCategory(RequestCategory, String)}
     * throws {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testAddRequestCategory_DAOThrowsDataAccessException() throws DAOException {
        doThrow(mockDAOException).when(mockRequestCategoryDAO).addRequestCategory(requestCategory, VALID_USER_ID);
        try {
            mockRequestCategoryManager.addRequestCategory(requestCategory, VALID_USER_ID);
        } catch (final ManagerException e) {
            assertEquals(REQUEST_CATEGORY_DB_INSERT_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testAddRequestCategory() throws DAOException {
        mockRequestCategoryManager.addRequestCategory(requestCategory, VALID_USER_ID);
        verify(mockRequestCategoryDAO, times(1)).addRequestCategory(requestCategory, VALID_USER_ID);
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_ZeroRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryManager.deleteRequestCategory(ZERO, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is
     * negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NegativeRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryManager.deleteRequestCategory(NEGATIVE, VALID_USER_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NullUserId() throws DAOException {
        try {
            mockRequestCategoryManager.deleteRequestCategory(VALID_CATEGORY_ID, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_EmptyUserId() throws DAOException {
        try {
            mockRequestCategoryManager.deleteRequestCategory(VALID_CATEGORY_ID, EMPTY);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_BlankUserId() throws DAOException {
        try {
            mockRequestCategoryManager.deleteRequestCategory(VALID_CATEGORY_ID, BLANK);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to throw {@link ManagerException} when
     * {@link RequestCategoryDAO#deleteRequestCategory(int, String)} throws
     * {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testDeleteRequestCategory_DAOThrowsDataAccessException() throws DAOException {
        doThrow(mockDAOException).when(mockRequestCategoryDAO).deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
        try {
            mockRequestCategoryManager.deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
        } catch (final ManagerException e) {
            assertEquals(REQUEST_CATEGORY_DB_DELETE_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testDeleteRequestCategory() throws DAOException {
        mockRequestCategoryManager.deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
        verify(mockRequestCategoryDAO, times(1)).deleteRequestCategory(VALID_CATEGORY_ID, VALID_USER_ID);
    }

    /**
     * Expects {@link RequestCategoryManager#getAllRequestCategories()} to throw
     * {@link ManagerException} when
     * {@link RequestCategoryDAO#getAllRequestCategories()} throws
     * {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testGetAllRequestCategories_RequestCategoryDAOThrowsDAOException() throws DAOException {
        when(mockRequestCategoryDAO.getAllRequestCategories()).thenThrow(mockDAOException);
        try {
            mockRequestCategoryManager.getAllRequestCategories();
        } catch (final ManagerException e) {
            assertEquals(REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#getAllRequestCategories()} to run
     * successfully when no exception is thrown.
     */
    @Test
    public void testGetAllRequestCategories() throws DAOException {
        when(mockRequestCategoryDAO.getAllRequestCategories()).thenReturn(requestCategories);
        assertSame(requestCategories, mockRequestCategoryManager.getAllRequestCategories());
    }

    /**
     * Expects {@link RequestCategoryManager#getAllRequestCategories(boolean)}
     * to throw {@link ManagerException} when
     * {@link RequestCategoryDAO#getAllRequestCategories(boolean)} throws
     * {@link DAOException}.
     */
    @Test(expected = ManagerException.class)
    public void testGetAllRequestCategories_Boolean_RequestCategoryDAOThrowsDAOException() throws DAOException {
        when(mockRequestCategoryDAO.getAllRequestCategories(Boolean.TRUE)).thenThrow(mockDAOException);
        try {
            mockRequestCategoryManager.getAllRequestCategories(Boolean.TRUE);
        } catch (final ManagerException e) {
            assertEquals(REQUEST_CATEGORY_DB_READ_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects {@link RequestCategoryManager#getAllRequestCategories(boolean)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testGetAllRequestCategories_Boolean() throws DAOException {
        when(mockRequestCategoryDAO.getAllRequestCategories(Boolean.TRUE)).thenReturn(requestCategories);
        assertSame(requestCategories, mockRequestCategoryManager.getAllRequestCategories(Boolean.TRUE));
    }
}