package com.cerner.devcenter.education.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.cerner.devcenter.education.admin.ManagerException;
import com.cerner.devcenter.education.managers.UserManager;
import com.cerner.devcenter.education.models.User;
import com.cerner.devcenter.education.user.UserProfileDetails;
import com.cerner.devcenter.education.utils.AuthenticationStatusUtil;
import com.cerner.devcenter.education.utils.AuthorizationLevel;
import com.cerner.devcenter.education.utils.TestConstants;

/**
 * Tests {@link UserController}
 *
 * @author Asim Mohammed (AM045300)
 * @author Anudeep Kumar Gadam (AG045334)
 * @author Jacob Zimmermann (JZ022690)
 * @author Rishabh Bhojak (RB048032)
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String MANAGE_ADMINS = "manage_admins";
    private static final String VALID_USER_ID = "JD009999";
    private static final String PAGE_RESTRICTED_MESSAGE = "restricted_message";
    private static final String MESSAGE = "message";
    private static final String USER_DETAILS = "userDetails";
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "  ";
    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String ADD_USER = "/app/add_user";
    private static final String REMOVE_USER = "/app/remove_user";
    private static final String AUTHORIZATION_LEVEL = "authorizationLevel";
    private static final String USER_ID = "userID";
    private static final String NAME = "MyFirstName, MyLastName";

    private static final String RETRIEVE_USERS_LIST_ERROR = "Error: Unable to retrieve list of available users";
    private static final String INVALID_USER_ID_MESSAGE = "UserId cannot be null, blank, or empty";
    private static final String NULL_USER_ID_MESSAGE = "UserId cannot be null";
    private static final String INVALID_AUTH_LEVEL_MESSAGE = "authLevel must be 0 or 1 or 2";
    private static final String ADD_NULL_USER_MESSAGE = "Cannot add a null user";

    private static final String EXPECTED_FALSE_WHEN_NOT_LOGGED_IN = "Expected removeAdminBasedOnUserId to return false when not logged in.";
    private static final String EXPECTED_FALSE_WHEN_NOT_ADMIN = "Expected removeAdminBasedOnUserId to return false when not an admin.";
    private static final String EXPECTED_TRUE_WITH_ADMIN_AUTH = "Expected userController.changeAuthorizationLevelBaseOnUserId(VALID_USER_ID, ADMIN.getLevel()) to return true.";
    private static final String EXPECTED_TRUE_WITH_INSTRUCTOR_AUTH = "Expected userController.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, INSTRUCTOR.getLevel()) to return true.";
    private static final String EXPECTED_TRUE_WITH_VALID_USER_ID = "Expected userController.removeAdminBasedOnUserId(VALID_USER_ID, session) to return true.";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private UserController userController = new UserController();
    @Mock
    private BindingResult result;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AuthenticationStatusUtil status;
    @Mock
    private UserManager userManager;
    @Mock
    private HttpSession session;
    @Mock
    UserProfileDetails userInfo;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(userController).build();
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setViewResolvers(viewResolver).build();
        when(result.hasErrors()).thenReturn(false);
        userInfo = new UserProfileDetails(NAME, TestConstants.ROLE, TestConstants.USER_ID, TestConstants.EMAIL_ID,
                TestConstants.DEPARTMENT, TestConstants.MANAGER, TestConstants.PROJECT);
        when(status.isLoggedIn()).thenReturn(true);
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        when(session.getAttribute(USER_DETAILS)).thenReturn(userInfo);
    }

    /**
     * Test for
     * {@link UserController#addUser(User, BindingResult, HttpServletRequest)},
     * the test need to return correct view name when valid user information is
     * passed in.
     * 
     * @throws Exception
     */
    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(
                post(ADD_USER).param(USER_ID, VALID_USER_ID)
                        .param(AUTHORIZATION_LEVEL, AuthorizationLevel.ADMIN.toString()))
                .andExpect(view().name(MANAGE_ADMINS));
    }

    /**
     * Test for
     * {@link UserController#addUser(User, BindingResult, HttpServletRequest)}
     * when user object is null.
     */
    @Test
    public void testAddUserWhenUserIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(ADD_NULL_USER_MESSAGE);
        userController.addUser(null, result, request);
    }

    /**
     * Test for
     * {@link UserController#removeUser(User, BindingResult, HttpServletRequest)}
     * , the test need to return correct view name when valid user information
     * is passed in.
     * 
     * @throws Exception
     */
    @Test
    public void testRemoveUser() throws Exception {
        mockMvc.perform(post(REMOVE_USER).param(USER_ID, VALID_USER_ID)).andExpect(view().name(MANAGE_ADMINS));
    }

    /**
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} and
     * expects {@link IllegalArgumentException} when userId is null.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_NullUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_MESSAGE);
        userController.removeAdminBasedOnUserId(null, session);
    }

    /**
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} and
     * expects {@link IllegalArgumentException} when userId is empty.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_EmptyUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_MESSAGE);
        userController.removeAdminBasedOnUserId(EMPTY_STRING, session);
    }

    /**
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} and
     * expects {@link IllegalArgumentException} when userId is empty with white
     * spaces.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_BlankUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_USER_ID_MESSAGE);
        userController.removeAdminBasedOnUserId(BLANK_STRING, session);
    }

    /**
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} when
     * userId is valid.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_ValidUserId() {
        when(userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, AuthorizationLevel.ASSOCIATE.getLevel()))
                .thenReturn(true);
        assertTrue(EXPECTED_TRUE_WITH_VALID_USER_ID, userController.removeAdminBasedOnUserId(VALID_USER_ID, session));
        verify(userManager, times(0)).deleteUser(any(User.class));
        verify(userManager, times(1))
                .changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, AuthorizationLevel.ASSOCIATE.getLevel());

    }

    /***
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} will
     * return false when not logged in.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_NotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        assertFalse(EXPECTED_FALSE_WHEN_NOT_LOGGED_IN, userController.removeAdminBasedOnUserId(VALID_USER_ID, session));
        // never make it to the changeAuthorizationLevelBasedOnUserId call
        verify(userManager, times(0))
                .changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, AuthorizationLevel.ASSOCIATE.getLevel());
    }

    /***
     * Test verifies
     * {@link UserController#removeAdminBasedOnUserId(String, HttpSession)} will
     * return false when not an admin.
     */
    @Test
    public void testRemoveAdminBasedOnUserId_NotAdmin() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        assertFalse(EXPECTED_FALSE_WHEN_NOT_ADMIN, userController.removeAdminBasedOnUserId(VALID_USER_ID, session));
        // never make it to the changeAuthorizationLevelBasedOnUserId call
        verify(userManager, times(0))
                .changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, AuthorizationLevel.ASSOCIATE.getLevel());
    }

    /**
     * Test verifies {@link UserController#showManageAdminsPage(HttpSession)}
     * when user is not admin then redirects to access denied page.
     */
    @Test
    public void testShowManageAdminsPage_UserNotAdmin() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        when(status.redirectsAccessDenied()).thenCallRealMethod();
        assertEquals(PAGE_RESTRICTED_MESSAGE, userController.showManageAdminsPage(session).getViewName());
    }

    /**
     * Test verifies {@link UserController#showManageAdminsPage(HttpSession)}
     * when user is admin then redirects to add/remove user page.
     */
    @Test
    public void testShowManageAdminsPage_UserIsAdmin() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(true);
        assertEquals(MANAGE_ADMINS, userController.showManageAdminsPage(session).getViewName());
    }

    /**
     * Test verifies {@link UserController#showManageAdminsPage(HttpSession)}
     * when session is null expects {@link IllegalArgumentException}.
     */
    @Test
    public void testShowManageAdminsPage_RequestAtrributesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        userController.showManageAdminsPage(null);
    }

    /***
     * Test that {@link UserController#showManageAdminsPage(HttpSession)} will
     * catch a {@link ManagerException} and display an error message to the
     * user.
     */
    @Test
    public void testShowManageAdminsPage_CatchManagerException() {
        when(userManager.getAllUsersWithAuthorizationLevel(AuthorizationLevel.ADMIN)).thenThrow(new ManagerException());
        assertEquals(RETRIEVE_USERS_LIST_ERROR, userController.showManageAdminsPage(session).getModel().get(MESSAGE));
    }

    /***
     * Test that
     * {@link UserController#changeAuthorizationLevelBasedOnUserId(String, int)}
     * will throw {@link IllegalArgumentException} when userId is null.
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserId_nullId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(NULL_USER_ID_MESSAGE);
        userController.changeAuthorizationLevelBasedOnUserId(null, AuthorizationLevel.ADMIN.getLevel());
    }

    /***
     * Test that
     * {@link UserController#changeAuthorizationLevelBasedOnUserId(String, int)}
     * will accept a authLevel of 0 (ADMIN).
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserId_AuthLevelAdmin() {
        final int testLevel = AuthorizationLevel.ADMIN.getLevel();
        when(userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel)).thenReturn(true);
        assertTrue(
                EXPECTED_TRUE_WITH_ADMIN_AUTH,
                userController.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel));
        verify(userManager, times(1)).changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel);
    }

    /***
     * Test that
     * {@link UserController#changeAuthorizationLevelBasedOnUserId(String, int)}
     * will accept a authLevel of 2 (INSTRUCTOR).
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserId_AuthLevelInstructor() {
        final int testLevel = AuthorizationLevel.INSTRUCTOR.getLevel();
        when(userManager.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel)).thenReturn(true);
        assertTrue(
                EXPECTED_TRUE_WITH_INSTRUCTOR_AUTH,
                userController.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel));
        verify(userManager, times(1)).changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, testLevel);
    }

    /***
     * Test that
     * {@link UserController#changeAuthorizationLevelBasedOnUserId(String, int)}
     * will throw {@link IllegalArgumentException} when authLevel is negative.
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserId_AuthLevelNegative() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_AUTH_LEVEL_MESSAGE);
        userController.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, -1);
    }

    /***
     * Test that
     * {@link UserController#changeAuthorizationLevelBasedOnUserId(String, int)}
     * will throw {@link IllegalArgumentException} when authLevel is 3.
     */
    @Test
    public void testChangeAuthorizationLevelBasedOnUserId_AuthLevelThree() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(INVALID_AUTH_LEVEL_MESSAGE);
        userController.changeAuthorizationLevelBasedOnUserId(VALID_USER_ID, 3);
    }

    /***
     * Test verifies
     * {@link UserController#addAdminBasedOnUserId(String, HttpSession)} when
     * user is not logged in.
     */
    @Test
    public void testAddAdminBasedOnUserId_NotLoggedIn() {
        when(status.isLoggedIn()).thenReturn(false);
        ModelAndView actual = userController.addAdminBasedOnUserId(VALID_USER_ID, session);
        assertEquals(LOGIN_REDIRECT, actual.getViewName());
        assertEquals("User is not logged in", actual.getModel().get(MESSAGE));
    }

    /**
     * Test verifies
     * {@link UserController#addAdminBasedOnUserId(String, HttpSession)} when
     * user is not admin then redirects to access denied page.
     */
    @Test
    public void testAddAdminBasedOnUserId_UserNotAdmin() {
        when(userManager.isAdminUser(userInfo.getUserId())).thenReturn(false);
        when(status.redirectsAccessDenied()).thenCallRealMethod();
        assertEquals(
                PAGE_RESTRICTED_MESSAGE,
                userController.addAdminBasedOnUserId(VALID_USER_ID, session).getViewName());
    }
}
