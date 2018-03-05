package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import com.cerner.devcenter.education.admin.DAOException;
import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.RequestCategoryManager;
import com.cerner.devcenter.education.models.RequestCategory;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;

/**
 * Tests the {@link RequestCategoryController} class.
 *
 * @author Vatsal Kesarwani (VK049896)
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestCategoryControllerTest {

    private static final String REDIRECT_LOGIN = "redirect:/login";

    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    private static final String REQUESTED_CATEGORIES_ATTRIBUTE = "requestedCategories";
    private static final String USER_DETAILS_ATTRIBUTE = "userDetails";

    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_USER_ID = "AA012345";
    private static final String VALID_CATEGORY_NAME = "Java";

    private static final int ZERO = 0;
    private static final int NEGATIVE = -1;
    private static final String EMPTY = "";
    private static final String BLANK = " ";

    private static final String NULL_SESSION_EXPECTED_MESSAGE = "Session is null.";
    private static final String NULL_USER_DETAILS_EXPECTED_MESSAGE = "UserDetails is null.";
    private static final String NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE = "RequestCategory object is null.";
    private static final String INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE = "RequestCategory id is zero/negative.";
    private static final String INVALID_USER_ID_EXPECTED_MESSAGE = "UserId is null/empty/blank.";

    private static final String REQUEST_CATEGORY_SUCCESS_MESSAGE = "requestCategory.successMessage";
    private static final String REQUEST_CATEGORY_ERROR_MESSAGE = "requestCategory.errorMessage";

    private static final ResourceBundle I18N_BUNDLE = ResourceBundle.getBundle("i18n", Locale.getDefault());

    private final RequestCategory requestCategory = new RequestCategory();
    private final List<RequestCategory> requestCategories = new ArrayList<RequestCategory>();

    @InjectMocks
    private RequestCategoryController mockRequestCategoryController;
    @Mock
    private RequestCategoryManager mockRequestCategoryManager;
    @Mock
    private ManagerException mockManagerException;
    @Mock
    private HttpSession mockSession;
    @Mock
    private AuthenticationStatusUtil mockAuthenticationStatus;
    @Mock
    private UserProfileDetails mockUserProfileDetails;

    @Before
    public void setUp() throws DAOException {
        requestCategory.setName(VALID_CATEGORY_NAME);
        requestCategories.add(requestCategory);
        when(mockAuthenticationStatus.isLoggedIn()).thenReturn(true);
        when(mockSession.getAttribute(USER_DETAILS_ATTRIBUTE)).thenReturn(mockUserProfileDetails);
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to redirect to login page when user is not logged in.
     */
    @Test
    public void testAddRequestCategory_NotLoggedIn() throws DAOException {
        when(mockAuthenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(REDIRECT_LOGIN,
                mockRequestCategoryController.addRequestCategory(mockSession, requestCategory).getViewName());
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when RequestCategory object is
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullRequestCategory() throws DAOException {
        try {
            mockRequestCategoryController.addRequestCategory(mockSession, null);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_REQUEST_CATEGORY_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when session is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullSession() throws DAOException {
        try {
            mockRequestCategoryController.addRequestCategory(null, requestCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_SESSION_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when UserDetails is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullUserDetails() throws DAOException {
        when(mockSession.getAttribute(USER_DETAILS_ATTRIBUTE)).thenReturn(null);
        try {
            mockRequestCategoryController.addRequestCategory(mockSession, requestCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_USER_DETAILS_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_NullUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(null);
        try {
            mockRequestCategoryController.addRequestCategory(mockSession, requestCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_EmptyUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(EMPTY);
        try {
            mockRequestCategoryController.addRequestCategory(mockSession, requestCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRequestCategory_BlankUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(BLANK);
        try {
            mockRequestCategoryController.addRequestCategory(mockSession, requestCategory);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to catch {@link ManagerException} and verify model object error message
     * when
     * {@link RequestCategoryManager#addRequestCategory(RequestCategory, String)}
     * throws {@link ManagerException}.
     */
    @Test
    public void testAddRequestCategory_ManagerThrowsManagerException() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(VALID_USER_ID);
        doThrow(mockManagerException).when(mockRequestCategoryManager).addRequestCategory(requestCategory,
                VALID_USER_ID);
        final ModelAndView modelAndView = mockRequestCategoryController.addRequestCategory(mockSession,
                requestCategory);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#addRequestCategory(HttpSession, RequestCategory)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testAddRequestCategory() {
        when(mockUserProfileDetails.getUserId()).thenReturn(VALID_USER_ID);
        final ModelAndView modelAndView = mockRequestCategoryController.addRequestCategory(mockSession,
                requestCategory);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE),
                modelAndView.getModel().get(SUCCESS_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to redirect to login page when user is not logged in.
     */
    @Test
    public void testDeleteRequestCategory_NotLoggedIn() throws DAOException {
        when(mockAuthenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(REDIRECT_LOGIN,
                mockRequestCategoryController.deleteRequestCategory(mockSession, VALID_CATEGORY_ID).getViewName());
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_ZeroRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, ZERO);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when requestCategoryId is
     * negative.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NegativeRequestCategoryId() throws DAOException {
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, NEGATIVE);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_REQUEST_CATEGORY_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when session is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NullSession() throws DAOException {
        try {
            mockRequestCategoryController.deleteRequestCategory(null, VALID_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_SESSION_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when UserDetails is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NullUserDetails() throws DAOException {
        when(mockSession.getAttribute(USER_DETAILS_ATTRIBUTE)).thenReturn(null);
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, VALID_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_USER_DETAILS_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when userId is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_NullUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(null);
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, VALID_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when userId is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_EmptyUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(EMPTY);
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, VALID_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to throw {@link IllegalArgumentException} when userId is blank.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteRequestCategory_BlankUserId() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(BLANK);
        try {
            mockRequestCategoryController.deleteRequestCategory(mockSession, VALID_CATEGORY_ID);
        } catch (final IllegalArgumentException e) {
            assertEquals(INVALID_USER_ID_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to catch {@link ManagerException} and verify model object error message
     * when {@link RequestCategoryManager#deleteRequestCategory(int, String)}
     * throws {@link ManagerException}.
     */
    @Test
    public void testDeleteRequestCategory_ManagerThrowsManagerException() throws DAOException {
        when(mockUserProfileDetails.getUserId()).thenReturn(VALID_USER_ID);
        doThrow(mockManagerException).when(mockRequestCategoryManager).deleteRequestCategory(VALID_CATEGORY_ID,
                VALID_USER_ID);
        final ModelAndView modelAndView = mockRequestCategoryController.deleteRequestCategory(mockSession,
                VALID_CATEGORY_ID);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#deleteRequestCategory(HttpSession, int)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testDeleteRequestCategory() {
        when(mockUserProfileDetails.getUserId()).thenReturn(VALID_USER_ID);
        final ModelAndView modelAndView = mockRequestCategoryController.deleteRequestCategory(mockSession,
                VALID_CATEGORY_ID);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE),
                modelAndView.getModel().get(SUCCESS_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession)} to
     * redirect to login page when user is not logged in.
     */
    @Test
    public void testGetAllRequestCategories_NotLoggedIn() throws DAOException {
        when(mockAuthenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(REDIRECT_LOGIN, mockRequestCategoryController.getAllRequestCategories(mockSession).getViewName());
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession)} to
     * throw {@link IllegalArgumentException} when session is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllRequestCategories_NullSession() throws DAOException {
        try {
            mockRequestCategoryController.getAllRequestCategories(null);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_SESSION_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession)} to
     * catch {@link ManagerException} and verify model object error message when
     * {@link RequestCategoryManager#getAllRequestCategories()} throws
     * {@link ManagerException}.
     */
    @Test
    public void testGetAllRequestCategories_ManagerThrowsManagerException() throws DAOException {
        when(mockRequestCategoryManager.getAllRequestCategories()).thenThrow(mockManagerException);
        final ModelAndView modelAndView = mockRequestCategoryController.getAllRequestCategories(mockSession);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession)} to
     * run successfully when no exception is thrown.
     */

    @Test
    public void testGetAllRequestCategories() {
        when(mockRequestCategoryManager.getAllRequestCategories()).thenReturn(requestCategories);
        final ModelAndView modelAndView = mockRequestCategoryController.getAllRequestCategories(mockSession);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE),
                modelAndView.getModel().get(SUCCESS_MESSAGE_ATTRIBUTE));
        assertEquals(requestCategories, modelAndView.getModel().get(REQUESTED_CATEGORIES_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession, boolean)}
     * to redirect to login page when user is not logged in.
     */
    @Test
    public void testGetAllRequestCategories_Boolean_NotLoggedIn() throws DAOException {
        when(mockAuthenticationStatus.isLoggedIn()).thenReturn(false);
        assertEquals(REDIRECT_LOGIN,
                mockRequestCategoryController.getAllRequestCategories(mockSession, Boolean.TRUE).getViewName());
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession, boolean)}
     * to throw {@link IllegalArgumentException} when session is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetAllRequestCategories_Boolean_NullSession() throws DAOException {
        try {
            mockRequestCategoryController.getAllRequestCategories(null, Boolean.TRUE);
        } catch (final IllegalArgumentException e) {
            assertEquals(NULL_SESSION_EXPECTED_MESSAGE, e.getMessage());
            throw e;
        }
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession, boolean)}
     * to catch {@link ManagerException} and verify model object error message
     * when {@link RequestCategoryManager#getAllRequestCategories(boolean)}
     * throws {@link ManagerException}.
     */
    @Test
    public void testGetAllRequestCategories_Boolean_ManagerThrowsManagerException() throws DAOException {
        when(mockRequestCategoryManager.getAllRequestCategories(Boolean.TRUE)).thenThrow(mockManagerException);
        final ModelAndView modelAndView = mockRequestCategoryController.getAllRequestCategories(mockSession,
                Boolean.TRUE);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_ERROR_MESSAGE),
                modelAndView.getModel().get(ERROR_MESSAGE_ATTRIBUTE));
    }

    /**
     * Expects
     * {@link RequestCategoryController#getAllRequestCategories(HttpSession, boolean)}
     * to run successfully when no exception is thrown.
     */
    @Test
    public void testGetAllRequestCategories_Boolean() {
        when(mockRequestCategoryManager.getAllRequestCategories(Boolean.TRUE)).thenReturn(requestCategories);
        final ModelAndView modelAndView = mockRequestCategoryController.getAllRequestCategories(mockSession,
                Boolean.TRUE);
        assertEquals(I18N_BUNDLE.getString(REQUEST_CATEGORY_SUCCESS_MESSAGE),
                modelAndView.getModel().get(SUCCESS_MESSAGE_ATTRIBUTE));
        assertEquals(requestCategories, modelAndView.getModel().get(REQUESTED_CATEGORIES_ATTRIBUTE));
    }
}